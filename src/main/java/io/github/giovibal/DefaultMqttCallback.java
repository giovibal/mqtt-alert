package io.github.giovibal;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by giovibal on 24/04/16.
 */
public class DefaultMqttCallback implements MqttCallback {


    private int alertsCountPer1Minute;
    private long lastAlertTime;
    private CepManager cepManager;
    private SoundPlayer soundPlayer;
    private MqttClientManager mqttClient;

    public DefaultMqttCallback(MqttClientManager mqttClient) {
        cepManager = new CepManager(mqttClient);
        soundPlayer = new SoundPlayer();
        cepManager.setGlobal("soundPlayer", soundPlayer);
    }

    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost ...");
        throwable.printStackTrace();
    }

    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String msg = new String(mqttMessage.getPayload(), "UTF-8");
//        System.out.println(topic +" "+ msg);
        if(msg.contains("Presenza rilevata")) {
            long currentAlertTime = System.currentTimeMillis();
            long timeDiff = currentAlertTime - lastAlertTime;
            if(timeDiff <= /*1 minute*/ 1000*60) {
                alertsCountPer1Minute++;
            } else {
                alertsCountPer1Minute = 1;
            }
            lastAlertTime = currentAlertTime;
//            System.out.println("Count per minute: "+ alertsCountPer1Minute);
//            if(alertsCountPer1Minute > 1) {
//                soundPlayer.playAlertSound();
//            } else {
//                soundPlayer.playWarningSound();
//            }
        }

        // CEP
        PirEvent pirEvent = new PirEvent();
        pirEvent.setMsg(msg);
        pirEvent.setTimestamp(System.currentTimeMillis());
        cepManager.insertEvent(pirEvent);
    }


    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Delivery complete");
    }


}
