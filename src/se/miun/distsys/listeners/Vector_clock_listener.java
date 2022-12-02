package se.miun.distsys.listeners;

import se.miun.distsys.messages.VecMessage;

public interface Vector_clock_listener {

    public void onIncomingVecMessage(VecMessage vecMessage);
}
