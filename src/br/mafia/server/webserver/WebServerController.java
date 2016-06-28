package br.mafia.server.webserver;

import br.mafia.server.util.Config;

public class WebServerController {
	private Config conf;
	private WebServer web;
	
	public WebServerController(Config conf) {
		this.conf = conf;
		this.web = new WebServer(this.conf.getPortawebserver(), this.conf.getRootwebserver(), this.conf.getUserwebserver(), this.conf.getPasswordwebserver(), this.conf.getPastamusicas(), this.conf.getPortawebsocketserver());
		new Thread(this.web).start();
	}
}
