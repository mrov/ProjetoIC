package br.mafia.server.usuarios;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import br.mafia.server.musicas.Musica;

public class UsuariosController {
	private int id;
	private ArrayList<Usuario> repusers;
	private String arquivoxml;
	
	public UsuariosController(String arquivoxml) {
		this.repusers = new ArrayList();
		this.arquivoxml = arquivoxml;
		this.id = 1;
		this.lerXML();
	}
	
	public void lerXML() {
		File f = new File(this.arquivoxml);
		SAXBuilder builder = new SAXBuilder();
			Document doc;
			try {
				doc = builder.build(f);
				Element root = (Element) doc.getRootElement();
				Attribute nextid = root.getAttribute("nextid");
				this.id = nextid.getIntValue();
				List usuarios = root.getChildren();
				Iterator i = usuarios.iterator();
				while( i.hasNext() ){
					Element usuario = (Element) i.next();
					int id = Integer.parseInt(usuario.getChildText("id"));
					String nome = usuario.getChildText("nome");
					String senha = usuario.getChildText("senha");
					String ip = usuario.getChildText("ip");
					
					this.repusers.add(new Usuario(id, nome, senha, ip));
				}
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				this.salvarXML();
			}
			

	}
	
	public void salvarXML() {
		Document doc = new Document();
		Element root = new Element("usuarios");
		Attribute nextid = new Attribute("nextid", String.valueOf(this.id));
		root.setAttribute(nextid);
		for (int i=0; i < this.repusers.size(); i++) {
			Element usuario = new Element("usuario");
			Usuario atual = this.repusers.get(i);
			
			 Element id = new Element("id");
			 id.setText(String.valueOf(atual.getId()));
			 usuario.addContent(id);
			 
			 Element nome = new Element("nome");
			 nome.setText(atual.getNome());
			 usuario.addContent(nome);
			 
			 Element senha = new Element("senha");
			 senha.setText(atual.getSenha());
			 usuario.addContent(senha);
			 
			 Element ip = new Element("ip");
			 ip.setText(atual.getIp());
			 usuario.addContent(ip);
			 
			 root.addContent(usuario);
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
	
	public Usuario cadastrarUsuario(String nome, String senha, String ip) throws UsuarioJaCadastradoException {
		if(this.getUsuarioNome(nome) != null) { System.out.println("nome: " + nome);
			throw new UsuarioJaCadastradoException();
		}
		Usuario u = new Usuario(this.id++, nome, senha, ip);
		this.repusers.add(u);
		this.salvarXML();
		return u;
	}
	
	public Usuario login(String nome, String senha, String ip) throws FalhaLoginException {
		for(int i = 0; i < this.repusers.size(); i++) {
			if(this.repusers.get(i).getNome().equals(nome) && this.repusers.get(i).getSenha().equals(senha)) {
				this.repusers.get(i).setOnline(true);
				this.repusers.get(i).setIp(ip);
				if(!ip.equals(this.repusers.get(i).getIp()))
					this.salvarXML(); //salva novo ip do usuário
				return this.repusers.get(i);
			}
		}
		throw new FalhaLoginException();
	}
	
	private Usuario getUsuarioNome(String nome) {
		Usuario u = null;
		for(int i = 0; i < this.repusers.size(); i++) if(this.repusers.get(i).getNome().equals(nome)) u = this.repusers.get(i);
		return  u;
	}
	
	public Usuario getUsuario(int id) {
		Usuario u = null;
		for(int i = 0; i < this.repusers.size(); i++) {
			if(this.repusers.get(i).getId() == id) {
				u = this.repusers.get(i);
				return u;
			}
		}
		return u;
	}
	
	public ArrayList<Usuario> getAll() {
		return this.repusers;
	}
}
