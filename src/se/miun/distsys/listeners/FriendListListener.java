package se.miun.distsys.listeners;

import se.miun.distsys.messages.FriendList;

public interface FriendListListener {

    public void onIncomingFriendListMessage(FriendList friendList);
}
