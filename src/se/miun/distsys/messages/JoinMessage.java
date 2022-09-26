package se.miun.distsys.messages;

import java.net.InetAddress;
import java.util.Random;

public class JoinMessage extends Message {
    private String messageBody;
    private String username;
    public String getUsername() {
        return username;
    }

    private String flavourText = " has entered the chat.";

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public JoinMessage(String username){
        this.messageBody = username + flavourText;
        this.username = username;
    }
}
