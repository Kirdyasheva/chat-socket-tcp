package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class InputThread extends Thread{

	
	private static DataInputStream inStr;
	private Socket socket;
	
	public InputThread(Socket socket) {
		try {
			inStr  = new DataInputStream(socket.getInputStream());
			this.socket = socket;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			while(!socket.isClosed()) {
				System.out.println(inStr.readUTF());
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
