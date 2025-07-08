package it.uniupo.pissir.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTClient {
    MqttClient client;

    public MQTTClient() {
        try {
            client = new MqttClient(
                    "tcp://broker.emqx.io:1883",
                    MqttClient.generateClientId(),
                    new MemoryPersistence()
            );
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        try {
            client.setCallback(new SubscribeCallback());
            client.connect();

            // Iscrizione ai topic MQTT
            client.subscribe("mezzo/batteria/+", 0);
            client.subscribe("mezzo/status/+", 0);

            System.out.println("MQTT Client connesso e iscritto ai topic.");

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MQTTClient client = new MQTTClient();
        client.start();
    }
}
