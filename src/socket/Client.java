package socket;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Client extends JFrame implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = 1L;
	private JTextArea text;
	private JTextField txtMsg;
	private JButton btnSend;
	private JButton btnSair;
	private JLabel lblHistorico;
	private JLabel lblMsg;
	private JPanel pnlContent;
	private Socket socket;
	private OutputStream outStr ;
	private Writer writter; 
	private BufferedWriter bufferWr;
	private JTextField txtIP;
	private JTextField txtPort;
	private JTextField txtName;
	
	//construtor do cliente (swing)
	public Client() throws IOException{
		
		// janela inicial
	    JLabel lblMessage = new JLabel("Dados para o servidor");
	    txtIP = new JTextField("127.0.0.1");
	    txtPort = new JTextField("12345");
	    txtName = new JTextField("Cliente");
	    
	    txtPort.setEditable(false);
	    txtIP.setEditable(false);
	    Object[] texts = {lblMessage, txtIP, txtPort, txtName };  
	    JOptionPane.showMessageDialog(null, texts);             
	    
	    // Janela do chat 
	    pnlContent = new JPanel();
	    text = new JTextArea(10,20);
	    text.setEditable(false);
	    text.setBackground(new Color(240,240,240));
	    txtMsg = new JTextField(20);
	    lblHistorico = new JLabel("Histórico");
	    lblMsg = new JLabel("Mensagem");
	    btnSend = new JButton("Enviar");
	    btnSend.setToolTipText("Enviar Mensagem");
	    btnSair = new JButton("Sair");
	    btnSair.setToolTipText("Sair do Chat");
	    btnSend.addActionListener(this);
	    btnSair.addActionListener(this);
	    btnSend.addKeyListener(this);
	    txtMsg.addKeyListener(this);
	    JScrollPane scroll = new JScrollPane(text);
	    text.setLineWrap(true);  
	    pnlContent.add(lblHistorico);
	    pnlContent.add(scroll);
	    pnlContent.add(lblMsg);
	    pnlContent.add(txtMsg);
	    pnlContent.add(btnSair);
	    pnlContent.add(btnSend);
	    pnlContent.setBackground(Color.LIGHT_GRAY);                                 
	    text.setBorder(BorderFactory.createEtchedBorder(Color.BLUE,Color.BLUE));
	    txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));                    
	    setTitle(txtName.getText());
	    setContentPane(pnlContent);
	    setLocationRelativeTo(null);
	    setResizable(false);
	    setSize(250,300);
	    setVisible(true);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) throws IOException { 
		Client app = new Client(); // instancia um cliente para o chat
		app.connect(); // conecta o cliente 
		app.listen(); // deixa a thread ounvido mensagens do servidor
	}
	
	// Método usado para conectar no server socket, retorna IO Exception caso dê algum erro.
	public void connect() throws IOException{
	                           
	  this.socket = new Socket(txtIP.getText(),Integer.parseInt(txtPort.getText()));
	  this.outStr = socket.getOutputStream();
	  this.writter = new OutputStreamWriter(outStr);
	  this.bufferWr = new BufferedWriter(this.writter);
	  this.bufferWr.write(txtName.getText()+"\r\n");
	  this.bufferWr.flush();
	}
	
	// Método usado para enviar mensagem para o server socket
	public void sendMessage(String message) throws IOException{
		
		//verifica se o cliente digitou 'sair' e se foi o mesmo que digitou a mensagem
		if(message.equals("Sair")){
			this.bufferWr.write("Desconectado \r\n");
			this.text.append("Desconectado \r\n");
		}else{
			this.bufferWr.write(message+"\r\n");
			this.text.append( txtName.getText() + " diz -> " + txtMsg.getText()+"\r\n");
		}
		this.bufferWr.flush();
		this.txtMsg.setText("");        
	}
	
	// Método usado para receber mensagem do servidor
	public void listen() throws IOException{          
	   InputStream inputStr = this.socket.getInputStream(); // coleta os dados do socket
	   InputStreamReader inputStrRd = new InputStreamReader(inputStr); // inicia o tratamento dos dados
	   BufferedReader bufferRd = new BufferedReader(inputStrRd); // finaliza o tratamento
	   String msg = "";
	                           
	   // enquanto o cliente não digitar sair, continua ouvindo mensagens
	   while(!"Sair".equalsIgnoreCase(msg)) {
		   if(bufferRd.ready()){
	         msg = bufferRd.readLine();
	         if(msg.equals("Sair")) text.append("Servidor caiu! \r\n");
	         else this.text.append(msg+"\r\n");         
		   }
	   }
	}
	
	// Método usado quando o usuário clica em sair
	public void exit() throws IOException{
		
		sendMessage("Sair"); // envia o comando para fechar conexão com o cliente 
		this.bufferWr.close(); // fecha os inputs
		this.writter.close();
		this.writter.close();
		this.socket.close(); // fecha a conexão
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	          
		try {
			if(e.getActionCommand().equals(btnSend.getActionCommand()))
				sendMessage(txtMsg.getText());
	     else
	        if(e.getActionCommand().equals(btnSair.getActionCommand()))
	        	exit();
	     } catch (IOException e1) {
	          // TODO Auto-generated catch block
	          e1.printStackTrace();
	     }                       
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		// se o cliente pressionar o enter, é enviado a mensagem escrita
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
	       try {
	          sendMessage(txtMsg.getText());
	       } catch (IOException e1) {
	           // TODO Auto-generated catch block
	           e1.printStackTrace();
	       }                                                          
	   }                       
	}
	    
	@Override
	public void keyReleased(KeyEvent arg0) {
	  // Método da interface               
	}
	    
	@Override
	public void keyTyped(KeyEvent arg0) {
	  // Método da interface               
	}
}