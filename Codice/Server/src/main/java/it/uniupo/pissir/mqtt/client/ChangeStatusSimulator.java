package it.uniupo.pissir.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ChangeStatusSimulator {

    public static void main(String[] args) {
        try {
            final int idMezzo = 2;
            final String nuovoStato = "NON_DISPONIBILE";//Stati possibili di cambiamento dall'MQTT sono : PRELEVABILE o NON_DISPONIBILE

            final String broker = "tcp://broker.emqx.io:1883";

            MqttClient client = new MqttClient(broker, MqttClient.generateClientId(), new MemoryPersistence());
            client.connect();

            String topic = "mezzo/status/" + idMezzo + "/" + nuovoStato;

            MqttMessage message = new MqttMessage(nuovoStato.getBytes());
            client.publish(topic, message);

            System.out.println("Stato simulato pubblicato su " + topic + ": " + nuovoStato);

            client.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}