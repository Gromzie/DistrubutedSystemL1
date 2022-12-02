package se.miun.distsys;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import se.miun.distsys.listeners.ChatMessageListener;
import se.miun.distsys.listeners.FriendListListener;
import se.miun.distsys.listeners.JoinMessageListener;
import se.miun.distsys.listeners.LeaveMessageListener;
import se.miun.distsys.messages.*;

public class GroupCommuncation {
	
	private int datagramSocketPort = 1; //You need to change this!
	DatagramSocket datagramSocket = null;
	static int vector_pos = 0;
	boolean runGroupCommuncation = true;	
	MessageSerializer messageSerializer = new MessageSerializer();
	List<String> friendList_ = new ArrayList();

	//Listeners
	ChatMessageListener chatMessageListener = null;
	JoinMessageListener joinMessageListener = null;
	LeaveMessageListener leaveMessageListener = null;
	FriendListListener friendListListener = null;

	static Vector<Integer> vector_clock= new Vector<>(1,1);

	public GroupCommuncation() {			
		try {
			runGroupCommuncation = true;				
			datagramSocket = new MulticastSocket(datagramSocketPort);
			ReceiveThread rt = new ReceiveThread();
			rt.start();
			vector_clock.add(0,0 );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		runGroupCommuncation = false;		
	}
	

	class ReceiveThread extends Thread{
		
		@Override
		public void run() {
			byte[] buffer = new byte[65536];		
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
			
			while(runGroupCommuncation) {
				try {
					datagramSocket.receive(datagramPacket);										
					byte[] packetData = datagramPacket.getData();					
					Message receivedMessage = messageSerializer.deserializeMessage(packetData);					
					handleMessage(receivedMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
				
		private void handleMessage (Message message) {
			if(message instanceof JoinMessage){
				JoinMessage joinMessage = (JoinMessage) message;
				friendList_.add(((JoinMessage) message).getUsername());
				System.out.println("Friendlist contains");
				for(String user : friendList_){
					System.out.println(user);
				}
				if(joinMessageListener != null){
					vector_clock.add(friendList_.size()-1, 0);
					System.out.println("size of friendlist: " + friendList_.size());
					System.out.println("size of vectorclock: " + vector_clock.size());
					joinMessageListener.onIncomingJoinMessage(joinMessage);
				}

			}
			if(message instanceof LeaveMessage){
				LeaveMessage leaveMessage = (LeaveMessage) message;
				for(ListIterator<String> it = friendList_.listIterator(); it.hasNext();){
					String value = it.next();
					if(value.equals(leaveMessage.getUsername())){
						it.remove();
					}
				}
				if(leaveMessageListener != null){
					leaveMessageListener.onIncomingLeaveMessage(leaveMessage);
				}
				for(String user : friendList_){
					System.out.println(user);
				}


			}
			if(message instanceof ChatMessage) {				
				ChatMessage chatMessage = (ChatMessage) message;				
				if(chatMessageListener != null){
					chatMessageListener.onIncomingChatMessage(chatMessage);
				}
			}

			if(message instanceof FriendList){
				if(friendListListener != null){
					FriendList friendList = (FriendList) message;
					System.out.println("Adding new user to list");
					friendList_.add(((FriendList) message).getUsername());
					vector_clock.add(friendList_.size()-1, 0);
					vector_pos = friendList_.size()-1;
					System.out.println("test size" + friendList_.size());
					System.out.print("New user" + vector_clock);
					System.out.println("All active users in chat");
					for(String user : friendList_){
						System.out.println(user);
					}
					friendListListener.onIncomingFriendListMessage(friendList);
				}
				else{
					System.out.println("FriendListListener not running");
				}

			}

		}		
	}	
	
	public void sendChatMessage(String chat) {
		try {
			vector_clock.setElementAt(vector_clock.elementAt(vector_pos)+1, vector_pos);
			System.out.println(vector_clock);
			ChatMessage chatMessage = new ChatMessage(chat);
			byte[] sendData = messageSerializer.serializeMessage(chatMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void sendVectorClock(Vector<Integer> vector_clock_){
		try {

			vector_clock vectorclock = new vector_clock(vector_clock_.toString());
			byte[] sendData = messageSerializer.serializeMessage(vectorclock);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void sendJoinMessage(String messageBody){
		try {
			JoinMessage joinMessage = new JoinMessage(messageBody);
			byte[] sendData = messageSerializer.serializeMessage(joinMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SendLeaveMessage(String leaveM){
		try {
			LeaveMessage leaveMessage = new LeaveMessage(leaveM);
			byte[] sendData = messageSerializer.serializeMessage(leaveMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendFriendListMessage(String username){
		try{
			FriendList currentUserMessage = new FriendList(username);
			byte[] sendData = messageSerializer.serializeMessage(currentUserMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void setChatMessageListener(ChatMessageListener listener) {this.chatMessageListener = listener;}
	public void setJoinMessageListener(JoinMessageListener listener) {this.joinMessageListener = listener; }
	public void setLeavenMessageListener(LeaveMessageListener listener) {this.leaveMessageListener = listener; }
	public void setFriendListListener(FriendListListener listener) {this.friendListListener = listener; }

}
