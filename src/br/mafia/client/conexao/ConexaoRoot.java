package br.mafia.client.conexao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import br.mafia.client.gui.Cliente;

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
}
