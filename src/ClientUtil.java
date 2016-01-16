import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class ClientUtil {
	
	// Database connectivity parameters
	public static String dbServerIP = "krc9698.wireless.rit.edu";
//	private static String dbServerIP = "localhost";
	private static String schema = "fcn";
	private static String dbuser = "kedar";
	private static String dbpass = "kedar";
	private static String driver = "com.mysql.jdbc.Driver";
	private static String connectionURL = "jdbc:mysql://" + dbServerIP + "/" + schema;
	
	// IMAPCommunicator connectivity parameters
	public static final int GMAIL_IMAP_PORT = 993;
	public static final String GMAIL_IMAP_HOST_NAME = "imap.gmail.com";

	public static final int OUR_IMAP_PORT = 143;
	public static final String OUR_IMAP_HOST_NAME = "krc9698.wireless.rit.edu";
//	public static final String OUR_IMAP_HOST_NAME = "localhost";
	
	// SMTP Receiver connectivity parameters
	public static final int GMAIL_SMTP_PORT = 465;
	public static final String GMAIL_SMTP_HOST_NAME = "imap.gmail.com";
	
	public static final int OUR_SMTP_PORT = 25;
	public static final String OUR_SMTP_HOST_NAME = "krc9698.wireless.rit.edu";
	
	// Domain names
	public static final String OUR_DOMAIN = "krc9698.wireless.rit.edu";
//	public static final String OUR_DOMAIN = "localhost";
	public static final String GMAIL_DOMAIN = "gmail.com";
	
	// Tables
	public static final String USER_TABLE = "fcn.user_info";
	
	public static boolean isAuthenticated(String username, String password) {
		
			Socket socket = null;
			Scanner scanner = null;
			PrintWriter out = null;
			try {
				if(username.contains(OUR_DOMAIN)) {
					socket = new Socket(OUR_IMAP_HOST_NAME, OUR_IMAP_PORT);
					System.out.println(socket.getLocalPort() + " " + socket.getPort());
				} else if(username.contains(GMAIL_DOMAIN)) {
					SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					socket = (SSLSocket) factory.createSocket(GMAIL_IMAP_HOST_NAME, GMAIL_IMAP_PORT);
				} else {
					return false;
				}
				scanner = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
			System.out.println(socket.getLocalPort() + " " + socket.getPort());
			System.out.println("Logging in.");
			System.out.println(scanner.nextLine());
			System.out.println("a1 login " + username + " " + password);
			out.println("a1 login " + username + " " + password);
			
			scanner.nextLine();
			
			
			if(scanner.nextLine().contains("Success")) {
				out.println("a5 logout");
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out.close();
				scanner.close();
				return true;
			} else {
				System.err.println("Wrong username/password");
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out.close();
				scanner.close();
				return false;
			}
	}

}
