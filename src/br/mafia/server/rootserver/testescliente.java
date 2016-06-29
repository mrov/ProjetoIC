package br.mafia.server.rootserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class testescliente { //arquivos de testes do cliente
	public static void main(String[] args) { 
		cadastro();
	}
	
	public static void cadastro() {
		try {
			Socket s = new Socket("localhost", 8080);
			OutputStream saida = s.getOutputStream();
			InputStream entrada = s.getInputStream();
			
			
			String usuario = "opa";
			String senha = "ok";
			saida.write(0); //cod de cadastro ("0000 0000")
			saida.write(usuario.length());
			saida.write(usuario.getBytes());
			saida.write(senha.length());
			saida.write(senha.getBytes());
			int r = entrada.read();
			if(r == 0) {
				System.out.println("Cadastrado com sucesso");
			} else {
				System.out.println("Usuario já cadastrado");
			}
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
