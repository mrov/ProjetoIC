package br.mafia.server.rootserver;

import br.mafia.server.program.Server;

public class DownloadsAnnouncer extends Thread {
	private Server server;
	int freq;
	
	public DownloadsAnnouncer(Server server, int freq) {
		this.server = server;
		this.freq = freq;
	}
	
	public  void run() {
		while(true) {
			try {
				Thread.sleep(this.freq);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.server.anunciaDownloads();
		}
	}
}
