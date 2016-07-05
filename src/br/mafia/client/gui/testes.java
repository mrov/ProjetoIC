package br.mafia.client.gui;

import java.util.ArrayList;

import br.mafia.client.conexao.FalhaLoginException;
import br.mafia.client.conexao.UsuarioJaCadastradoException;
import br.mafia.client.musicas.Musica;

public class testes {
	public static void main(String[] args) {
		Cliente t = new Cliente();
//		t.setIPservidor("localhost");
//		t.setPortaServidor("8080");
//		t.SalvarConfig();
		System.out.println(t.getIPservidor() + ":" + t.getPortaServidor());
//		try {
//			t.cadastrar("teste", "teste");
//		} catch (UsuarioJaCadastradoException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//t.addMusica(1, "teste", "ok", 100, "arquivo.mp3", 1000);
		try {
			t.login("teste", "teste");
			ArrayList<Musica> musicas = t.procurarMusicaNome("cavalo");
			for(int i = 0; i < musicas.size(); i++) {
				System.out.println(musicas.get(i).getNome());
			}
			t.logout();
		} catch (FalhaLoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
