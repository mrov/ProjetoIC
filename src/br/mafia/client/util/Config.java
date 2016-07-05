package br.mafia.client.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Config {
	private String arquivoconfig;
	private String pastamusicas;
	private String arquivomusicas;
	private String portaservidor;
	private String ipservidor;
	public static String dir;
	private boolean carregado;
	
	public Config(String arquivo) {
		this.carregado = false;
		Config.dir = System.getProperty("user.dir");
		this.arquivoconfig = Config.dir + File.separator + arquivo;
		File f = new File(this.arquivoconfig);
		if(f.exists() && !f.isDirectory()) { 
			try {
				BufferedReader br = new BufferedReader(new FileReader(this.arquivoconfig));
				try {
					String linha;
					linha = br.readLine();
					linha = linha.substring(14, linha.length());
					this.pastamusicas = linha;
					linha = br.readLine();
					linha = linha.substring(16, linha.length());
					this.arquivomusicas = linha;
					linha = br.readLine();
					linha = linha.substring(12, linha.length());
					this.ipservidor = linha;
					linha = br.readLine();
					linha = linha.substring(15, linha.length());
					this.portaservidor = linha;					
					
					this.carregado = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {}
		} else {
			this.geraConfig();
		}
	}
	
	public void geraConfig() {
		PrintWriter writer;
		try {
			writer = new PrintWriter(this.arquivoconfig, "UTF-8");
			writer.println("pastamusicas: " + (this.pastamusicas = Config.dir + File.separator + "musicas"));
			writer.println("arquivomusicas: " + (this.arquivomusicas = Config.dir + File.separator + "musicas.xml"));
			writer.println("ipservidor: "); this.ipservidor = "";
			writer.println("portaservidor: "); this.portaservidor = "";
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void salvaConfig(String arquivo) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(this.arquivoconfig, "UTF-8");
			writer.println("pastamusicas: " + Config.dir + File.separator + "musicas");
			writer.println("arquivomusicas: " + Config.dir + File.separator + "musicas.xml");
			writer.println("ipservidor: " + this.ipservidor);
			writer.println("portaservidor: " + this.portaservidor);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setIPservidor(String ip) {
		this.ipservidor = ip;
	}
	
	public void setPortaServidor(String porta) {
		this.portaservidor = porta;
	}
	
	public String getPastaMusicas() {
		return this.pastamusicas;
	}
	
	public String getArquivoMusicas() {
		return this.arquivomusicas;
	}
	
	public String getIPservidor() {
		return this.ipservidor;
	}
	
	public String getPortaServidor() {
		return this.portaservidor;
	}
}
