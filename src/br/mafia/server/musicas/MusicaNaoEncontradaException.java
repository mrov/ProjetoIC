package br.mafia.server.musicas;

public class MusicaNaoEncontradaException extends Exception {
	public MusicaNaoEncontradaException() {
		super("Música não encontrada");
	}
}
