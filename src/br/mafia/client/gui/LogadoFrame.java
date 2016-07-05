package br.mafia.client.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import br.mafia.client.musicas.Musica;

public class LogadoFrame {

	private JFrame frame;
	private Cliente cliente;
	private LoginFrame login;
	private JTextField textField;
	private DefaultTableModel model;
	private JComboBox comboBox;
	private JTable table;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the application.
	 */
	public LogadoFrame(Cliente cliente, LoginFrame login) {
		this.cliente = cliente;
		this.login = login;
		initialize();
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 750, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(27, 63, 696, 476);
		frame.getContentPane().add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Procurar músicas", null, panel, null);
		panel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(29, 34, 305, 19);
		panel.add(textField);
		textField.setColumns(10);
		
		String[] buscaspossiveis = {"Nome", "Artista"};
		comboBox = new JComboBox(buscaspossiveis);
		comboBox.setBounds(346, 31, 158, 24);
		panel.add(comboBox);
		
		JButton btnProcurar = new JButton("Procurar");
		btnProcurar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				procurar();
			}
		});
		btnProcurar.setBounds(516, 31, 146, 25);
		panel.add(btnProcurar);
		
		this.model = new DefaultTableModel() {
			@Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		
		this.model.addColumn("Id"); 
		this.model.addColumn("Nome"); 
		this.model.addColumn("Artista");
		this.model.addColumn("Duração"); 
		this.model.addColumn("Tamanho");
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(29, 81, 633, 356);
		panel.add(panel_2);
		
		table = new JTable(this.model);
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		
		
		
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(2).setPreferredWidth(20);
		table.getColumnModel().getColumn(3).setPreferredWidth(20);
		table.getColumnModel().getColumn(4).setPreferredWidth(20);
		table.removeColumn(table.getColumnModel().getColumn(0));
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane(table);
		panel_2.add(scrollPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Minhas músicas", null, panel_1, null);
		
		JButton btnDesconectar = new JButton("Desconectar");
		btnDesconectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desconectar();
			}
		});
		btnDesconectar.setBounds(582, 26, 141, 25);
		frame.getContentPane().add(btnDesconectar);
	}
	
	public void desconectar() {
		this.cliente.logout();
		this.frame.setVisible(false);
		this.login.v();
	}
	
	public void procurar() {
		int linhastabela = this.table.getRowCount();
		for (int i = linhastabela - 1; i >= 0; i--) {
		    this.model.removeRow(i);
		}
		
		String busca = this.textField.getText();
		String mod = this.comboBox.getSelectedItem().toString();
		ArrayList<Musica> musicas;
		if(mod.equals("Nome")) {
			musicas = this.cliente.procurarMusicaNome(busca);
		} else {
			musicas = this.cliente.procurarMusicaArtista(busca);
		}
		Musica atual;
		int minutos, segundos;
		for(int i = 0; i < musicas.size(); i++) {
			atual = musicas.get(i);
			minutos = atual.getDuracao() / 60;
			segundos = atual.getDuracao() % 60;
			this.model.addRow(new Object[]{String.valueOf(atual.getId()), atual.getNome(), atual.getArtista(), minutos + ":" + segundos, this.tamstring(atual.getTam())});
		}
	}
	
	private String tamstring(long tam) {
		int kb = (int)(tam / 1024);
		int mb = kb / 1024;
		int gb = mb / 1024;
		String r = "";
		if(gb > 0) r = gb + " GB";
		else if(mb > 0) r = mb + " MB";
		else if(kb > 0) r = kb + " KB";
		else r = tam + " Bytes";
		return r;
	}
}
