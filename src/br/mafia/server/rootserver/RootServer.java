package br.mafia.server.rootserver;

import br.mafia.server.program.Server;
import br.mafia.server.util.Config;

public class RootServer {
	Config conf;
	Server server; //fachada
	
	public RootServer(Config conf, Server server) {
		this.conf = conf;
		this.server = server;
		if(this.server.semMusicas()) {
			this.server.buscaNaPasta();
		}
		
		//implementação do servidor, a porta já está no atributo conf, esta thread não pode ser bloqueada
	}
}
