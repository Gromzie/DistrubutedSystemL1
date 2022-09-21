package se.miun.distsys.messages;

public class LeaveMessage extends Message{
    public String getUsername() {
        return username;
    }

    private String username;

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    private String leaveMessage;

    private String leaveText = " has left the chat.";

    public LeaveMessage(String username){
        this.leaveMessage = username + leaveText;
        this.username = username;

    }
}
