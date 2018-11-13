package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Server  extends Thread {

	// Definição das variaveis
	private static ArrayList<BufferedWriter> clients;           
	private static ServerSocket server; 
	private String name;
	private Socket socket;
	private InputStream inputStr;  
	private InputStreamReader inputStrRd;  
	private BufferedReader bufferRd;
	
	// construtor
	public Server(Socket socket) {
	   this.socket = socket;
	   try {
         inputStr  = socket.getInputStream();
         inputStrRd = new InputStreamReader(inputStr);
	     bufferRd = new BufferedReader(inputStrRd);
	   } catch (IOException e) {
          e.printStackTrace();
	   }                          
	}
	
	//método principal
	public static void main(String[] args) {
		try {
		    //Cria os objetos necessário para instânciar o servidor
		    JLabel lblMessage = new JLabel("Porta do Servidor:");
		    JTextField txtPorta = new JTextField("12345");
		    Object[] texts = {lblMessage, txtPorta };  
		    JOptionPane.showMessageDialog(null, texts);
		    
		    // instanciação do servidor e clientes
		    server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
		    clients = new ArrayList<BufferedWriter>();
		    
		    JOptionPane.showMessageDialog(null,"Servidor ativo na porta: "+         
		    txtPorta.getText());
		    
		    while(true){
		       System.out.println("Aguardando conexão...");
		       Socket socket = server.accept();
		       System.out.println("Cliente conectado...");
		       Thread t = new Server(socket);
		       t.start();   
		    }
		}catch (Exception e) {
		    e.printStackTrace();
		}   
	}
	
	// inicializa uma thread
	public void run() {
	  try {
	    String message;
	    OutputStream outputStr =  this.socket.getOutputStream(); // coleta os dados do socket 
	    Writer writter = new OutputStreamWriter(outputStr); // começa o tratamento dos dados
	    BufferedWriter bufferWr = new BufferedWriter(writter); // finaliza o tratamento
	    clients.add(bufferWr); // adiciona um cliente
	    this.name = message = this.bufferRd.readLine(); // armazena a mensagem do cliente
	               
	    // enquanto o cliente não digitar nada ou sair, executa o código abaixo 
	    while(!"Sair".equalsIgnoreCase(message) && message != null) {           
	       message = this.bufferRd.readLine(); // armazena a mensagem do cliente
	       
	       // TODO - adicionar método para enviar mensagem para um unico usuario
	       // TODO - adicionar comando para listar clientes no chat
	       
	       sendToAll(bufferWr, message); // envia para todos o clientes a mensagem (um dos comandos exigidos) 
	       System.out.println(message);                                              
	    }
	  }catch (Exception e) {
	     e.printStackTrace();
	  }                       
	}
	
	// Método usado para enviar mensagem para todos os clients
	public void sendToAll(BufferedWriter bufferWrOut, String message) throws  IOException {
	  BufferedWriter bufferWr;
	    
	  for(BufferedWriter bw : clients){
		  bufferWr= (BufferedWriter)bw;
		  if(!(bufferWrOut == bufferWr)){
			  bw.write(this.name + " -> " + message+"\r\n");
			  bw.flush(); 
		  }
	  }          
	}
}
