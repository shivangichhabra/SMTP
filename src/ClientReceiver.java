import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class ClientReceiver {
	
	private String username;
	private String password;
	
	public ClientReceiver(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public void updateSeenStatus(int id) {
		Socket socket = null;
		Scanner scanner = null;
		PrintWriter out = null;
		try {
			if(username.contains(ClientUtil.OUR_DOMAIN)) {
				socket = new Socket(ClientUtil.OUR_IMAP_HOST_NAME, ClientUtil.OUR_IMAP_PORT);
			} else if(username.contains(ClientUtil.GMAIL_DOMAIN)) {
				SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				socket = factory.createSocket(ClientUtil.GMAIL_IMAP_HOST_NAME, ClientUtil.GMAIL_IMAP_PORT);
			}
			scanner = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		scanner.nextLine();
//		String username = "sksfcnproj@gmail.com";
//		String password = "fcnproj123";
		out.println("a1 login " + username + " " + password);
		
		scanner.nextLine();
		if(!scanner.nextLine().contains("Success")) {
			System.err.println("Wrong username/password");
		}

		out.println("a3 select INBOX");
		System.out.println("a4 store " + id + " +FLAGS (\\Seen)");
		out.println("a4 store " + id + " +FLAGS (\\Seen)");

		String line;
		while(!((line = scanner.nextLine()).contains("Success"))) {
			System.out.println(line);
		}
		System.out.println("a5 logout");
		
		out.println("a5 logout");
	
	}
	
	public Email[] fetchEmail() {

		Socket socket = null;
		Scanner scanner = null;
		PrintWriter out = null;
		try {
			if(username.contains(ClientUtil.OUR_DOMAIN)) {
				socket = new Socket(ClientUtil.OUR_IMAP_HOST_NAME, ClientUtil.OUR_IMAP_PORT);
			} else if(username.contains(ClientUtil.GMAIL_DOMAIN)) {
				SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				socket = (SSLSocket) factory.createSocket(ClientUtil.GMAIL_IMAP_HOST_NAME, ClientUtil.GMAIL_IMAP_PORT);
			}
			scanner = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		scanner.nextLine();
//		String username = "sksfcnproj@gmail.com";
//		String password = "fcnproj123";
		out.println("a1 login " + username + " " + password);
		
		scanner.nextLine();
		if(!scanner.nextLine().contains("Success")) {
			System.err.println("Wrong username/password");
			return null;
		}
		
		out.println("a3 examine INBOX");
		
		String[] str = scanner.findWithinHorizon("(\\* [0-9]+ EXISTS)", 0).split(" ");
		int length = Integer.parseInt(str[1]);
		
		System.out.println("Total " + length + " emails");
		
		Email[] emails = new Email[length];

		int start = length - 25 > 0 ? length - 25 : 1;
		int end = length;
		out.println("a4 fetch " + start + ":" + end + " BODY[]");
		System.out.println();

		scanner.findWithinHorizon("(\\* " + start + " FETCH \\(BODY\\[\\])", 0);
		int index = 0;;
		for(int i = start; i <= end; i++) {
			System.out.println("Fetching " + i + "th Email!");

			String line;
			String from = null, fromString = "From: ";
			String to = null, toString = "To: ";
			String subject = null, subString = "Subject: ";
			String timestamp = null, dateString = "Date: ";
			String boundary = null, boundaryString = "boundary=";
			String content = null;
			String contentHTML = null;
			String dataType = "";
			StringBuilder data = new StringBuilder();
			StringBuilder contentBuilder = new StringBuilder();
			try {
				while(!((line = scanner.nextLine()).contains("* " + (i + 1) + " FETCH (BODY[]")) && !line.contains("a4 OK Success")) {
					System.out.println("" + i + "> " + line);
					data.append(line + "<br/>");
					if(line.startsWith(fromString)) {
						from = line.substring(fromString.length());
						System.out.println(">>>>>>>>>>>" + fromString + from);
					}
					if(line.startsWith(toString)) {
						to = line.substring(toString.length());
						System.out.println(toString + to);
					}
					if(line.startsWith(subString)) {
						subject = line.substring(subString.length());
						System.out.println(subString + subject);
					}
					if(line.startsWith(dateString)) {
						timestamp = line.substring(dateString.length());
						System.out.println(dateString + timestamp);
					}
					if(line.contains(boundaryString)) {
						boundary = line.substring(line.indexOf(boundaryString) + boundaryString.length());
						if(boundary.contains("\"")) {
							boundary = boundary.replaceAll("\"", "");
							boundary = boundary.replaceAll(";", "");
						}
						System.out.println(boundaryString + " <" + boundary + ">");
					}
					if(dataType.equals("HTML")) {
						if(line.contains("charset=UTF-8") || line.contains("charset=us-ascii") || line.contains("Content-")) {
							continue;
						}
						if(boundary != null && line.contains(boundary)) {
							dataType = "";
							contentHTML = contentBuilder.toString();
							System.out.println(contentHTML);
							continue;
						}
//						Thread.sleep(200);
//						System.out.println("> " + contentLine);
						contentBuilder.append(line);
					}
					if(line.startsWith("Content-Type: text/html")) {
						dataType = "HTML";
						contentBuilder = new StringBuilder();
					}
					if(dataType.equals("PLAIN")) {
						if(line.contains("charset=UTF-8") || line.contains("charset=us-ascii") || line.contains("Content-")) {
							continue;
						}
						if(boundary != null && line.contains(boundary)) {
							dataType = "";
							content = contentBuilder.toString();
							System.out.println("Content = " + content);
							continue;
						}
//						Thread.sleep(200);
//						System.out.println(">? " + contentLine);
						contentBuilder.append(line + "<br/>");
					}
					if(line.startsWith("Content-Type: text/plain")) {
						dataType = "PLAIN";
						contentBuilder = new StringBuilder();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		
			if(content == null && contentHTML == null) {
				content = data.toString();
			}
			emails[index++] = new Email(i, from, to, subject, timestamp, content == null ? contentHTML : content );
			
		}

		System.out.println("Done reading");
		index = 0;
		String line;
//		while(!((line = scanner.nextLine()).contains("Success")));
		out.println("a fetch " + start + ":" +  end + " flags");
		for(index = 0; index < length; index++) {
			line = scanner.nextLine();
			System.out.println("FLAGS>> " + line);
			if(line.contains("Success")) {
				break;
			}
			System.out.println(start + index);
			if(!line.contains("" + (start + index))) {
				System.err.println("Error: wrong index");
			}
			if(line.contains("\\Seen") && emails[index] != null) {
				emails[index].setSeen(true);
			}
		}
		System.out.println("a5 logout");
		out.println("a5 logout");
		for(int i = 0; i < length; i++) {
			System.out.println(emails[i]);
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
		out.close();
	
		return emails;
	}

}
