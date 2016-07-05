package br.mafia.client.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MafiaClient {

	private JFrame frame;
	private Cliente cliente;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MafiaClient window = new MafiaClient();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MafiaClient() {
		initialize();
		this.cliente = new Cliente();
		this.textField.setText(cliente.getIPservidor());
		this.textField_1.setText(cliente.getPortaServidor());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblIpDoServidor = new JLabel("IP do servidor:");
		lblIpDoServidor.setBounds(43, 76, 136, 15);
		frame.getContentPane().add(lblIpDoServidor);
		
		JLabel lblPorta = new JLabel("Porta:");
		lblPorta.setBounds(101, 111, 44, 15);
		frame.getContentPane().add(lblPorta);
		
		textField = new JTextField();
		textField.setBounds(163, 74, 212, 19);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(163, 109, 83, 19);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				abrir();
			}
		});
		btnOk.setBounds(258, 203, 117, 25);
		frame.getContentPane().add(btnOk);
	}
	
	public void abrir() {
		cliente.setIPservidor("localhost");
		cliente.setPortaServidor("8080");
		cliente.SalvarConfig();
		this.frame.setVisible(false);
		new LoginFrame(this.cliente);
	}
}
