import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;

public class ClientSender {

	private String username;
	private String password;
	
	public ClientSender(String username, String password) {
	
		this.username = username;
		this.password = password;
	}
	
	public boolean sendEmail(Email email) {
		
		if (username.contains(ClientUtil.OUR_DOMAIN) && !email.getTo().contains(ClientUtil.OUR_DOMAIN)) {
			return false;
		}

		Socket socket = null;
		Scanner scanner = null;
		PrintWriter out = null;
		try {
			if(email.getTo().contains(ClientUtil.OUR_DOMAIN)) {
				socket = new Socket(ClientUtil.OUR_SMTP_HOST_NAME, ClientUtil.OUR_SMTP_PORT);
			} else if(email.getTo().contains(ClientUtil.GMAIL_DOMAIN)) {
				System.out.println("connecting to gmail");
				SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				SSLSocket s= (SSLSocket) factory.createSocket(ClientUtil.GMAIL_SMTP_HOST_NAME, ClientUtil.GMAIL_SMTP_PORT);
//				s.startHandshake();
				socket = s;
			}
			scanner = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		String message = "\000" + username + "\000" + password;
		byte[] auth = message.getBytes();
		String encoded = DatatypeConverter.printBase64Binary(auth);
		out.println("AUTH PLAIN " + encoded);
		scanner.nextLine();
		
		out.println("HELO");
		scanner.nextLine();
		
		out.println("mail from:<" + email.getFrom() + ">");
		scanner.nextLine();
		
		out.println("rcpt to:<" + email.getTo() + ">");
		scanner.nextLine();
		
		out.println("data");
		scanner.nextLine();
		
		String body = "";
		body += "From: " + email.getFrom() + "\r\n";
		body += "To: " + email.getTo() + "\r\n";
		body += "Subject: " + email.getSubject() + "\r\n";
		body += "Date: " + new Date().toString() + "\r\n";
		body += "boundary=1234" + "\r\n";
		body += "--12345" + "\r\n";
		body += "Content-Type: text/html \r\n";
		body += email.getContent() + "\r\n";
		body += "--12345" + "\r\n";
		
		System.out.println(body);
		
		Scanner scanContent = new Scanner(body);
		while(scanContent.hasNextLine()) {
			out.println(scanContent.nextLine());
		}
		scanContent.close();
		out.println();
		out.println(".");
		scanner.nextLine();
		
		out.println("quit");
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
		out.close();
		
		return true;
	}
}
