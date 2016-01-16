import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class Client {

	private JFrame frame;
	private JFrame authenticator;
	private JFrame composer;

	private JLabel receiverlbl;
	private JLabel subjectlbl;
	private JLabel receiver;
	private JLabel subject;
	private JLabel content;
	
	private JButton login;

	private DefaultListModel<Email> listModel;

	private String username;
	private String password;

	private static final String FROM_STR    = "From        :";
	private static final String TO_STR      = "To          :";
	private static final String SUBJECT_STR = "Subject     :";
	
	private static final String DEFAULT_USERNAME = "kedar@krc9698.wireless.rit.edu";
	private static final String DEFAULT_PASSWORD = "fcn";
	
	private ClientReceiver clientReceiver;
	private ClientSender clientSender;

	public Client() {

		authenticator = new JFrame("Login");

		Component authenticatorContents = createAuthenticatorComponents();
		authenticator.getContentPane().add(authenticatorContents);

		authenticator.getRootPane().setDefaultButton(login);
		
		authenticator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		authenticator.pack();
		authenticator.setLocationRelativeTo(null);
		authenticator.setVisible(true);

	}

	private Component createAuthenticatorComponents() {
		String lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {

		}

		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.setBorder(new EmptyBorder(10, 10, 10, 10) );

		// Add Username
		JPanel uPanel = new JPanel();
		uPanel.setLayout(new BoxLayout(uPanel, BoxLayout.X_AXIS));

		JTextField dummy = new JTextField("123456789012345@aaaaaaaa.com");

		JLabel unamelbl = new JLabel("Username");
		unamelbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		uPanel.add(unamelbl);
		final JTextField uname = new JTextField(DEFAULT_USERNAME);
		uname.setPreferredSize(dummy.getPreferredSize());
		uPanel.add(uname);

		main.add(uPanel);

		// Add Password
		JPanel pPanel = new JPanel();
		pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.X_AXIS));

		JLabel passlbl = new JLabel("Password");
		passlbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		pPanel.add(passlbl);
		final JPasswordField pass = new JPasswordField(DEFAULT_PASSWORD);
		pass.setPreferredSize(dummy.getPreferredSize());
		pPanel.add(pass);

		main.add(pPanel);

		// Add buttons
		JPanel bPanel = new JPanel();
		bPanel.setLayout(new BoxLayout(bPanel, BoxLayout.X_AXIS));

		login = new JButton("Login");
		login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				username = uname.getText();
				password = new String(pass.getPassword());
				
				if(ClientUtil.isAuthenticated(username, password)) {
					authenticator.dispose();
					clientReceiver = new ClientReceiver(username, password);
					clientSender = new ClientSender(username, password);
					
					frame = new JFrame("Email Client");
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
					Component contents = createComponents();
					frame.getContentPane().add(contents);

					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.pack();
					frame.setVisible(true);

				} else {
					JOptionPane.showMessageDialog(null, "Invalid Username / password");
				}
			}
		});
		bPanel.add(login);
		main.add(bPanel);

		return main;
	}

	private Component createComponents() {

		String lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {

		}

		JPanel main = new JPanel();
		main.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		Border border = BorderFactory.createEtchedBorder();
		main.setBorder(new EmptyBorder(10, 10, 10, 10) );

		// Add hello label
		JPanel helloLabels = new JPanel();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		// constraints.weightx = 10;
		constraints.weighty = 2;
		main.add(helloLabels, constraints); 

		JLabel helloMessage = new JLabel("Hello, " + getUserName() + "!");
		helloMessage.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
		helloLabels.add(helloMessage);

		// Add buttons
		JPanel buttons = new JPanel();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		// constraints.weightx = 5;
		constraints.weighty = 2;

		JButton dummy1 = new JButton("XXXXXXXX");
		JButton compose = new JButton("Compose");
		compose.setPreferredSize(dummy1.getPreferredSize());
		compose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				composeMail();
			}

		});
		buttons.add(compose);
		/*JButton reply = new JButton("Reply");
		reply.setPreferredSize(dummy1.getPreferredSize());
		reply.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}

		});
		buttons.add(reply);
		JButton forward = new JButton("Forward");
		forward.setPreferredSize(dummy1.getPreferredSize());
		forward.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}

		});
		buttons.add(forward);
		JButton delete = new JButton("Delete");
		delete.setPreferredSize(dummy1.getPreferredSize());
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}

		});
		buttons.add(delete);*/
		JButton refresh = new JButton("Refresh");
		refresh.setPreferredSize(dummy1.getPreferredSize());
		refresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getEmail();
			}

		});
		buttons.add(refresh);

		main.add(buttons, constraints);

		// Add List of emails and details view
		listModel = new DefaultListModel<Email>();
		getEmail();

		JScrollPane msgPane = new JScrollPane();

		msgPane.setBorder(BorderFactory.createTitledBorder(border, "Inbox"));
		msgPane.setMinimumSize(msgPane.getPreferredSize());
		msgPane.setMaximumSize(msgPane.getPreferredSize());
		msgPane.setPreferredSize(msgPane.getPreferredSize());

		// list of emails
		final JList<Email> listOfEmails = new JList<>(listModel);
		listOfEmails.setCellRenderer(new CustomCellRenderer());
		listOfEmails.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				Email email = listOfEmails.getSelectedValue();
				if(email != null) {
					if(!email.isSeen()) {
						email.setSeen(true);
						clientReceiver.updateSeenStatus(email.getId());
					}
					setContent("" + email.getHTMLContent());
					setReceiver(email.getFrom());
					setSubject(email.getSubject());
				}
			}
		});

		msgPane.setViewportView(listOfEmails);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 10);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 20;
		constraints.weighty = 96;

		main.add(msgPane, constraints);

		JPanel detailsView = new JPanel();
		detailsView.setLayout(new GridBagLayout());
		detailsView.setBorder(BorderFactory.createTitledBorder(border, "Details"));
		detailsView.setMinimumSize(detailsView.getPreferredSize());
		detailsView.setMaximumSize(detailsView.getPreferredSize());
		detailsView.setPreferredSize(detailsView.getPreferredSize());
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 10, 0, 0);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 80;
		constraints.weighty = 96;

		main.add(detailsView, constraints);

		// Add From line
		receiverlbl = new JLabel(FROM_STR);
		receiverlbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		receiverlbl.setBackground(Color.GREEN);
		receiverlbl.setPreferredSize(new Dimension(80, 20));
		receiverlbl.setMinimumSize(receiverlbl.getPreferredSize());
		receiverlbl.setMaximumSize(receiverlbl.getPreferredSize());
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 5;
		constraints.weighty = 2;
		detailsView.add(receiverlbl, constraints);

		receiver = new JLabel("");
		receiver.setPreferredSize(new Dimension(400, 20));
		receiver.setMinimumSize(receiver.getPreferredSize());
		receiver.setMaximumSize(receiver.getPreferredSize());
		receiver.setBackground(Color.GREEN);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 95;
		constraints.weighty = 2;
		detailsView.add(receiver, constraints);

		// Add Subject line
		subjectlbl = new JLabel(SUBJECT_STR);
		subjectlbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		subjectlbl.setBackground(Color.RED);
		subjectlbl.setPreferredSize(new Dimension(80, 20));
		subjectlbl.setMinimumSize(subjectlbl.getPreferredSize());
		subjectlbl.setMaximumSize(subjectlbl.getPreferredSize());
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 5;
		constraints.weighty = 2;
		detailsView.add(subjectlbl, constraints);

		subject = new JLabel("");
		subject.setPreferredSize(new Dimension(400, 20));
		subject.setMinimumSize(subject.getPreferredSize());
		subject.setMaximumSize(subject.getPreferredSize());
		subject.setBackground(Color.RED);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 95;
		constraints.weighty = 2;
		detailsView.add(subject, constraints);		

		// Add content
		content = new JLabel();
		content.setHorizontalAlignment(SwingConstants.LEFT);
		content.setVerticalAlignment(SwingConstants.TOP);
		content.setPreferredSize(content.getPreferredSize());
		content.setMinimumSize(content.getPreferredSize());
		content.setMaximumSize(content.getPreferredSize());
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		//		constraints.weightx = 90;
		constraints.weighty = 96;
		JScrollPane contentPane = new JScrollPane();
		contentPane.setViewportView(content);
		detailsView.add(contentPane, constraints);

		return main;
	}

	public void composeMail() {
		composer = new JFrame("Compose Mail");

		Component authenticatorContents = createComposerComponents();
		composer.getContentPane().add(authenticatorContents);

		composer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		composer.pack();
		composer.setLocationRelativeTo(null);
		composer.setVisible(true);

	}

	public Component createComposerComponents() {
		JPanel main = new JPanel();
		main.setPreferredSize(new Dimension(600, 400));
		main.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
//		Border border = BorderFactory.createEtchedBorder();
		main.setBorder(new EmptyBorder(10, 10, 10, 10) );

		// Add From line
		JLabel receiverlbl = new JLabel(TO_STR);
		receiverlbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 5;
		constraints.weighty = 2;
		main.add(receiverlbl, constraints);

		final JTextField receiver = new JTextField();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 95;
		constraints.weighty = 2;
		main.add(receiver, constraints);

		// Add Subject line
		JLabel subjectlbl = new JLabel(SUBJECT_STR);
		subjectlbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 5;
		constraints.weighty = 2;
		main.add(subjectlbl, constraints);

		final JTextField subject = new JTextField();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 95;
		constraints.weighty = 2;
		main.add(subject, constraints);		

		// Add content
		final JTextArea content = new JTextArea();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTH;
		//				constraints.weightx = 90;
		constraints.weighty = 94;
		JScrollPane contentPane = new JScrollPane();
		contentPane.setViewportView(content);
		main.add(contentPane, constraints);
		
		// Add content
		JButton send = new JButton("Send");
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String emailFrom = username;
				String emailTo = receiver.getText();
				String emailSubject = subject.getText();
				String emailDate = new Date().toString();
				String emailContent = content.getText().replaceAll("\n", "<br/>");
				
				Email email = new Email(emailFrom, emailTo, emailSubject, emailDate, emailContent);
				
				System.out.println("Sending email = " + email);
				clientSender.sendEmail(email);
				
				composer.dispose();
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTH;
		// constraints.weightx = 90;
		constraints.weighty = 2;
		main.add(send, constraints);

		return main;
	}

	private String getUserName() {
		return this.username;
	}

	public void setReceiver(String username) {
		this.receiver.setText(username);
	}

	public void setSubject(String subject) {
		this.subject.setText(subject);
	}

	public void setContent(String stringContent) {
		this.content.setText(stringContent);
	}

	public void getEmail() {
		listModel.clear();
		
		Email[] list = clientReceiver.fetchEmail();
		for(int i = list.length - 1; i >= 0; i--) {
			if(list[i] != null) {
				listModel.addElement(list[i]);
			}
		}
	}

	public static void main(String[] args) {
		new Client();
	}

}

class CustomCellRenderer extends JLabel implements ListCellRenderer<Email> {

	private static final long serialVersionUID = -7969372306425418822L;

	public CustomCellRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Email> list, Email value, int index, boolean isSelected, boolean cellHasFocus) {
		if(value != null) {
			setText(value.toString());
		} 
		if(isSelected) {
			setBackground(new Color(57, 105, 138));
			setForeground(Color.WHITE);
		} else {
			setBackground(index % 2 == 0 ? Color.WHITE : new Color(220, 233, 241));
			setForeground(Color.BLACK);
		}
		return this;
	}
}
