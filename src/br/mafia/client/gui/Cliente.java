package br.mafia.client.gui;

import java.util.ArrayList;

import br.mafia.client.conexao.ConexaoRoot;
import br.mafia.client.conexao.FalhaLoginException;
import br.mafia.client.conexao.UsuarioJaCadastradoException;
import br.mafia.client.musicas.Musica;
import br.mafia.client.musicas.MusicasController;
import br.mafia.client.util.Config;

public class Cliente {
	private Config conf;
	private MusicasController musicas;
	private ConexaoRoot root;
	
	public Cliente() {
		this.conf = new Config("mafia.conf");
		this.musicas = new MusicasController(this.conf.getArquivoMusicas());
		this.root = new ConexaoRoot(this);
	}
	
	// { <ROOT>
	
	public void cadastrar(String usuario, String senha) throws UsuarioJaCadastradoException {
		this.root.cadastro(usuario, senha);
	}
	
	public void login(String usuario, String senha) throws FalhaLoginException {
		this.root.login(usuario, senha);
	}
	
	public void logout() {
		this.root.logout();
	}
	
	// } </ROOT>
	
	// { <MUSICAS>
	
	public ArrayList<Musica> getMusicasDisponiveis() {
		return this.musicas.getMusicasBaixadas();
	}
	
	public void addMusica(int id, String nome, String artista, int duracao, String path, int tam) {
		this.musicas.addMusica(id, nome, artista, duracao, path, tam);
	}
	
	// } </MUSICAS>
	
	// { <CONFIG>
	
	public String getPastaMusicas() {
		return this.conf.getPastaMusicas();
	}
	
	public String getArquivoMuscas() {
		return this.conf.getArquivoMusicas();
	}
	
	public String getIPservidor() {
		return this.conf.getIPservidor();
	}
	
	public String getPortaServidor() {
		return this.conf.getPortaServidor();
	}
	
	public void setIPservidor(String ip) {
		this.conf.setIPservidor(ip);
	}
	
	public void setPortaServidor(String porta) {
		this.conf.setPortaServidor(porta);
	}
	
	public void SalvarConfig() {
		this.conf.salvaConfig("mafia.conf");
	}
	
	// } </CONFIG>
}
