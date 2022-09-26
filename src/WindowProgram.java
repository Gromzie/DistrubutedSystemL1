import java.awt.*;

import javax.swing.*;

import se.miun.distsys.GroupCommuncation;
import se.miun.distsys.listeners.ChatMessageListener;
import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.JoinMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Scanner;

//Skeleton code for Distributed systems

public class WindowProgram implements ChatMessageListener, ActionListener {

	JFrame frame;
	JTextPane txtpnChat = new JTextPane();
	JTextPane txtpnMessage = new JTextPane();
	JTextPane txtpnUsers = new JTextPane();
	String username = "";

	GroupCommuncation gc = null;	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WindowProgram window = new WindowProgram();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public WindowProgram() {
		initializeUsername();

		gc = new GroupCommuncation();		
		gc.setChatMessageListener(this);
		System.out.println("Group Communcation Started");
	}

	private void initializeUsername() {
		Scanner usernameCheck = new Scanner(System.in);
		while(username.isBlank()){
			try {
				System.out.println("Enter Username");
				username = usernameCheck.nextLine();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Thread Sleep Exeption");
				throw new RuntimeException(e);
			}
		}
		System.out.println(username);
		initializeChat();
	}
	private void initializeChat(){


		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);
		scrollPane.setViewportView(txtpnChat);
		txtpnChat.setEditable(false);
		txtpnChat.setText("--== Group Chat ==--");

		JoinMessage joinMessage = new JoinMessage(username);
		txtpnChat.setText(joinMessage.getMessageBody());

		txtpnMessage.setText("Message");
		frame.getContentPane().add(txtpnMessage);


		JButton btnSendChatMessage = new JButton("Send Chat Message");
		btnSendChatMessage.addActionListener(this);
		btnSendChatMessage.setActionCommand("send");

		frame.getContentPane().add(btnSendChatMessage);

		txtpnUsers.setText("Active chat users");
		txtpnUsers.setVisible(true);
		txtpnUsers.setEditable(false);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				gc.shutdown();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equalsIgnoreCase("send")) {
			gc.sendChatMessage(username + ": " + txtpnMessage.getText());
		}
	}
	
	@Override
	public void onIncomingChatMessage(ChatMessage chatMessage) {	
		txtpnChat.setText(chatMessage.chat + "\n" + txtpnChat.getText());				
	}
}