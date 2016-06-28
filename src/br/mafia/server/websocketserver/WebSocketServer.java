package br.mafia.server.websocketserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import br.mafia.server.program.Server;
import br.mafia.server.util.Config;

public class WebSocketServer extends Thread {
	private Config conf;
	private Server server; //fachada
	private ServerSocket srvsocker;
	private ArrayList<WebSocket> conectados;
	
	
	public WebSocketServer(Config conf, Server server) throws IOException {
		this.conf = conf;
		this.server = server;
		this.srvsocker = new ServerSocket(this.conf.getPortawebsocketserver());
		this.conectados = new ArrayList();
	}
	
	public void run() {
		while(!this.srvsocker.isClosed()) {
			try {
				Socket client = this.srvsocker.accept();
				InputStream in;

				in = client.getInputStream();
				byte[] buff = new byte[30000];
				int total = 0;
				int k = 0;
				while((k = in.read(buff, 0, buff.length)) > -1) {
					total += k;
					if(buff[total - 1] == 10 && buff[total - 2] == 13 && buff[total - 3] == 10 && buff[total - 4] == 13) {
						break;
					}
				}
				StringBuilder linha = new StringBuilder();
				boolean leitura = true;
				int pos = 0;
				int linhas = 0;
				String[] primeiralinha = null;
				Map<String, String> parametros = new HashMap<String, String>();
				if(total == 0) client.close();
				else {
					while(leitura) {
						if(buff[pos] == 13 && buff[pos + 1] == 10 && buff[pos + 2] == 13 && buff[pos + 3] == 10) {
							leitura = false;
							linhas ++;
							String[] param = linha.toString().split(": ");
							parametros.put(param[0], param[1]);
							linha = new StringBuilder();
							pos += 3;
						} else if(buff[pos] == 13 && buff[pos + 1] == 10) {
							if(linhas == 0) {
								primeiralinha = linha.toString().split(" ");
							} else {
								String[] param = linha.toString().split(": ");
								parametros.put(param[0], param[1]);
							}
							linhas++;
							linha = new StringBuilder();
							pos++;
						} else {
							linha.append((char)buff[pos]);
						}
						pos++;
					}
				}
				
				if(parametros.get("Upgrade") != null && parametros.get("Upgrade").equals("websocket")) {
					String chave = parametros.get("Sec-WebSocket-Key");
					chave = chave.concat("258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
					String ok = "";
					MessageDigest cript;
					try {
						cript = MessageDigest.getInstance("SHA-1");
						cript.reset();
				        cript.update(chave.getBytes("utf8"));
				        byte[]   n = Base64.encodeBase64(cript.digest());
				        ok = new String(n);
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					DataOutputStream socketOut;
					socketOut = new DataOutputStream(client.getOutputStream());
					socketOut.writeBytes("HTTP/1.1 101 Switching Protocols\r\nUpgrade: WebSocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: " + ok + "\r\n\r\n");
					socketOut.flush();
				}
				
				WebSocket c = new WebSocket(client, this.server);
				this.conectados.add(c);
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void Broadcast(String msg) { //manda msg para todos os administradores conectados no momento
		for(int i = 0; i < this.conectados.size(); i++) {
			try {
				this.conectados.get(i).sendMsg(msg);
			} catch (IOException e) {
				this.conectados.get(i).close();
				this.conectados.remove(i);
			}
		}
	}
}
