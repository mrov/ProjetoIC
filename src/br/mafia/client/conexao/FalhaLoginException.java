package br.mafia.client.conexao;

public class FalhaLoginException extends Exception {
	public FalhaLoginException() {
		super("Usu�rio ou senha incorretos");
	}
}
