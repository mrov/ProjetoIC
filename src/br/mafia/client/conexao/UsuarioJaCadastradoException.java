package br.mafia.client.conexao;

public class UsuarioJaCadastradoException extends Exception {
	public UsuarioJaCadastradoException() {
		super("Nome de usuário já cadastrado");
	}
}
