package br.mafia.server.websocketserver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.mafia.server.musicas.Musica;
import br.mafia.server.program.Server;
import br.mafia.server.rootserver.Download;
import br.mafia.server.usuarios.Usuario;
import br.mafia.server.util.EncodingUtil;

public class WebSocket {
	private Socket socket;
	private ByteArrayOutputStream buffer;
	private InputStream in;
	private DataOutputStream out;
	private Server server;
	private int heartbet;
	
	public WebSocket(Socket socket, Server server) throws IOException {
		this.socket = socket;
		this.in = socket.getInputStream();
		this.buffer = new ByteArrayOutputStream();
		this.out = new DataOutputStream(socket.getOutputStream());
		this.server = server;
		this.enviaListaMusicas();
	}
	
	public String readMsg() throws IOException {
		long contlido = 0L;
		long conttam = -1L;
		boolean fin = false;
		int rsv = 0;
		int opcode = 0;
		boolean mask = false;
		long mascara = 0L;
		while(true) {
			int b = this.in.read();
			if(conttam == -1) {
				mask = fin = false;
				mascara = 0;
				contlido = conttam = rsv = opcode = 0; //zera todas as variáveis de controle
				
				fin = ((b & (1 << 7)) != 0) ? true : false; //primeiro bit do datagrama indica se é o ultimo pacote da mensagem
				rsv = (b >> 4) & 3; //reservado para o futuro
				opcode = b & 15; //cod que indica o tipo de conteúdo
				
				b = this.in.read(); //lê segundo byte do datagrama websocket
				mask = (((b >> 7) & 1) == 1) ? true : false; //verifica se o browser ativou a mascara
				conttam = b & 127;	//System.out.println("tam: " + conttam);
				if(conttam > 125) conttam = this.capturaTam(conttam);
				if(mask) {
					mascara = this.capturaMascara();
				}
			} else {
				if(mask) {
					b = this.decode(b, mascara, contlido);
				}//if(fin) System.out.println("msg final :D " + b);
				buffer.write(b);
				contlido++;
				if(conttam == contlido) {
					conttam = -1;
					if(fin) {
						fin = false;
						String s = new String(this.buffer.toByteArray(), "UTF-8");
						this.buffer.reset();
						if(s.equals("opa")) this.sendMsg("abc");
						return s;
					}
				}
			}
		}
	}
	
	private long capturaMascara() throws IOException {
		long mascara = 0L;
		long b;
		for(int i = 0; i < 4; i++) { //lê 8 bytes da mascara e atribui a um inteiro
			b = this.in.read();
			mascara = mascara | (b << ((3 - i) * 8));
		}
		return mascara;
	}
	
	private int decode(int b, long mascara, long contlido) {
		long bitslidos = contlido * 8;
		int decodificado = 0;
		int mask = 0;
		for(int i = 0; i < 8; i++) {
			int posmask = (int) (bitslidos++ % 32);
			mask = mask | (((int) mascara >> (31 - posmask)) & 1) << (7 - i);
		}
		decodificado = b ^ mask;
		return decodificado;
	}
	
	private long capturaTam(long len) throws IOException { //busca tamanho do datagrama websocket
		long tam = 0L;
		int b;
		if(len == 126) { //126 -> o tamanho cabe em 16 bits, captura os próximos 16
			for(int i = 0; i < 2; i++) {
				b = this.in.read();
				tam = tam | (b << ((1 - i) * 8));
			}
		} else if(len == 127) { //127 -> o tamanho cabe em 64 bits, captura os próximos 64
			for(int i = 0; i < 8; i++) {
				b = this.in.read();
				tam = tam | (b << (7 - i));
			}
		}
		return tam;
	}
	
	public void sendMsg(String msg) throws IOException {
		msg = EncodingUtil.encodeURIComponent(msg);
		this.send(true, 0, 1, true, msg);
	}
	
