package br.mafia.server.webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

public class RequisicaoWeb extends Thread {
	private Socket socket;
	private String pasta;
	private String userweb;
	private String passweb;
	private String pastamusicas;
	private int portawebsocket;
	
	public RequisicaoWeb(Socket socket, String pasta, String userweb, String passweb, String pastamusicas, int portawebsocket) {
		this.socket = socket;
		this.pasta = pasta;
		this.userweb = userweb;
		this.passweb = passweb;
		this.portawebsocket = portawebsocket;
		this.pastamusicas = pastamusicas;
	}
	
	public void run() {
		InputStream in;
		try {
			in = this.socket.getInputStream();
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
			if(total == 0) this.socket.close();
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
				if(primeiralinha[0].equals("GET")) {
					if(primeiralinha[1].equals("/")) {
						primeiralinha[1] = "/index.html";
					}
					if(primeiralinha[1].endsWith(".html")) {
						this.respondeHTML(primeiralinha[0], primeiralinha[1], parametros.get("Authorization"));
					} else if(primeiralinha[1].endsWith(".png")) {
						this.respondePNG(primeiralinha[0], primeiralinha[1]);
					} else if(primeiralinha[1].endsWith(".js")) { 
						if(primeiralinha[1].equals("/porta.js")) {
							String arquivo = "var porta = " + this.portawebsocket + ";";
							DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
							int tam = arquivo.length();
							out.writeBytes("HTTP/1.1 200 OK\r\nContent-type: application/javascript\r\nContent-length: " + tam + "\r\n\r\n" + arquivo);
							out.flush();
							out.close();
							this.socket.close();
						} else {
							this.respondeJS(primeiralinha[0], primeiralinha[1]);
						}
					} else if(primeiralinha[1].endsWith(".mp3")) {
						this.respondeMP3(primeiralinha[0], primeiralinha[1]);
					} else if(primeiralinha[1].endsWith(".ico")) {
						this.respondeICO(primeiralinha[0], primeiralinha[1]);
					} else {
						this.respondeBinario(primeiralinha[0], primeiralinha[1]);
					}
				} else if(primeiralinha[0].equals("POST")) {
					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void responde404() {
		try {
			DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
			String res = "<html><head><title>Not Found</title></head><body><h1>404</h1>Arquivo não encontrado</body><html>";
			int tam = res.length();
			out.writeBytes("HTTP/1.1 404 Not Found\r\nContent-type: text/html\r\nContent-length: " + tam + "\r\n\r\n" + res);
			out.flush();
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void pedeSenha() {
		try {
			DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
			out.writeBytes("HTTP/1.1 401 Access Denied\r\nWWW-Authenticate: Basic realm=\"Mafia server\"\r\nContent-Length: 0\r\n\r\n");
			out.flush();
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void respondeHTML(String metodo, String arquivo, String autorizacao) {
		if(arquivo.equals("/index.html") && autorizacao == null) {
			this.pedeSenha();
		} else if(arquivo.equals("/index.html")) {
			String auth = this.userweb + ":" + this.passweb;
			String[] douser = autorizacao.split(" ");
			
			byte[] b64 = Base64.encodeBase64(auth.getBytes());
			if(new String(b64).equals(douser[1])) {
				this.enviaConteudo(this.pasta + arquivo, "text/html; charset=utf-8");
			} else {
				this.pedeSenha();
			}
		} else {
			this.enviaConteudo(this.pasta + arquivo, "text/html; charset=utf-8");
		}
	}
	
	private void respondePNG(String metodo, String arquivo) {
		this.enviaConteudo(this.pasta + arquivo, "image/png");
	}
	
	private void respondeJS(String metodo, String arquivo) {
		this.enviaConteudo(this.pasta + arquivo, "application/javascript");
	}
	
	private void respondeMP3(String metodo, String arquivo) {
		//arquivo = arquivo.replace("%20", " ");
		try {
			arquivo = java.net.URLDecoder.decode(arquivo, "UTF-8");
			this.enviaConteudo(this.pastamusicas + arquivo, "Content-Type: audio/mpeg");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	private void respondeICO(String metodo, String arquivo) {
		this.enviaConteudo(this.pasta + arquivo, "image/x-icon");
	}
	
	private void respondeBinario(String arquivo, String tipo) {
		this.enviaConteudo(this.pasta + arquivo, "application/octet-stream");
	}
	
	private void enviaConteudo(String arquivo, String tipo) {
		arquivo = arquivo.replace("/", File.separator);
		DataOutputStream out;
		try {
			File f = new File(arquivo);
			long tam = f.length();
			InputStream in = new FileInputStream(f);
			out = new DataOutputStream(this.socket.getOutputStream());
			out.writeBytes("HTTP/1.1 200 OK\r\nContent-Length: " + tam + "\r\nContent-Type: " + tipo + "\r\nConnection: Closed\r\n\r\n");
			out.flush();
			OutputStream outfile = socket.getOutputStream();
			byte[] bytes = new byte[16 * 1024];
			int count;
	        while ((count = in.read(bytes)) > 0) {
	            out.write(bytes, 0, count);
	        }
	        in.close();
	        out.close();
	        outfile.close();
			this.socket.close();
		} catch (FileNotFoundException e) {
			this.responde404();
		} catch (IOException e) {	
			// TODO Auto-generated catch block
			//e.printStackTrace();
			try {
				this.socket.close();
			} catch (IOException e1) {}
		}
	}
}
