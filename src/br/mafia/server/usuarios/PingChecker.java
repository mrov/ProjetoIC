package br.mafia.server.usuarios;

import java.util.ArrayList;

import br.mafia.server.program.Server;

public class PingChecker extends Thread {
	private Server server;
	
	public PingChecker(Server server) {
		this.server = server;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long hora = System.currentTimeMillis() / 1000;
			ArrayList<Usuario> usuarios = this.server.getAllUsers();
			for(int i = 0; i < usuarios.size(); i++) {
				Usuario atual = usuarios.get(i);
				if(atual.getOnline()) {
					if(hora - atual.getHeartBeat() > 40) this.server.kick(atual);
				}
			}
		}
	}
}
