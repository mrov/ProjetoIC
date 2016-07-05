package br.mafia.client.program;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;

public class testescliente { //arquivos de testes do cliente
	public static void main(String[] args) {

		String address = "localhost";
		int port = 8080;
		Client cliente = new Client(address, port);
		cliente.cadastro("teste", "teste");
		System.out.println("Cadastrou, irá logar!");
		cliente.login("teste", "teste");
		cliente.setBusca("");
		cliente.comando(32);
		cliente.setIdMusica(1);
		cliente.comando(48);
		cliente.setDirectory("/home/CIN/vrm");
		cliente.setPath("musica.mp3");
		Scanner in3 = new Scanner(System.in);
		cliente.setTam(in3.nextInt());
		cliente.comando(49);
		Scanner in = new Scanner(System.in);
		in.nextInt();		
		cliente.comando(50);
		Scanner in2 = new Scanner(System.in);
		in2.nextInt();
		cliente.comando(52);
		cliente.waitDownloads();
		cliente.logoff();
	}
}
