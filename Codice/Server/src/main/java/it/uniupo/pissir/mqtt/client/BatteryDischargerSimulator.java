package it.uniupo.pissir.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.web.client.RestTemplate;

public class BatteryDischargerSimulator {

    public static void main(String[] args) {
        try {
            final int idMezzo = 2;
            final String broker = "tcp://broker.emqx.io:1883";
            final String topic = "mezzo/batteria/" + idMezzo;

            MqttClient client = new MqttClient(broker, MqttClient.generateClientId(), new MemoryPersistence());
            client.connect();

            //RestTemplate restTemplate = new RestTemplate();

            /* Da qui si può recuperare la batteria dal DB, ma per la simulazione metto un valore fisso essendo che il dispositivo conosce da se la batteria del proprio mezzo
            int batteria=0;
            try{
                String getUrl = "http://localhost:4567/api/v1.0/batteriaMezzo?id_mezzo=" + idMezzo;
                String risposta = restTemplate.getForObject(getUrl, String.class);
                assert risposta != null;
                batteria = Integer.parseInt(risposta.replaceAll("[^0-9]", ""));
                //System.out.println("Senza fare conversioni" + Integer.parseInt(risposta));
                System.out.println(batteria);
            }catch (Exception e){
                System.out.println(e.getMessage() + " Non è possibile recuperare la batteria dal DB");
                e.printStackTrace();
            }
            */

            int batteria = 100; // Simulazione con batteria al 100%

            System.out.println("🔧 Simulazione avviata per mezzo ID " + idMezzo + " con batteria " + batteria + "%");

            while (batteria > 0) {
                // Pubblica solo via MQTT
                String payload = String.valueOf(batteria);
                client.publish(topic, new MqttMessage(payload.getBytes()));
                System.out.println("Pubblicato su " + topic + ": " + batteria + "%");

                Thread.sleep(5000);
                batteria--;
            }

            System.out.println("Batteria esaurita. Simulazione terminata per mezzo ID " + idMezzo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
