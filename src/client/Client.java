package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Client {
	
	private static JTextField txtUsername;
	private static Socket socket;
	private static String name;
	private static DataOutputStream outStr;
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
	static Scanner sc = new Scanner(System.in);
	
	public Client(){
	    JLabel lblUsername = new JLabel("Username");
	    txtUsername = new JTextField("Cliente");

	    Object[] texts = {lblUsername, txtUsername };  
	    JOptionPane.showMessageDialog(null, texts); 
	}
	
	public static void main(String[] args) throws IOException{
		Client client = new Client();
		client.connect();
		
		name = txtUsername.getText();
		System.out.println("-- Nome do Cliente: " + name + " --");
		outStr.writeUTF(name);
		
		client.listen();
	}
	
	public void connect() throws IOException{
		socket = new Socket("localhost", 12345);
		outStr = new DataOutputStream(socket.getOutputStream());
	}
	
	public void listen() throws IOException{
		Thread t = new InputThread(socket);
		t.start();
		String message;
		while(true) {
			message = sc.nextLine();
			if(!"bye".equalsIgnoreCase(message) && message != null)
				outStr.writeUTF(message);
			else {
				t.interrupt();
				outStr.writeUTF(message);
				socket.close();
			}
		}
	}

}
