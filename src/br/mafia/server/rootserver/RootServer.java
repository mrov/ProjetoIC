package br.mafia.server.rootserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import br.mafia.server.program.Server;
import br.mafia.server.util.Config;

public class RootServer extends Thread {
	private Config conf;
	private Server server; //fachada
	private ServerSocket servidor;
	
	public RootServer(Config conf, Server server) {
		this.conf = conf;
		this.server = server;
		if(this.server.semMusicas()) {
			this.server.buscaNaPasta();
		}
		
		//implementação do servidor, a porta já está no atributo conf, esta thread não pode ser bloqueada
	}
	
	public void run() {
		try {
			this.servidor = new ServerSocket(this.conf.getPortaprincipal());
			while(true) {
				Socket c = this.servidor.accept();
				Conexao con = new Conexao(this.server, this.conf, c);
				new Thread(con).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
