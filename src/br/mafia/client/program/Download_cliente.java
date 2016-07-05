package br.mafia.client.program;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import br.mafia.client.gui.Cliente;

public class Download_cliente extends Thread{
	private String address;
	private int port;
	private int id_download;
	private int byte_init;
	private String directory, path, caminho;
	private int pause;
	private Cliente client;
	private String nome, artista;
	private int id_musica, duracao, tam;


	public Download_cliente(String address, int port, int id_download, int byte_init, String directory, String path, Cliente client, String nome, String artista, int duracao, int tam, int id_musica){
		this.address = address;
		this.port = port;
		this.id_download = id_download;
		this.byte_init = byte_init;
		this.directory = directory;
		this.path = path;
		this.caminho = this.directory + "/" + this.path;
		pause=0;
		this.client = client;
		this.nome = nome;
		this.artista = artista;
		this.duracao = duracao;
		this.tam = tam;
		this.id_musica = id_musica;
	}

	public void run() {
		try {
			if (pause == 0 && !Thread.currentThread().isInterrupted()){
				Socket socket = new Socket(address, port);
				OutputStream saida_t = socket.getOutputStream();
				InputStream entrada_t = socket.getInputStream();
				saida_t.write(49);
				saida_t.write(id_download>>8);
				saida_t.write(id_download & 255); //envia 2 bytes de id_download
				saida_t.write(byte_init >> 24);
				saida_t.write((byte_init & 255) >> 16);
				saida_t.write((byte_init & 255) >> 8);
				saida_t.write(byte_init & 255);
				System.out.println("Download iniciado");
				System.out.println("Vai salvar em: " + caminho);
				int bytes_receive=0;
				byte[] buffer = new byte[1024*4];

				OutputStream download;
				if (byte_init == 0) download = new FileOutputStream(caminho);  //está começando agora
				else download = new FileOutputStream(caminho, true);           //continuando download

				int total_bytes=0;
				while ( !Thread.currentThread().isInterrupted() && pause == 0 && (bytes_receive = entrada_t.read(buffer)) > 0){ //recebe dados e grava no buffer
					download.write(buffer, 0, bytes_receive); //envia dados do buffer para o arquivo
					total_bytes+=bytes_receive;
					System.out.println("T. Bytes recebidos: " + total_bytes);
					System.out.println(((double)total_bytes/(double)tam)*100 + "%");
				}
				if (Thread.currentThread().isInterrupted() ){
					System.out.println("Download " + id_download + " cancelado");
				} else if (pause == 0) System.out.println("Download " + id_download + " concluído");
				else {
					System.out.println("Download " + id_download + " pausado");
					byte_init = total_bytes;
				}
				client.addMusica(id_musica, nome, artista, duracao, path, tam);

				socket.close();
			} else if (pause == 1){
				System.out.println("Download " + id_download + " pausado");

			} else {
				System.out.println("Download " + id_download + " cancelado");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (e instanceof IOException){
				System.out.println("Erro: ");
				e.printStackTrace();
			} else{
				System.out.println("Temos problemas no download: ");
				e.printStackTrace();
			}
		}

	}

	public void pause(){
		this.pause = 1;
	}

	public void despause(){
		this.pause = 0;
	}

	public int getID(){
		return id_download;
	}
}
