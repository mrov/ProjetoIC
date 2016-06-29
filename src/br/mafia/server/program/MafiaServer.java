package br.mafia.server.program;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import br.mafia.server.usuarios.FalhaLoginException;
import br.mafia.server.usuarios.Usuario;
import br.mafia.server.usuarios.UsuarioJaCadastradoException;
import br.mafia.server.util.ArquivoConfigNaoEncontradoException;
import br.mafia.server.util.Config;

public class MafiaServer {
	public static void main(String[] args) {
		Server server; //fachada
		boolean tentainiciar = true;
		while(tentainiciar) {
			try { //se tudo der certo a aplicação continua por aqui...
				server = new Server();
				tentainiciar = false;
				
				Scanner t = new Scanner(System.in);
				int x = t.nextInt();
				try {
					Usuario u = server.loginUsuario("ok", "blabla", "kkk");
					x = t.nextInt();
					server.logoutUsuario(1);
					x = t.nextInt();
					server.log("RootServer", "test");
					x = t.nextInt();
					try {
						server.cadastrarUsuario("ok", "blabla", "123");
					} catch (UsuarioJaCadastradoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (FalhaLoginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (ArquivoConfigNaoEncontradoException e) {
				System.out.println("Arquivo de configurações não foi encontrado em \"" + e.getDir() + "\"");
				System.out.println("Tentando criar arquivo...");
				try {
					Config.geraArquivoConfig(e.getDir());
					System.out.println("Arquivo de configuração criado!");
				} catch (FileNotFoundException | UnsupportedEncodingException e1) {
					System.out.println("Não foi possível criar arquivo de configuração, verifique suas permissões\nEncerrando execução");
					tentainiciar = false;
				}
			}
		}
	}
}
