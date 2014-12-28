import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class SocketClient{

	private static final int PORT = 5001;
	private static String ipAddress; 
	BufferedReader in;
	PrintWriter out;

	public static void main(String[] args) throws Exception{			
		ipAddress = args[0];
		SocketClient client = new SocketClient();
		client.run();

	
	}
	
	private void run() throws IOException {
		Socket socket = new Socket(ipAddress ,PORT);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		String command;
		while(true){
			command = in.readLine();
			System.out.println(command);
			if(command.equals("stop")){
				break;
			}
			command = in.readLine();
			if(command != null){
				System.out.println(command);
				if(!command.contains("Welcome")){
					out.println(executeCommand(command));
				}
			}
		}
		out.println("exit");
		socket.close();
	}
	
	private String executeCommand(String command) {
 
		StringBuffer output = new StringBuffer();
 
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader inputStream = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader errorStream = 
                            new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
			String line = "";			
			while ((line = inputStream.readLine())!= null) {
				output.append(line + "\n");
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


