import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class Try {
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		SSLSocketFactory factory=(SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket= (SSLSocket) factory.createSocket("smtp.gmail.com", 465);

		System.out.println("Connected!");
        socket.startHandshake();
        
//		SSLSocket socket = new SSLSocket("smtp.gmail.com", 25);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		Scanner sc = new Scanner(System.in);
		System.out.println("Server: ");
		String command = in.readLine();
		System.out.println(command);
		
		while(sc.hasNextLine()) {
			String line = sc.nextLine();
			System.out.println("You   : " + line);
			if(!line.equals("1"))
				out.println(line);
			if(!line.contains("111")) {
				System.out.print("Server: ");
				command = in.readLine();
				System.out.println(command);
			}
		}
		
		sc.close();
		in.close();
		out.close();
		socket.close();
	}
}
