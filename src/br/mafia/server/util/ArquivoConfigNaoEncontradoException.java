package br.mafia.server.util;

public class ArquivoConfigNaoEncontradoException extends Exception {
	private String dir;
	
	public ArquivoConfigNaoEncontradoException(String dir) {
		super("Arquivo de configuração não encontrado");
		this.dir = dir;
	}
	
	public String getDir() {
		return this.dir;
	}
}
