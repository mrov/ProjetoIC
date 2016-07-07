package br.mafia.server.rootserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import br.mafia.server.musicas.Musica;
import br.mafia.server.program.Server;
import br.mafia.server.usuarios.FalhaLoginException;
import br.mafia.server.usuarios.Usuario;
import br.mafia.server.usuarios.UsuarioJaCadastradoException;
import br.mafia.server.util.Config;

public class Conexao extends Thread {
	private Server server;
	private Config conf;
	private Usuario user;
	private Socket socket;
	private InputStream entrada;
	private DataOutputStream saida;
	
	public Conexao(Server server, Config conf, Socket socket) {
		this.server = server;
		this.conf = conf;
		this.user = null;
		this.socket = socket;
		try {
			this.entrada = this.socket.getInputStream();
			this.saida = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(this.socket.isConnected()) {
			try {
				int b = this.entrada.read();
				int op = b >> 4;
				int par = b & 15;
				switch(op) {
				case 0:
					this.cadastro();
					return;
				case 1:
					this.login();
					break;
				case 2:
					this.procuramusicas(par);
					break;
				case 3:
					switch(par) {
					case 0:
						this.solicitarDownload();
						break;
					case 1:
						this.iniciarDownload();
						break;
					case 2:
						this.pausarDownload();
						break;
					case 3:
						this.cancelarDownload();
						break;
					}
					break;
				case 4:
					this.close();
					break;
				case 5:
					this.user.ping();
					break;
				}
			} catch (IOException e) {
				if(!this.socket.isClosed()) this.close();
			}
		}
	}
	
	public void cadastro() {
		try {
			int b = this.entrada.read(); //tam da string do usuário
			byte[] user = new byte[b];
			this.entrada.read(user);
			String nome= new String(user);
			b = this.entrada.read();
			byte[] pass = new byte[b];
			this.entrada.read(pass);
			String senha = new String(pass);
			String ip = this.socket.getRemoteSocketAddress().toString();
			try {
				this.server.cadastrarUsuario(nome, senha, ip.substring(1, ip.lastIndexOf(":")));
				this.saida.write(0);
			} catch (UsuarioJaCadastradoException e) {
				this.saida.write(1);
			} finally {
				this.socket.close();
			}
		} catch (IOException e) {
			try {
				this.socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void login() {
		try {
			int b = this.entrada.read(); //tam da string do usuário
			byte[] user = new byte[b];
			this.entrada.read(user);
			String nome= new String(user);
			b = this.entrada.read();
			byte[] pass = new byte[b];
			this.entrada.read(pass);
			String senha = new String(pass);
			String ip = this.socket.getRemoteSocketAddress().toString();
			try {
				this.user = this.server.loginUsuario(nome, senha, ip.substring(1, ip.lastIndexOf(":")));
				this.saida.write(16);
			} catch (FalhaLoginException e) {
				this.saida.write(17);
				this.socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void procuramusicas(int param) {
		int b;
		try {
			b = this.entrada.read(); //tam da string de busca
			byte[] busca = new byte[b];
			this.entrada.read(busca);
			String pesquisa = new String(busca);
			
			ArrayList<Musica> musicas = this.server.procurarMusica(param, pesquisa);
			int qtdmusicas = musicas.size();
			this.saida.write(32);
			this.saida.write(qtdmusicas >> 8);
			this.saida.write(qtdmusicas & 255);
			
			Musica atual;
			int id;
			long tam;
			int dur;
			String nome, artista, path;
			
			for(int i = 0; i < qtdmusicas; i++) {
				atual = musicas.get(i);
				id = atual.getId();
				
				this.saida.write(id >> 8); //escreve primeiro byte do ID
				this.saida.write(id & 255); //escreve segundo byte do ID
				
				tam = atual.getTam();
				this.saida.write((int)(tam >> 24)); //escreve primeiro byte do tam
				this.saida.write((int)((tam >> 16) & 255)); //escreve segundo byte do tam
				this.saida.write((int)((tam >> 8) & 255)); //escreve terceiro byte do tam
				this.saida.write((int)(tam & 255)); //escreve quarto byte do tam
				
				dur = atual.getDuracao();
				this.saida.write(dur >> 8); //escreve primeiro byte da duração
				this.saida.write(dur & 255); //escreve segundo byte da duração
				
				nome = atual.getNome();
				byte[] bnome = nome.getBytes("UTF-8");
				this.saida.write(bnome.length); //escreve tamanho da string de nome da música
				this.saida.write(bnome); //escreve nome da música
				
				artista = atual.getArtista();
				byte[] bartista = artista.getBytes("UTF-8");
				this.saida.write(bartista.length); //escreve tamanho da string de nome do artista
				this.saida.write(bartista); //escreve nome do artista
				
				path = atual.getPath();
				byte[] bpath = path.getBytes("UTF-8");
				this.saida.write(bpath.length); //escreve tamanho da string do "caminho" da música
				this.saida.write(bpath); //escreve "caminho" da música
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void solicitarDownload() {
		try {
			int idmusica = this.entrada.read() << 8;
			idmusica = idmusica | this.entrada.read();
			
			int iddownload = this.server.solicitarDownload(idmusica, this.user);
			this.saida.write(iddownload >> 8);
			this.saida.write(iddownload & 255);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void iniciarDownload() {
		try {
			int iddownload = this.entrada.read() << 8;
			iddownload = iddownload | this.entrada.read();
			
			long offset = this.entrada.read() << 24;
			offset = offset | (this.entrada.read() << 16);
			offset = offset | (this.entrada.read() << 8);
			offset = offset | (this.entrada.read()); System.out.println("iniciando upload em: " + offset);
			
			this.server.baixarMusica(iddownload, offset, this.socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void pausarDownload() {
		try {
			int iddownload = this.entrada.read() << 8;
			iddownload = iddownload | this.entrada.read();
			this.server.pausarDownload(iddownload);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cancelarDownload() {
		try {
			int iddownload = this.entrada.read() << 8;
			iddownload = iddownload | this.entrada.read();
			this.server.cancelarDownload(iddownload);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		this.server.logoutUsuario(this.user.getId());
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
