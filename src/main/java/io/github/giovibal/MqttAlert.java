package io.github.giovibal;


/**
 * Created by Giovanni Baleani on 10/01/16.
 */
public class MqttAlert {

    private MqttClientManager mqttClient;


    public static void main(String[] args) {
        final MqttAlert mqttAlert = new MqttAlert();
        mqttAlert.connectAndSubscribe("tcp://104.154.60.150:1883", "/baleani/laspio/pir1");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Thread.sleep(200);
                    System.out.println("Shouting down ...");

                    mqttAlert.disconnect();
                    System.out.println("Disconnected");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void connectAndSubscribe(String broker, String topic) {
        mqttClient = new MqttClientManager(broker, topic, new DefaultMqttCallback());
        mqttClient.connectAndSubscribe();
        mqttClient.startConnectionWatchdog(1);
    }

    public void disconnect() {
        mqttClient.disconnect();
    }

}
