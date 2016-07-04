package br.mafia.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Config {
	public static String dir; //diretório da aplicação
	private int portaprincipal;
	private int portawebserver;
	private String rootwebserver;
	private String userwebserver;
	private String passwordwebserver;
	private int portawebsocketserver;
	private int freqatualizawebsocket;
	private String arquivomusicas;
	private String arquivousuarios;
	private String pastamusicas;
	private int maxclientes;
	private int maxconexaoporcliente;
	
	public Config(String arquivo) throws ArquivoConfigNaoEncontradoException { //procura pelo arquivo de configuração na pasta do servidor
		Config.dir = System.getProperty("user.dir");
		String arquivoconfig = Config.dir + File.separator + arquivo;
		File f = new File(arquivoconfig);
		if(f.exists() && !f.isDirectory()) { 
			try {
				BufferedReader br = new BufferedReader(new FileReader(arquivoconfig));
				try {
					String linha;
					linha = br.readLine();
					linha = linha.substring(16, linha.length());
					this.portaprincipal = Integer.parseInt(linha);
					linha = br.readLine();
					linha = linha.substring(16, linha.length());
					this.portawebserver = Integer.parseInt(linha);
					linha = br.readLine();
					linha = linha.substring(15, linha.length());
					this.rootwebserver = linha;
					linha = br.readLine();
					linha = linha.substring(15, linha.length());
					this.userwebserver = linha;
					linha = br.readLine();
					linha = linha.substring(19, linha.length());
					this.passwordwebserver = linha;
					linha = br.readLine();
					linha = linha.substring(22, linha.length());
					this.portawebsocketserver = Integer.parseInt(linha);
					linha = br.readLine();
					linha = linha.substring(23, linha.length());
					this.freqatualizawebsocket = Integer.parseInt(linha);
					linha = br.readLine();
					linha = linha.substring(16, linha.length());
					this.arquivomusicas = linha;
					linha = br.readLine();
					linha = linha.substring(17, linha.length());
					this.arquivousuarios = linha;
					linha = br.readLine();
					linha = linha.substring(14, linha.length());
					this.pastamusicas = linha;
					linha = br.readLine();
					linha = linha.substring(13, linha.length());
					this.maxclientes = Integer.parseInt(linha);
					linha = br.readLine();
					linha = linha.substring(22, linha.length());
					this.maxconexaoporcliente = Integer.parseInt(linha);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {}
		} else {
			throw new ArquivoConfigNaoEncontradoException(arquivoconfig);
		}
	}
	
	public static void geraArquivoConfig(String caminho) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(caminho, "UTF-8");
		writer.println("portaprincipal: 8080");
		writer.println("portawebserver: 8081");
		writer.println("rootwebserver: " + Config.dir + File.separator + "admin");
		writer.println("userwebserver: root");
		writer.println("passwordwebserver: 123");
		writer.println("portawebsocketserver: 8082");
		writer.println("freqatualizawebsocket: 500"); //em milisegundos
		writer.println("arquivomusicas: " + Config.dir + File.separator + "musicas.xml");
		writer.println("arquivousuarios: " + Config.dir + File.separator + "usuarios.xml");
		writer.println("pastamusicas: " + Config.dir + File.separator + "musicas");
		writer.println("maxclientes: 0"); //0 = ilimitado
		writer.println("maxconexaoporcliente: 0"); //0 = ilimitado
		writer.close();
	}

	public int getPortaprincipal() {
		return portaprincipal;
	}

	public int getPortawebserver() {
		return portawebserver;
	}

	public String getRootwebserver() {
		return rootwebserver;
	}

	public String getUserwebserver() {
		return userwebserver;
	}

	public String getPasswordwebserver() {
		return passwordwebserver;
	}

	public int getPortawebsocketserver() {
		return portawebsocketserver;
	}

	public int getFreqatualizawebsocket() {
		return freqatualizawebsocket;
	}

	public String getArquivomusicas() {
		return arquivomusicas;
	}
	
	public String getArquivousuarios() {
		return arquivousuarios;
	}

	public String getPastamusicas() {
		return pastamusicas;
	}

	public int getMaxclientes() {
		return maxclientes;
	}

	public int getMaxconexaoporcliente() {
		return maxconexaoporcliente;
	}
}
