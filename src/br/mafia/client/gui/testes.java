package br.mafia.client.gui;

import br.mafia.client.conexao.FalhaLoginException;
import br.mafia.client.conexao.UsuarioJaCadastradoException;

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
		t.addMusica(1, "teste", "ok", 100, "arquivo.mp3", 1000);
		try {
			t.login("teste", "teste");
			System.out.println("ok");
			t.logout();
		} catch (FalhaLoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
