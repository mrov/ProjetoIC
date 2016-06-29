package br.mafia.server.program;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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
				int par = b & 127;
				switch(op) {
				case 0:
					this.cadastro();
					return;
				case 1:
					this.login();
					break;
				case 4:
					this.close();
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
			int b = this.entrada.read();
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
				this.saida.write(0);
			} catch (FalhaLoginException e) {
				this.saida.write(1);
				this.socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //tam da string do usuário
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
