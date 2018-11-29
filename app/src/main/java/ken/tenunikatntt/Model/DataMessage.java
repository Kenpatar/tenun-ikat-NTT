package ken.tenunikatntt.Model;

import java.util.HashMap;

/**
 * Created by Emilken18 on 11/29/2018.
 */

public class DataMessage {

    public String to;
    public HashMap<String, String> data;

    public DataMessage() {
    }

    public DataMessage(String to, HashMap<String, String> data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }
}
