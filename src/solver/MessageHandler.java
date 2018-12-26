package solver;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler {

    private List<String> outgoingMessages;
    private List<String> incommingMessages;
    private List<String> sentMessages;



    public MessageHandler(){
        outgoingMessages = new ArrayList<>();
        incommingMessages = new ArrayList<>();
        sentMessages = new ArrayList<>();
    }


    public List<String> getOutgoingMessages() {
        return outgoingMessages;
    }

    public List<String> getIncommingMessages() {
        return incommingMessages;
    }
}
