import java.awt.*;

import javax.swing.*;

import se.miun.distsys.GroupCommuncation;
import se.miun.distsys.listeners.*;
import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.FriendList;
import se.miun.distsys.messages.JoinMessage;
import se.miun.distsys.messages.LeaveMessage;
import se.miun.distsys.messages.VecMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

//Skeleton code for Distributed systems

public class WindowProgram implements ChatMessageListener, JoinMessageListener, LeaveMessageListener, FriendListListener, ActionListener, Vector_clock_listener {

	JFrame frame;

	JTextPane txtpnChat = new JTextPane();
	JTextPane txtpnMessage = new JTextPane();
	JTextPane txtpnUsers = new JTextPane();
	String username = "";
	List<String> userList = new ArrayList();

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
		gc = new GroupCommuncation();

		initializeUsername();

		gc.setFriendListListener(null);
		gc.setChatMessageListener(this);
		gc.setJoinMessageListener(this);
		gc.setLeavenMessageListener(this);

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
		gc.setVecMessageListener(this);
		gc.setFriendListListener(this);
		gc.sendJoinMessage(username);
		//System.out.println(username);
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

		JScrollPane scrollPaneUsers = new JScrollPane();
		frame.getContentPane().add(scrollPaneUsers);
		scrollPaneUsers.setViewportView(txtpnUsers);
		userList.add(username);
		txtpnUsers.setEditable(false);
		txtpnUsers.setText(username + "\n" + txtpnUsers.getText());

		JButton btnSendChatMessage = new JButton("Send Chat Message");
		btnSendChatMessage.addActionListener(this);
		btnSendChatMessage.setActionCommand("send");

		frame.getContentPane().add(btnSendChatMessage);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				gc.SendLeaveMessage(username);
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
	@Override
	public void onIncomingFriendListMessage(FriendList friendList) {
		userList.add(friendList.getUsername());
		txtpnUsers.setText("");
		for(String user : userList){
			txtpnUsers.setText(user + "\n" + txtpnUsers.getText());
		}
	}
	@Override
	public void onIncomingJoinMessage(JoinMessage joinMessage) {
		txtpnChat.setText(joinMessage.getMessageBody() + "\n" + txtpnChat.getText());
		gc.sendFriendListMessage(username);
		userList.add(joinMessage.getUsername());
		txtpnUsers.setText("");
		for(String user : userList){
			txtpnUsers.setText(user + "\n" + txtpnUsers.getText());
		}
	}
	@Override
	public void onIncomingLeaveMessage(LeaveMessage leaveMessage) {
		txtpnChat.setText(leaveMessage.getLeaveMessage() + "\n" + txtpnChat.getText());
		userList.remove(leaveMessage.getUsername());
		for(ListIterator<String> it = userList.listIterator(); it.hasNext();){
			String value = it.next();
			if(value.equals(leaveMessage.getUsername())) {
				it.remove();
			}
		}
		txtpnUsers.setText("");
		for(String user : userList){
			txtpnUsers.setText(user + "\n" + txtpnUsers.getText());
		}
	}
	@Override
	public void onIncomingVecMessage(VecMessage vecMessage){
	}
}

