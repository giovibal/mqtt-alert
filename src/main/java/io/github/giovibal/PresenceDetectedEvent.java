package io.github.giovibal;

/**
 * Created by giovibal on 01/05/16.
 */
public class PresenceDetectedEvent extends PirEvent {

    public PresenceDetectedEvent() {}
    public PresenceDetectedEvent(String msg) {
        setMsg(msg);
    }
    public PresenceDetectedEvent(PirEvent e) {
        setMsg(e.getMsg());
        setTimestamp(e.getTimestamp());
    }

}
