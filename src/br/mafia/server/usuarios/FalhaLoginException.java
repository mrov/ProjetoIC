package br.mafia.server.usuarios;

public class FalhaLoginException extends Exception {
	public FalhaLoginException() {
		super("Usuário ou senha incorretos");
	}
}
