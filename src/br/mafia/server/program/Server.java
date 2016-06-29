package br.mafia.server.program;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import br.mafia.server.musicas.Musica;
import br.mafia.server.musicas.MusicaNaoEncontradaException;
import br.mafia.server.musicas.MusicasController;
import br.mafia.server.rootserver.RootServer;
import br.mafia.server.usuarios.FalhaLoginException;
import br.mafia.server.usuarios.Usuario;
import br.mafia.server.usuarios.UsuarioJaCadastradoException;
import br.mafia.server.usuarios.UsuariosController;
import br.mafia.server.util.ArquivoConfigNaoEncontradoException;
import br.mafia.server.util.Config;
import br.mafia.server.webserver.WebServerController;
import br.mafia.server.websocketserver.WebSocketServer;

public class Server {
	private Config conf;
	private MusicasController musicas;
	private RootServer root;
	private WebServerController webserver;
	private WebSocketServer websocketserver;
	private UsuariosController usuarios;
	
	public Server() throws ArquivoConfigNaoEncontradoException {
		this.conf = new Config("mafia.conf"); //procura o arquivo de configuração mafia.conf no diretório do programa
		this.musicas = new MusicasController(this.conf.getArquivomusicas()); //cria um novo controlador com a lista de músicas do arquivo xml
		this.root = new RootServer(this.conf, this);
		new Thread(this.root).start();
		this.webserver = new WebServerController(this.conf);
		try {
			this.websocketserver = new WebSocketServer(this.conf, this);
			new Thread(this.websocketserver).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.usuarios = new UsuariosController(this.conf.getArquivousuarios());
	}
	
	// { Músicas
	
	public void addMusica(String nome, String artista, int duracao, String path, int tam) {
		this.musicas.addMusica(nome, artista, duracao, path, tam);
	}
	
	public Musica getMusica(int id) throws MusicaNaoEncontradaException {
		return this.musicas.getMusica(id);
	}
	
	public void deletarMusica(int id) { //esta função não deleta a música do disto, apenas do XML!!
		this.musicas.deletarMusica(id);
	}
	
	public void buscaNaPasta() {
		this.musicas.buscaNaPasta(this.conf.getPastamusicas());
	}
	
	public boolean semMusicas() {
		return this.musicas.repVazio();
	}
	
	public ArrayList<Musica> procurarMusica(int tipo, String busca) {
		return this.musicas.procuraMusica(tipo, busca);
	}
	
	// } Fim músicas
	
	
	// { Usuários
	
	public void cadastrarUsuario(String nome, String senha, String ip) throws UsuarioJaCadastradoException {
		Usuario u = this.usuarios.cadastrarUsuario(nome, senha, ip);
		JSONObject root = new JSONObject();
		try {
			root.put("cod", "6");
			root.put("id", String.valueOf(u.getId()));
			root.put("nome", nome);
			root.put("ip", ip);
			this.AdminBroadcast(root.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Usuario loginUsuario(String nome, String senha, String ip) throws FalhaLoginException {
		Usuario u = this.usuarios.login(nome, senha, ip);
		JSONObject root = new JSONObject();
		try {
			root.put("cod", "5");
			root.put("param", "1");
			JSONObject usuario = new JSONObject();
			usuario.put("id", String.valueOf(u.getId()));
			usuario.put("ip", ip);
			root.put("usuario", usuario);
			this.AdminBroadcast(root.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;
	}
	
	public void logoutUsuario(int id) {
		Usuario u = this.usuarios.getUsuario(id);
		u.setOnline(false);
		JSONObject root = new JSONObject();
		try {
			root.put("cod", "5");
			root.put("param", "2");
			JSONObject usuario = new JSONObject();
			usuario.put("id", String.valueOf(u.getId()));
			root.put("usuario", usuario);
			this.AdminBroadcast(root.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Usuario> getAllUsers() {
		return this.usuarios.getAll();
	}
	
	// } Fim Usuários
	
	
	// { WebSocketServer / admin
	
	private void AdminBroadcast(String msg) {
		this.websocketserver.Broadcast(msg);
	}
	
	// } Fim WebSocketServer
	
	
	// { Configurações
	
	public void geraArquivoConfig(String caminho) throws FileNotFoundException, UnsupportedEncodingException { //chamado se o arquivo de configuração não existir
		this.conf.geraArquivoConfig(caminho);
	}
	
	public void log(String quem, String msg) {
		JSONObject root = new JSONObject();
		try {
			root.put("cod", "4");
			root.put("quem", quem);
			root.put("msg", msg);
			this.AdminBroadcast(root.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	// } Fim configurações
}
