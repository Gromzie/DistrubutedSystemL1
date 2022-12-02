package se.miun.distsys.messages;
import java.util.Vector;

public class VecMessage extends Message{

    private Vector<Integer> vec;

    public Vector<Integer> getClock() {
        return vec;
    }

    public VecMessage(Vector<Integer> clock){
        this.vec = clock;
    }
}