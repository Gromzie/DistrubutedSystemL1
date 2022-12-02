package se.miun.distsys.messages;

public class vector_clock extends Message{
    String clock = "";
    public vector_clock(String clock) {
        this.clock = clock;
    }
}
