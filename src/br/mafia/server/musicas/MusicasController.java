package br.mafia.server.musicas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

public class MusicasController {
	private ArrayList<Musica> repmusicas;
	private String arquivoxml;
	private int id;
	
	public MusicasController(String arquivoxml) {
		this.repmusicas = new ArrayList();
		this.arquivoxml = arquivoxml;
		this.id = 1;
		this.lerXLM();
	}
	
	private void lerXLM() {
		File f = new File(this.arquivoxml);
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(f);
			Element root = (Element) doc.getRootElement();
			Attribute nextid = root.getAttribute("nextid");
			this.id = nextid.getIntValue();
			List musicas = root.getChildren();
			Iterator i = musicas.iterator();
			while( i.hasNext() ){
				Element musica = (Element) i.next();
				int id = Integer.parseInt(musica.getChildText("id"));
				String nome = musica.getChildText("nome");
				String artista = musica.getChildText("artista");
				int duracao = Integer.parseInt(musica.getChildText("duracao"));
				String path = musica.getChildText("path");
				int tam = Integer.parseInt(musica.getChildText("tam"));
				this.repmusicas.add(new Musica(id, nome, artista, duracao, path, tam));
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			this.salvarXML();
		}
	}
	
	
	public void addMusica(String nome, String artista, int duracao, String path, int tam) {
		this.repmusicas.add(new Musica(this.id++, nome, artista, duracao, path, tam));
		this.salvarXML();
	}
	
	public void deletarMusica(int id) {
	    for (int i=0; i < this.repmusicas.size(); i++) {
	    	if(this.repmusicas.get(i).getId() == id) {
	    		this.repmusicas.remove(i);
	    	}
	    }
		this.salvarXML();
	}
	
	public Musica getMusica(int id) throws MusicaNaoEncontradaException {
		Musica s = null;
		for (int i=0; i < this.repmusicas.size(); i++) {
	    	if(this.repmusicas.get(i).getId() == id) {
	    		s = this.repmusicas.get(i);
	    	}
	    }
		if(s == null) throw new MusicaNaoEncontradaException();
		return s;
	}
	
	private void salvarXML() {
		Document doc = new Document();
		Element root = new Element("musicas");
		Attribute nextid = new Attribute("nextid", String.valueOf(this.id));
		root.setAttribute(nextid);
	    for (int i=0; i < this.repmusicas.size(); i++) {
		    Element musica = new Element("musica");
		    Musica atual = this.repmusicas.get(i);
		    
		    Element id = new Element("id");
		    id.setText(String.valueOf(atual.getId()));
		    musica.addContent(id);
		    
		    Element nome = new Element("nome");
		    nome.setText(String.valueOf(atual.getNome()));
		    musica.addContent(nome);
		    
		    Element artista = new Element("artista");
		    artista.setText(String.valueOf(atual.getArtista()));
		    musica.addContent(artista);
		    
		    Element duracao = new Element("duracao");
		    duracao.setText(String.valueOf(atual.getDuracao()));
		    musica.addContent(duracao);
		    
		    Element path = new Element("path");
		    path.setText(String.valueOf(atual.getPath()));
		    musica.addContent(path);
		    
		    Element tam = new Element("tam");
		    tam.setText(String.valueOf(atual.getTam()));
		    musica.addContent(tam);
		    
		    root.addContent(musica);
	    }
	    doc.setRootElement(root);
	    
	    XMLOutputter xout = new XMLOutputter();
	    try {
			OutputStream out = new FileOutputStream( new File(this.arquivoxml));
		    xout.output(doc , out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void buscaNaPasta(String caminho) {
		File f = new File(caminho);
		String arquivos[] = f.list();
		if(arquivos == null) { //diretório de músicas não existe, então tenta criar
			new File(caminho).mkdirs();
		} else {
			for(String a:arquivos){
				if(a.toLowerCase().endsWith(".mp3")){
					File m = new File(caminho + File.separator + a);
					//String nome = this.nome(m);
					String nome = a.substring(0, a.indexOf("."));
					String artista = this.artista(m);
					if(artista.equals("")) artista = "Desconhecido";
					int duracao = this.duracao(m);
					long tam = m.length();
					this.repmusicas.add(new Musica(this.id++, nome, artista, duracao, a, tam));
				}
			}
		}
		this.salvarXML();
	}
	
	private int duracao(File f) {
		try {
			  AudioFile arquivo = AudioFileIO.read(f);
			  int duracao = arquivo.getAudioHeader().getTrackLength();
			  return duracao;
			} catch (Exception e) {
			  e.printStackTrace();
			}
		return -1;
	}
	
	private String nome(File f) {
		String nome = null;
		try {
			  AudioFile arquivo = AudioFileIO.read(f);
			  nome = arquivo.getTag().getFirst(FieldKey.TITLE);
			} catch (Exception e) {
			  e.printStackTrace();
			}
		return nome;
	}
	
	private String artista(File f) {
		String artista = null;
		try {
			  AudioFile arquivo = AudioFileIO.read(f);
			  artista = arquivo.getTag().getFirst(FieldKey.ARTIST);
			} catch (Exception e) {
			  e.printStackTrace();
			}
		return artista;
	}
	
	public boolean repVazio() {
		return this.repmusicas.size() == 0 ? true : false;
	}
	
	public ArrayList<Musica> procuraMusica(int tipo, String busca) {
		ArrayList<Musica> musicasencontradas = new ArrayList();
		String b = busca.toLowerCase();
		
		for(int i = 0; i < this.repmusicas.size(); i++) {
			String f = ((tipo == 0) ? this.repmusicas.get(i).getNome() : this.repmusicas.get(i).getArtista()).toLowerCase();
			if(f.matches("(.*)" + b + "(.*)")) {
				musicasencontradas.add(this.repmusicas.get(i));
			}
		}
		return musicasencontradas;
	}
}
