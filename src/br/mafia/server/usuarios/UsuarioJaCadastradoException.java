package br.mafia.server.usuarios;

public class UsuarioJaCadastradoException extends Exception {
	public UsuarioJaCadastradoException() {
		super("Usuário já cadastrado");
	}
}
