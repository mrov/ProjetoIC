package br.mafia.server.rootserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class testescliente { //arquivos de testes do cliente
	public static void main(String[] args) { 
		//cadastro();
		login();
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
	
	public static void login() {
		try {
			Socket s = new Socket("localhost", 8080);
			OutputStream saida = s.getOutputStream();
			InputStream entrada = s.getInputStream();
			
			
			String usuario = "ok";
			String senha = "blabla";
			saida.write(1 << 4); //cod de login ("0001 0000")
			saida.write(usuario.length());
			saida.write(usuario.getBytes());
			saida.write(senha.length());
			saida.write(senha.getBytes());
			int r = entrada.read();
			if(r == 0) {
				System.out.println("Logado\n");
				//saida.write(4 << 4); //cod de logout (0100 0000)
				
				/* INÍCIO DA LISTAGEM DE MÚSICAS !!! */
				System.out.println("Listando músicas\n");
				
				saida.write(32); //cod de solicitação de lista de músicas por nome (0010 0000) = 32
				//saida.write(33); //cod de solicitação de lista de músicas por artista (0010 0001) = 33
				String busca = "1999";
				byte[] bbusca = busca.getBytes("UTF-8");
				saida.write(bbusca.length); //envia tamanho da string de busca
				saida.write(bbusca); //envia string de busca
				
				int res = entrada.read();
				
				if(res == 32) { //início da lista, esse teste na verdade nem precisa ser feito, o retorno sempre é 32..
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
						
						System.out.println("nome: " + nome + " artista: " + artista + " tam (bytes): " + tam + " duracao (seg): " + duracao + " path: " + path);
					}
				}
				
				/* FIM DA LISTAGEM DE MÚSICAS */
				
				
			} else {
				System.out.println("Login ou senha incorretos");
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
