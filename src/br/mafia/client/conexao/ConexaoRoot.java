package br.mafia.client.conexao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import br.mafia.client.gui.Cliente;
import br.mafia.client.musicas.Musica;

public class ConexaoRoot {
	private Cliente cliente;
	private Socket socket;
	private InputStream entrada;
	private OutputStream saida;
	
	public ConexaoRoot(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public void cadastro(String usuario, String senha) throws UsuarioJaCadastradoException {
		try {
			this.socket = new Socket(this.cliente.getIPservidor(), Integer.parseInt(this.cliente.getPortaServidor()));
			this.saida = this.socket.getOutputStream();
			this.entrada = this.socket.getInputStream();
			
			this.saida.write(0); //cod de cadastro ("0000 0000")
			this.saida.write(usuario.length());
			this.saida.write(usuario.getBytes());
			this.saida.write(senha.length());
			this.saida.write(senha.getBytes());
			int r = entrada.read();
			this.socket.close();
			this.saida.close();
			this.entrada.close();
			if(r != 0) {
				throw new UsuarioJaCadastradoException();
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void login(String usuario, String senha) throws FalhaLoginException {
		try {
			this.socket = new Socket(this.cliente.getIPservidor(), Integer.parseInt(this.cliente.getPortaServidor()));
			this.saida = this.socket.getOutputStream();
			this.entrada = this.socket.getInputStream();
			
			saida.write(1 << 4); //cod de login ("0001 0000")
			saida.write(usuario.length());
			saida.write(usuario.getBytes());
			saida.write(senha.length());
			saida.write(senha.getBytes());
			int r = entrada.read();
			if(r != 16) {
				throw new FalhaLoginException();
			}
			new Thread(new Ping(this.socket)).start();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void logout() {
		try {
			this.saida.write(64);
			this.socket.close();
			this.entrada.close();
			this.saida.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Musica> procuraMusica(String busca, int selecao) { //seleção 32 -> nome da música | 33 -> nome do artista
		ArrayList<Musica> musicas = new ArrayList<Musica>();
		try {
			this.saida.write(selecao);
			byte[] bbusca = busca.getBytes("UTF-8");
			saida.write(bbusca.length);
			saida.write(bbusca);
			int res = entrada.read();
			if(res == 32) {
				
			}
			int qtdmusicas = entrada.read() << 8; //captura primeiro byte
			qtdmusicas = qtdmusicas | entrada.read(); //captura segundo byte
			
			int idmusica, duracao, tamstr1, tamstr2, tamstr3;
			long tam = 0L;
			String nome, artista, path;
			
			for(int i = 0; i < qtdmusicas; i++) {
				idmusica = entrada.read() << 8;
				idmusica = idmusica | entrada.read();
				
				tam = entrada.read() << 24;
				tam = tam | (entrada.read() << 16);
				tam = tam | (entrada.read() << 8);
				tam = tam | entrada.read();
				
				duracao = entrada.read() << 8;
				duracao = duracao | entrada.read();
				
				tamstr1 = entrada.read();
				byte[] bnome = new byte[tamstr1];
				entrada.read(bnome);
				nome = new String(bnome, "UTF-8");
				
				tamstr2 = entrada.read();
				byte[] bartista = new byte[tamstr2];
				entrada.read(bartista);
				artista = new String(bartista, "UTF-8");
				
				tamstr3 = entrada.read();
				byte[] bpath = new byte[tamstr3];
				entrada.read(bpath);
				path = new String(bpath, "UTF-8");
				
				musicas.add(new Musica(idmusica, nome, artista, duracao, path, tam));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return musicas;
	}
}
