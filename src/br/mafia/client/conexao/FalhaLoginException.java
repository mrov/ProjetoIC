package br.mafia.client.conexao;

public class FalhaLoginException extends Exception {
	public FalhaLoginException() {
		super("Usuário ou senha incorretos");
	}
}
