package br.mafia.server.musicas;

public class Musica {
	private int id;
	private String nome; //nome da faixa
	private String artista; //Wesley Safadão, Calipso, etc :D
	private int duracao; //em segundos
	private String path; //caminho para o arquivo (C:\x.mp3)
	private long tam; //tam em bytes
	
	public Musica(int id, String nome, String artista, int duracao, String path, long tam) {
		this.id = id;
		this.nome = nome;
		this.artista = artista;
		this.duracao = duracao;
		this.path = path;
		this.tam = tam;
	}

	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getArtista() {
		return artista;
	}

	public int getDuracao() {
		return duracao;
	}

	public String getPath() {
		return path;
	}

	public long getTam() {
		return tam;
	}
}
