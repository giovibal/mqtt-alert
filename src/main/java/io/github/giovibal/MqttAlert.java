package io.github.giovibal;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Giovanni Baleani on 10/01/16.
 */
public class MqttAlert implements MqttCallback {

    private MqttClient mqttClient;
    private int alertsCountPer1Minute;
    private long lastAlertTime;


    public static void main(String[] args) {
        MqttAlert mqttAlert = new MqttAlert();
        mqttAlert.connectAndSubscribe("tcp://104.154.60.150:1883", "/baleani/laspio/pir1");
    }

    public void connectAndSubscribe(String broker, String topic) {
        String clientId     = "MqttAlert";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(20);
            mqttClient.setCallback(this);

            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected");

            mqttClient.subscribe(topic);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        Thread.sleep(200);
                        System.out.println("Shouting down ...");

                        disconnect();
                        System.out.println("Disconnected");
//                        System.exit(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch(MqttException me) {
            handleException(me);
        }

    }
    public void disconnect() {
        try {
            if(mqttClient!=null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                System.out.println("Disconnected");
            }
        } catch(MqttException me) {
            handleException(me);
        }

    }

    private void handleException(MqttException me) {
        System.out.println("reason "+me.getReasonCode());
        System.out.println("msg "+me.getMessage());
        System.out.println("loc "+me.getLocalizedMessage());
        System.out.println("cause "+me.getCause());
        System.out.println("excep "+me);
        me.printStackTrace();
    }


    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost ...");
        throwable.printStackTrace();
    }

    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String msg = new String(mqttMessage.getPayload(), "UTF-8");
        System.out.println(topic +" "+ msg);
        if(msg.contains("Presenza rilevata")) {
            long currentAlertTime = System.currentTimeMillis();
            long timeDiff = currentAlertTime - lastAlertTime;
            if(timeDiff <= /*1 minute*/ 1000*60) {
                alertsCountPer1Minute++;
            } else {
                alertsCountPer1Minute = 1;
            }
            lastAlertTime = currentAlertTime;
            System.out.println("Count per minute: "+ alertsCountPer1Minute);
            playSound();
        }
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Delivery complete");
    }

    private void playSound() {
        try {
            // Open an audio input stream.
            // http://www.wavsource.com/sfx/sfx.htm
            InputStream soundFile = getClass().getResourceAsStream("/car_chirp_x.wav");
            InputStream soundFileBuffered = new BufferedInputStream(soundFile);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFileBuffered);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
