package br.mafia.server.rootserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

import br.mafia.server.musicas.Musica;
import br.mafia.server.program.Server;
import br.mafia.server.usuarios.Usuario;
import br.mafia.server.util.Config;

public class Download {
	private int id;
	private Musica musica;
	private Usuario usuario;
	private long enviado;
	private long ultimaleitura;
	private int status; //0 -> enviando | 1 -> pausado | 2 -> cancelado | 3 -> problema | 4 -> concluído
	public static int nextid = 1;
	private Config conf;
	private Server server;
	
	public Download(Musica musica, Usuario usuario, Config conf, Server server) {
		this.id = Download.nextid++;
		this.musica = musica;
		this.usuario = usuario;
		this.enviado = 0;
		this.ultimaleitura = 0;
		this.status = 0;
		this.conf = conf;
		this.server = server;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Usuario getUsuario() {
		return this.usuario;
	}
	
	public Musica getMusica() {
		return this.musica;
	}
	
	public long getEnviado() {
		this.ultimaleitura = this.enviado;
		return this.enviado;
	}
	
	public long getUltimaLeitura() {
		return this.ultimaleitura;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public void pausar() {
		this.status = 1;
	}
	
	public void cancelar() {
		this.status = 2;
	}
	
	public void baixar(long inicio, Socket socket) {
		this.enviado = inicio;
		this.status = 0;
		File f = new File(this.conf.getPastamusicas() + File.separator + this.musica.getPath());
		try {
			RandomAccessFile r = new RandomAccessFile(f, "r");
			try {
				r.seek(inicio);
				byte[] bytes = new byte[16 * 1024];
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				int count;
				while ((count =r.read(bytes)) > 0) {
		            out.write(bytes, 0, count);
		            this.enviado += count;
		        }
				this.status = 4;
				this.server.downloadFinalizado(this.id);
				r.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				try {//aguarda um pouco por algum comando do cliente, se ele não avisar que pausou ou cancelou o download é sinal que deu merda
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(this.status == 0) {
					this.status = 3;
					this.server.downloadProblema(this.id);
				}
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
