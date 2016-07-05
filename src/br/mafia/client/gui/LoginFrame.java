package br.mafia.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import br.mafia.client.conexao.FalhaLoginException;

public class LoginFrame {

	private JFrame frame;
	private Cliente cliente;
	private JTextField textField;
	private JPasswordField passwordField;
	private JButton btnLogin;
	private JButton btnNewButton;

	/**
	 * Create the application.
	 */
	public LoginFrame(Cliente cliente) {
		this.cliente = cliente;
		initialize();
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblLogin = new JLabel("Login:");
		lblLogin.setBounds(70, 61, 70, 15);
		frame.getContentPane().add(lblLogin);
		
		JLabel lblSenha = new JLabel("Senha:");
		lblSenha.setBounds(70, 98, 70, 15);
		frame.getContentPane().add(lblSenha);
		
		textField = new JTextField();
		textField.setBounds(133, 59, 160, 19);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(133, 88, 160, 19);
		frame.getContentPane().add(passwordField);
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		btnLogin.setBounds(176, 139, 117, 25);
		frame.getContentPane().add(btnLogin);
		
		btnNewButton = new JButton("Cadastro");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cadastro();
			}
		});
		btnNewButton.setBounds(176, 181, 117, 25);
		frame.getContentPane().add(btnNewButton);
	}
	
	public void login() {
		try {
			this.cliente.login(this.textField.getText(), this.passwordField.getText());
			this.frame.setVisible(false);
			new LogadoFrame(this.cliente, this);
		} catch (FalhaLoginException e) {
			JOptionPane.showMessageDialog(frame, "Usuário ou senha incorretos", "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void cadastro() {
		new CadastroFrame(this.cliente, this);
	}
	
	public void setText(String usuario, String senha) {
		this.textField.setText(usuario);
		this.passwordField.setText(senha);
	}
	
	public void v() {
		this.frame.setVisible(true);
	}
}
