package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date; 

public class Server  extends Thread {

	private static ArrayList<Socket> clients;
	private static ArrayList<String> clientsNames;
	private static ServerSocket server; 
	private String name;
	private String header;
	private String clientIP;
	private Integer clientPort;
	private Socket socket;
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy"); 
	private DataOutputStream outStr;
	private DataInputStream inStr;
	
	public Server(Socket socket) {
	   this.socket = socket;                     
	}
	
	public static void main(String[] args) {
		try {
		    server = new ServerSocket(12345);
		    clients = new ArrayList<Socket>();
		    clientsNames = new ArrayList<String>();
		    while(true){
		    	System.out.println("Aguardando conexão...");
			    Socket socket = server.accept();
			    System.out.println("Cliente conectado...");
			    clients.add(socket);
			    Thread t = new Server(socket);
			    t.start();
		    }
		}catch (Exception e) {
		    e.printStackTrace();
		}   
	}
	
	public void run() {
		try {
			String message;
			clientIP = socket.getInetAddress().getHostAddress();
			clientPort = socket.getPort();
			outStr = new DataOutputStream(socket.getOutputStream());
			inStr = new DataInputStream(socket.getInputStream());
			name = message = inStr.readUTF();
			header = clientIP+":"+clientPort+"/~"+ name;
			Date date = new Date();
	    	String formattedData = formatter.format(date);
		
			clientsNames.add(this.name);
			while(!"bye".equalsIgnoreCase(message) && message != null) {
				message = inStr.readUTF();
		    	String[] splitted = message.split(" ");
		    	Integer splittedLen = splitted.length;
		    	if(message.equals("list")) {
		    		outStr.writeUTF(getAllClients());
		    	}else if(splittedLen >= 1) 
		    		switch(splitted[0]) {
		    		case "send":
		    			if(splittedLen == 1){
		    				this.outStr.writeUTF("Comando inválido \n");
		    				this.help();
		    			}else 
		    				switch(splitted[1]) {
		    					case "-all":
		    						if(splittedLen >= 3)
		    							sendToAll(header, joinMessageStringArray(splitted, 2), formattedData);
		    						else
		    							outStr.writeUTF("Mensagem não informada!\nComo usar: send -all <mensagem>");
		    						break;
		    					case "-user":
		    						if(splittedLen > 3)
		    							sendToUser(splitted[2], header , joinMessageStringArray(splitted, 3), formattedData);
		    						else if(splittedLen == 3)
		    							outStr.writeUTF("Mensagem não informada!\nComo usar: send -user <nome_usuario> <mensagem>");
		    						else
		    							outStr.writeUTF("Usuário não informado!\nComo usar: send -user <nome_usuario> <mensagem>");
		    						break;
		    					default:
		    						this.help();
		    			}
		    			break;
		    		case "rename":
		    			if(splittedLen == 2) 
		    				rename(splitted[1]);
		    			else if(splittedLen == 1)
		    				outStr.writeUTF("Nome não informado!\nComo usar: rename <novo_nome>");
		    			else
		    				outStr.writeUTF("Nome não pode possuir espaços!\nComo usar: rename <novo_nome>");
		    			break;
		    		case "bye":
		    			if(splittedLen == 1) {
		    				outStr.writeUTF("bye");
			    			sendToAll(header , "saiu do grupo!", formattedData);
				    		clients.remove(socket);
				    	    clientsNames.remove(name);
				    	    socket.close();
				    	    System.out.println("Cliente desconectado...");
		    			}else
		    				outStr.writeUTF("Comando não possui parâmetros!\nComo usar: bye");
		    			break;
		    		default:
		    			this.help();
		    	}
		    	
			}
	  }catch (Exception e) {
	     e.printStackTrace();
	  }                       
	}
	
	private void help() throws IOException {
		this.outStr.writeUTF("--- Lista de comandos do chat ---");
		this.outStr.writeUTF("bye					Sair do Grupo");
		this.outStr.writeUTF("send -all <message>			Enviar mensagem ao grupo");
		this.outStr.writeUTF("send -user <name_user> <message>	Enviar mensagem reservada");
		this.outStr.writeUTF("list					Visualizar participantes");
		this.outStr.writeUTF("rename <new_name>			Renomear usuário");
	}
	
	private String joinMessageStringArray(String[] splitted, int start) {
		String result = "";
		for(int i=start; i < splitted.length; i++) {
			result += splitted[i] + " ";
		}
		return result.trim();
	}
	
	public Socket getClient(String clientName) {
		for(String name: clientsNames) {
			if(name.equals(clientName)) {
				return clients.get(clientsNames.indexOf(name));
			}
		}
		return null;
	}
	
	public Integer getClientNameIndex(String clientName) {
		for(String name: clientsNames) {
			if(name.equals(clientName)) {
				return clientsNames.indexOf(name);
			}
		}
		return null;
	}
	
	public String getAllClients() {
		String result = "";
		for(String name: clientsNames) {
			result+= name + "\t";
		}
		return result;
	}
	
	public void sendToAll(String header, String message, String date) throws  IOException {
		for(Socket client : clients){
			  if(!(client == this.socket)){
				  DataOutputStream output = new DataOutputStream(client.getOutputStream());
				  output.writeUTF(header + ": " + message + " " + date + "\r\n");
			  }
		}          
	}
	
	public void sendToUser(String name, String header, String message, String date) throws  IOException{
		Socket client = getClient(name);
		if(client!=null) {
			DataOutputStream output = new DataOutputStream(client.getOutputStream());
			output.writeUTF(header + ": " + message + " " + date + "\r\n");
		}else {
			outStr.writeUTF("Usuário " + name +" não existe");
		}
	}
	
	public void rename(String newName) throws IOException {
		if(clientsNames.contains(newName))
			outStr.writeUTF("Nome de usuário já em uso: " + newName);
		else {
			clientsNames.set(getClientNameIndex(name), newName);
			name = newName;
			header = clientIP+":"+clientPort+"/~"+ name;
			outStr.writeUTF("Cliente Renomeado com sucesso para: " + newName);
		}
		
	}

}
