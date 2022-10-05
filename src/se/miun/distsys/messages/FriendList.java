package se.miun.distsys.messages;

public class FriendList extends Message {
    private String username;
    public String getUsername() {
        return username;
    }
    public FriendList(String username){this.username = username;}
}
