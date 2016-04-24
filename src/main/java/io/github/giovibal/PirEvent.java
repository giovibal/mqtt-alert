package io.github.giovibal;

/**
 * Created by giovibal on 24/04/16.
 */
public class PirEvent {

    private long timestamp;
    private String msg;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return msg;
    }
}
