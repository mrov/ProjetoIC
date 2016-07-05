package br.mafia.client.musicas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

public class MusicasController {
	private String arquivoxml;
	private ArrayList<Musica> repmusicas;
	
	public MusicasController(String arquivoxml) {
		this.repmusicas = new ArrayList();
		this.arquivoxml = arquivoxml;
	}
	
	private void salvarXML() {
		Document doc = new Document();
		Element root = new Element("musicas");
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
	
	private void lerXLM() {
		File f = new File(this.arquivoxml);
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(f);
			Element root = (Element) doc.getRootElement();
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
	
	public ArrayList<Musica> getMusicasBaixadas() {
		return this.repmusicas;
	}
	
	public void addMusica(int id, String nome, String artista, int duracao, String path, int tam) {
		this.repmusicas.add(new Musica(id, nome, artista, duracao, path, tam));
		this.salvarXML();
	}
}
