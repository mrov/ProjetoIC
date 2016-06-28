package br.mafia.server.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer extends Thread {
	private int porta;
	private String pasta;
	private String userweb;
	private String passweb;
	private boolean roda;
	private ServerSocket server;
	private String pastamusicas;
	int portawebsocket;
	
	public WebServer(int porta, String pasta, String user, String pass, String pastamusicas,  int portawebsocket) {
		this.porta = porta;
		this.pasta = pasta;
		this.userweb = user;
		this.passweb = pass;
		this.portawebsocket = portawebsocket;
		this.pastamusicas = pastamusicas;
	}
	
	public void run() {
		this.roda = true;
		try {
			this.server = new ServerSocket(porta);
			while(roda) {
				Socket requisicao = this.server.accept();
				new Thread(new RequisicaoWeb(requisicao, this.pasta, this.userweb, this.passweb, this.pastamusicas, this.portawebsocket)).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finalizar() throws IOException {
		this.roda = false;
		this.server.close();
	}
}
