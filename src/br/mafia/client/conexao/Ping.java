package br.mafia.client.conexao;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Ping extends Thread {
	private Socket socket;
	private OutputStream saida;
	
	public Ping(Socket socket) {
		try {
			this.socket = socket;
			this.saida = this.socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(!this.socket.isClosed()) {
			try {
				this.saida.write(80);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
