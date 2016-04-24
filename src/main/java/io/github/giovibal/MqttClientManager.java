package io.github.giovibal;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by giovibal on 24/04/16.
 */
public class MqttClientManager {

    private MqttClient mqttClient;
    private String broker;
    private String topic;
    private MqttCallback mqttCallback;


    public MqttClientManager(String broker, String topic, MqttCallback mqttCallback) {
        this.broker = broker;
        this.topic = topic;
        this.mqttCallback = mqttCallback;
    }


    public void connectAndSubscribe() {
        String clientId     = "MqttAlert";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(20);
            mqttClient.setCallback(mqttCallback);

            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected");

            mqttClient.subscribe(topic);

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

    public void startConnectionWatchdog(long pollSeconds) {
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                if(mqttClient!=null && !mqttClient.isConnected()) {
                    connectAndSubscribe();
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(t, 1000, pollSeconds*1000);

    }

    private void handleException(MqttException me) {
        System.out.println("reason "+me.getReasonCode());
        System.out.println("msg "+me.getMessage());
        System.out.println("loc "+me.getLocalizedMessage());
        System.out.println("cause "+me.getCause());
        System.out.println("excep "+me);
        me.printStackTrace();
    }


}
