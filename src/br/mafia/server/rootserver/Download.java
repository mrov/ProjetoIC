package br.mafia.server.rootserver;

public class Download {
	private int id; //id único não importa como seja gerado
	private int idmusica;
	private String ip; //endereço ip de quem solicitou o arquivo
	private int porcentagem;
	private int tamarquivo; //em bytes
	private int baixados; //em bytes
	private int segundosconclusao;
	
}