	private void send(boolean fin, int rsv, int opcode, boolean mask, String msg) throws IOException {
		int b = 0;
		b = b | ((fin ? 1 : 0) << 7);
		b = b | (rsv << 4);
		b = b | opcode; mask = false; //chrome... 
		out.write(b);
		b = 0 | ((mask ? 1 : 0) << 7); //indica o status da mascara
		long tam = msg.length();
		if(tam < 126) { //o tamanho cabe em 7 bits
			b = b | (int)tam;
			out.write(b);
		} else if(tam < 65537) { //o tamanho cabe em apenas 16 bits
			b = b | 126;
			out.write(b);
			b = (int)((tam >> 8) & 255);
			out.write(b);
			b = (int)(tam & 255);
			out.write(b);
		} else { //o tamanho MUITO provavelmente cabe em 64 bits
			b = b | 127;
			out.write(b);
			b = (int)((tam >> 56) & 255);
			out.write(b);
			b = (int)((tam >> 48) & 255);
			out.write(b);
			b = (int)((tam >> 40) & 255);
			out.write(b);
			b = (int)((tam >> 32) & 255);
			out.write(b);
			b = (int)((tam >> 24) & 255);
			out.write(b);
			b = (int)((tam >> 16) & 255);
			out.write(b);
			b = (int)((tam >> 8) & 255);
			out.write(b);
			b = (int)(tam & 255);
			out.write(b);
		}
		
		//gera mascara de 32 bits aleatória
		Random gerador = new Random();
		long mascara = 0L;
		if(mask) {
			for(int i = 0; i < 4; i++) { //gera 4 bytes = 32 bits
				long random = gerador.nextInt(256);
				mascara = mascara | (random << ((3 - i) * 8));
			}
		}
		//envia mascara
		if(mask) {
			for(int i = 0; i < 4; i++) {
				b = (int)((mascara >> ((3 - i) * 8)) & 255);
				out.write(b);
			}
		}
		
		//envia mensagem
		long bytesenviados = 0;
		for(int i = 0; i < msg.length(); i++) {
			b = msg.charAt(i);
			if(mask) { //mascara a mensagem
				b = this.encode(b, mascara, bytesenviados);
			}
			bytesenviados++;
			out.write(b);
		}
	}
	
	private int encode(int b, long mascara, long contlido) {
		long bitsenviados = contlido * 8;
		int mask = 0;
		for(int i = 0; i < 8; i++) {
			int posmask = (int) (bitsenviados++ % 32);
			mask = mask | (((int) mascara >> (31 - posmask)) & 1) << (7 - i);
		}
		return mask ^ b;
	}
	
	public void close() {
		try {
			this.in.close();
			this.socket.close();
		} catch (IOException e) {}
	}
	
	public void enviaListaMusicas() {
		ArrayList<Musica> musicas = this.server.procurarMusica(0, "");
		JSONObject root = new JSONObject();
		try {
			root.put("cod", "1");
			JSONArray jsmusicas = new JSONArray();
			for(int i = 0; i < musicas.size(); i++) {
				Musica atual = musicas.get(i);
				JSONObject jsmusica = new JSONObject();
				jsmusica.put("id", String.valueOf(atual.getId()));
				jsmusica.put("nome", atual.getNome());
				jsmusica.put("artista", atual.getArtista());
				jsmusica.put("duracao", String.valueOf(atual.getDuracao()));
				jsmusica.put("path", atual.getPath());
				jsmusica.put("tam", String.valueOf(atual.getTam()));
				jsmusicas.put(jsmusica);
			}
			root.put("musicas", jsmusicas);
			
			ArrayList<Usuario> usuarios = this.server.getAllUsers();
			JSONArray jsusers = new JSONArray();
			for(int i = 0; i < usuarios.size(); i++) {
				Usuario atual = usuarios.get(i);
				JSONObject usuario = new JSONObject();
				usuario.put("id", String.valueOf(atual.getId()));
				usuario.put("nome", atual.getNome());
				usuario.put("ip", atual.getIp());
				usuario.put("status", atual.getOnline() == true ? "on" : "off");
				jsusers.put(usuario);
			}
			
			root.put("usuarios", jsusers);
			
			ArrayList<Download> downloads = this.server.getAllDownloads();
			JSONArray jsdownloads = new JSONArray();
			for(int i = 0; i < downloads.size(); i++) {
				Download atual = downloads.get(i);
				JSONObject download = new JSONObject();
				download.put("id", String.valueOf(atual.getId()));
				download.put("usuario", atual.getUsuario().getNome());
				download.put("ip", atual.getUsuario().getIp());
				download.put("musica", atual.getMusica().getNome());
				download.put("status", String.valueOf(atual.getStatus()));
				download.put("tamanho", String.valueOf(atual.getMusica().getTam()));
				download.put("enviado", String.valueOf(atual.getEnviado()));
				jsdownloads.put(download);
			}
			
			root.put("downloads", jsdownloads);
			
			try {
				this.sendMsg(root.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void enviaListaDownloads() {
		
	}
}
