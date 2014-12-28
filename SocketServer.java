import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.Dimension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

public class SocketServer {
	
	static HashMap<JRadioButton, PrintWriter> clients = new HashMap<JRadioButton, PrintWriter>();
	private static final int PORT = 5001;
	static int clientNumber = 1;
	static JFrame frame;
    	static JTextField textField = new JTextField(40);
    	static JTextArea messageArea = new JTextArea(8, 40);
    	static JPanel eastPanel = new JPanel();
    	final static int WIDTH = 550;
    	final static int HEIGHT = 350;
	static ButtonGroup bg = new ButtonGroup();
	/**
	Creates the GUI for this application
	**/
	public SocketServer(){
		frame = new JFrame("Control System");
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		textField.setEditable(true);
        	messageArea.setEditable(false);
        	frame.getContentPane().add(textField, "South");
        	frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        	eastPanel.setLayout(new GridLayout(6,1));
		frame.getContentPane().add(eastPanel, "East");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	frame.pack();

        	textField.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
				Set<JRadioButton> list = clients.keySet();
				for(JRadioButton button : list){
					if(button.isSelected()){
						clients.get(button).println(textField.getText());
						break;	
					}
				}
				
                		textField.setText("");
            		}
        	});
	}
	
	
	/**
	The main method
	**/
	public static void main(String[] args){
		SocketServer server = new SocketServer();
		server.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       		server.frame.setVisible(true);
		try{ 
    	    		ServerSocket listener = new ServerSocket(PORT);
			messageArea.append("The command server is running on IP:"+ InetAddress.getLocalHost().getHostAddress()+ " Port: "+PORT+"\n");
        		try {
           			while (true) {
                			new Handler(listener.accept()).start();
					
            			}
        		} finally {
            			listener.close();
        		}
		}catch(IOException e){
			System.out.println(e);
		}
    	}
	/**
	Handles each client

	**/
	private static class Handler extends Thread{
		Socket socket;		
		
		PrintWriter out;
		BufferedReader in;		

		public Handler(Socket socket){
			this.socket = socket;
			messageArea.append("A new client with the number "+clientNumber+" has Connected with the IP: "+ socket.getInetAddress()+"\n");
			System.out.println("InetAddress "+socket.getInetAddress());
			System.out.println("LocalAddress "+socket.getLocalAddress());
			
		}
		public void run(){
			int thisClient = clientNumber++;
			JRadioButton rb = new JRadioButton("Client #"+thisClient);
			bg.add(rb);
			eastPanel.add(rb);
			frame.repaint();
			frame.validate();
			
			try{
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				clients.put(rb, out);
				out.println("Welcome Client #"+thisClient);
				String message;
				do{
					message = in.readLine();
					if(message != null && !message.equals("exit"))
						messageArea.append(message+"\n");
				}while(!message.equals("exit"));
			}catch(IOException e){
				System.out.println(e);
			}finally{
				try {
					socket.close();
                		} catch (IOException e) {
					System.out.println("Socket was already closed");
                		}
                		bg.remove(rb);
				eastPanel.remove(rb);
				frame.repaint();
				frame.validate();
				messageArea.append("Client #" + thisClient+ " has went offline\n");
			}
			
		}

	}
	

}
