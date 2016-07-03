package br.mafia.server.program;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.mafia.server.musicas.Musica;
import br.mafia.server.musicas.MusicaNaoEncontradaException;
import br.mafia.server.musicas.MusicasController;
import br.mafia.server.rootserver.Download;
import br.mafia.server.rootserver.DownloadsAnnouncer;
import br.mafia.server.rootserver.RootServer;
import br.mafia.server.usuarios.FalhaLoginException;
import br.mafia.server.usuarios.PingChecker;
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
		new Thread(new PingChecker(this)).start();
		new Thread(new DownloadsAnnouncer(this, conf.getFreqatualizawebsocket())).start();
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
	
	public void kick(Usuario usuario) {
		this.logoutUsuario(usuario.getId());
		this.log("RootServer", "Usuário \"" + usuario.getNome() + "\" desconectado por timeout (IP: " + usuario.getIp() + ")");
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
	
	// { RootServer
	
	public int solicitarDownload(int idmusica, Usuario usuario) {
		Musica musica;
		int id = 0;
		try {
			musica = this.getMusica(idmusica);
			id = this.root.solicitarDownload(musica, usuario);
			
			JSONObject root = new JSONObject();
			try {
				root.put("cod", "3");
				root.put("param", "1");
				root.put("id", String.valueOf(id));
				root.put("usuario", usuario.getNome());
				root.put("ip", usuario.getIp());
				root.put("musica", musica.getNome());
				root.put("status", "0");
				root.put("tam", musica.getTam());
				root.put("enviado", "0");
				this.AdminBroadcast(root.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MusicaNaoEncontradaException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		return id;
	}
	
	public void baixarMusica(int id, long offset, Socket socket) {
		this.getDownload(id).baixar(offset, socket);
	}
	
	public Download getDownload(int id) {
		return this.root.getDownload(id);
	}
	
	public void pausarDownload(int id) {
		this.getDownload(id).pausar();
	}
	
	public void cancelarDownload(int id) {
		this.getDownload(id).cancelar();
	}
	
	public void downloadFinalizado(int id) {
		JSONObject root = new JSONObject();
		try {
			root.put("cod", "3");
			root.put("param", "5");
			root.put("id", String.valueOf(id));
			this.AdminBroadcast(root.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void anunciaDownloads() {
		JSONObject root = new JSONObject();
		try {
			root.put("cod", "3");
			root.put("param", "6");
			
			JSONArray jsdownloads = new JSONArray();
			ArrayList<Download> downloads = this.getAllDownloads();
			for(int i = 0; i < downloads.size(); i++) {
				Download atual = downloads.get(i);
				if(atual.getStatus() == 0) {
					JSONObject jsdownload = new JSONObject();
					jsdownload.put("id", String.valueOf(atual.getId()));
					
					long uleitura = atual.getUltimaLeitura();
					long enviado = atual.getEnviado();
					
					long envtempo = enviado - uleitura;
					
					int taxaup = (int) (envtempo / ((double)this.conf.getFreqatualizawebsocket() / 1000));
					
					jsdownload.put("enviado", String.valueOf(enviado));
					jsdownload.put("taxa", String.valueOf(taxaup));
					jsdownloads.put(jsdownload);
				}
			}
			root.put("downloads", jsdownloads);
			
			this.AdminBroadcast(root.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Download> getAllDownloads() {
		return this.root.getAllDownloads();
	}
	
	// } Fim RootServer
	
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
