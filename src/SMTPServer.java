
public class SMTPServer {
	
	public static void main(String[] args) {
		new SMTPReceiver().start();
		new IMAPCommunicator().start();		
	}
}
