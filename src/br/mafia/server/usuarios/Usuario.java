package br.mafia.server.usuarios;

public class Usuario {
	private int id;
	private String nome;
	private String senha;
	private boolean online;
	private String ip;
	
	public Usuario(int id, String nome, String senha, String ip) {
		this.id = id;
		this.nome = nome;
		this.senha = senha;
		this.online = false;
		this.ip = ip;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getNome() {
		return this.nome;
	}
	
	public String getSenha() {
		return this.senha;
	}
	
	public boolean getOnline() {
		return this.online;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public void setOnline(boolean online) {
		this.online = online;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
}
