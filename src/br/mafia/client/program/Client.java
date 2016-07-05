package br.mafia.client.program;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import br.mafia.client.gui.Cliente;

public class Client {
	Vector threads;
	private Socket socket;
	private String address;
	private int port;
	OutputStream saida;
	InputStream entrada;
	boolean logado;

	private int id_musica, id_download, byte_init;
	private String busca, directory, path;
	private Cliente client;

	private String nome, artista;
	private int duracao, tam;



	public Client( String address, int port){
		threads = new Vector();
		this.address = address;
		this.port = port;
		logado = false;
		this.client = new Cliente();

		directory = "/home/CIN/vrm";
		path="musica.mp3";
		id_musica = 1;
		byte_init = 0;
	}

	public void cadastro(String usuario, String senha) {
		try {
			socket = new Socket(address, port);
			saida = socket.getOutputStream();
			entrada = socket.getInputStream();
			saida.write(0); //cod de cadastro ("0000 0000")
			saida.write(usuario.length());
			saida.write(usuario.getBytes());
			saida.write(senha.length());
			saida.write(senha.getBytes());
			int r = entrada.read();
			if(r == 0) {
				System.out.println("Cadastrado com sucesso");
			} else {
				System.out.println("Usuario já cadastrado");
			}
			logoff();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void login(String usuario, String senha) {
		try {
			socket = new Socket(address, port);

			saida = socket.getOutputStream();
			entrada = socket.getInputStream();

			saida.write(16); //login ("0001 0000")
			saida.write(usuario.length());
			saida.write(usuario.getBytes());
			saida.write(senha.length());
			saida.write(senha.getBytes());
			int r = entrada.read();
			if(r == 16) { // servidor aceitou login, 0001 0000
				logado = true;
				System.out.println("Logado\n");

			} else if ( r == 18){ // 0001 0010
				System.out.println("Servidor cheio, tente mais tarde");
				logoff();
			} else {
				System.out.println("Login ou senha incorretos");
				System.out.println("R: " + r);
				logoff();
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (ConnectException e){
			System.out.println("Servidor não alcançavel");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void comando(int resp){
		/* resp:
		 * 32, setBusca
		 * 48, setIdMusica
		 * 49, comando(48), setDirectory, setPath, setTam, //setNome, setArtista, setDuracao, setIdMusica
		 * 50, setIdDownload
		 * 51, setIdDownload
		 * 52, setIdDownload
		 */
		if (logado){
			try{
				if (resp == 64){ //deslogando 0100 0000
					logoff();

				} else if (resp == 32 || resp == 33){ // pesquisa lista por nome ou artista
					saida.write(resp); //cod de solicitação de lista de músicas por nome (0010 0000) = 32
					byte[] bbusca = busca.getBytes("UTF-8");
					saida.write(bbusca.length); //envia tamanho da string de busca
					saida.write(bbusca); //envia string de busca

					System.out.println("Listando músicas\n");

					int res = entrada.read();
					if(res == 32) {
						int qtdmusicas = entrada.read() << 8; //captura primeiro byte
						qtdmusicas = qtdmusicas | entrada.read(); //captura segundo byte

						int idmusica, duracao, tamstr1, tamstr2, tamstr3;
						long tam = 0L;
						String nome, artista, path_full;

						for(int i = 0; i < qtdmusicas; i++) {
							idmusica = entrada.read() << 8;
							idmusica = idmusica | entrada.read();

							tam = entrada.read() << 24;
							tam = tam | (entrada.read() << 16);
							tam = tam | (entrada.read() << 8);
							tam = tam | entrada.read();

							duracao = entrada.read() << 8;
							duracao = duracao | entrada.read();

							tamstr1 = entrada.read();
							byte[] bnome = new byte[tamstr1];
							entrada.read(bnome);
							nome = new String(bnome, "UTF-8");

							tamstr2 = entrada.read();
							byte[] bartista = new byte[tamstr2];
							entrada.read(bartista);
							artista = new String(bartista, "UTF-8");

							tamstr3 = entrada.read();
							byte[] bpath = new byte[tamstr3];
							entrada.read(bpath);
							path_full = new String(bpath, "UTF-8");

							System.out.println("nome: " + nome + " | id: " + idmusica + " | artista: " + artista + " | tam (bytes): " + tam + " | duracao (seg): " + duracao + " | path: " + path_full);

						}
					}

				} else if (resp == 48){ // solicitação de download 0011 0000
					saida.write(resp);
					saida.write(id_musica >> 8); //envia o id da música em 2 bytes
					saida.write(id_musica & 255); // para o java não fazer besteira
					id_download = entrada.read() << 8; //captura o 1° byte do id de download gerado
					id_download = id_download | entrada.read(); // o 2°
					System.out.println("\nid download: " + id_download);

				} else if (resp == 49){ //solicitação de arquivo
					Thread temp = new Download_cliente(address, port, id_download, byte_init, directory, path, client, nome, artista, duracao, tam, id_musica);
					temp.start();
					threads.add(temp);

				} else if (resp==50){ //pausa 0011 0010
					saida.write(resp);
					saida.write(id_download>>8);
					saida.write(id_download & 255);
					if (!threads.isEmpty()){
						for (int i=0; i<threads.size();i++){
							if (((Download_cliente) threads.get(i)).getID() == id_download) {
								System.out.println("Pausará download " + id_download);
								((Download_cliente) threads.get(i)).pause();
								break;
							}
						}
					}

				} else if (resp == 51){ //cancela 0011 0011
					saida.write(resp);
					saida.write(id_download>>8);
					saida.write(id_download & 255); //envia 2 bytes de id_download
					if (!threads.isEmpty()){
						for (int i=0; i<threads.size();i++){
							if (((Download_cliente) threads.get(i)).getID() == id_download) {
								System.out.println("Cancelando download " + id_download);
								((Thread)threads.get(i)).interrupt();
								break;
							}
						}
						for (int i=0; i<threads.size();i++){
							if (((Download_cliente) threads.get(i)).getID() == id_download) {
								threads.remove(i);
								break;
							}
						}
					}
				} else if(resp == 52){ //despausa 0011 0100
					if (!threads.isEmpty()){ //procura na lista a thread com o id (é assumido que a thread esteja na lista)
						for (int i=0; i<threads.size();i++){
							if (((Download_cliente) threads.get(i)).getID() == id_download) {
								System.out.println("Despausará download " + id_download);
								((Download_cliente) threads.get(i)).despause();
								((Download_cliente) threads.get(i)).run();
								break;
							}
						}
					}
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				System.out.println("Host não encontrado");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Erro comando");
				e.printStackTrace();
			}
		} else{
			System.out.println("Não logado!");
		}
	}

	public void waitDownloads(){
		if (!threads.isEmpty()){
			System.out.println("Irá aguardar os downloads terminarem");
			for (int i=0; i<threads.size();i++){
				try {
					((Thread) threads.get(i)).join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void logoff(){
		logado = false;
		try {
			saida.write(64);
		} catch (IOException e1) {
			System.out.println("Logoff_não_foi_preciso_Exception");
//			e1.printStackTrace();
		}

		if (!threads.isEmpty()){
			for (int i=0; i<threads.size();i++){
				((Thread) threads.get(i)).interrupt();
			}
			for (int i=0; i<threads.size();i++){
				threads.remove(i);
			}
		}
		try{
			this.socket.close();
		} catch (Exception e){
			System.out.println("Exception logoff");
			e.printStackTrace();

		}
		System.out.println("Logoff");
	}

	public void setBusca(String busca){
		this.busca = busca;
	}

	public void setDirectory(String directory){
		this.directory = directory;
	}

	public void setPath(String path){
		this.path = path;
	}

	public void setIdMusica(int id_musica){
		this.id_musica = id_musica;
	}

	public void setIdDownload(int id_download){
		this.id_download = id_download;
	}

	public void setNome(String nome){
		this.nome = nome;
	}

	public void setArtista(String artista){
		this.artista = artista;
	}
	public void setDuracao(int duracao){
		this.duracao = duracao;
	}

	public void setTam(int tam){
		this.tam = tam;
	}
	//
	// public void setBitInit(int bit_init){
	// 	this.byte_init = bit_init;
	// }

}
