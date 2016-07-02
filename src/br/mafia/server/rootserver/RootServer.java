package br.mafia.server.rootserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import br.mafia.server.musicas.MusicaNaoEncontradaException;
import br.mafia.server.program.Server;
import br.mafia.server.usuarios.Usuario;
import br.mafia.server.util.Config;

public class RootServer extends Thread {
	private Config conf;
	private Server server; //fachada
	private ServerSocket servidor;
	private ArrayList<Download> downloads;
	
	public RootServer(Config conf, Server server) {
		this.conf = conf;
		this.server = server;
		this.downloads = new ArrayList();
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
	
	public int solicitarDownload(int idmusica, Usuario usuario) {
		try {
			Download d = new Download(this.server.getMusica(idmusica), usuario, this.conf);
			this.downloads.add(d);
			return d.getId();
		} catch (MusicaNaoEncontradaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public Download getDownload(int id) {
		for(int i = 0; i < this.downloads.size(); i++) {
			if(this.downloads.get(i).getId() == id) return this.downloads.get(i);
		}
		return null;
	}
	
	public ArrayList<Download> getAllDownloads() {
		return this.downloads;
	}
}
