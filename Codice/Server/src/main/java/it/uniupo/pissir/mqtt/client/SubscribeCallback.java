package it.uniupo.pissir.mqtt.client;

import com.google.gson.Gson;
import it.uniupo.pissir.mqtt.Message;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;

public class SubscribeCallback implements MqttCallback {

    public void connectionLost(Throwable cause) {
        System.out.println("Connessione MQTT persa: " + cause.getMessage());
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        RestTemplate rest = new RestTemplate();
        Gson gson = new Gson();

        String msg = message.toString();

        // Log
        System.out.println("Messaggio dal topic '" + topic + "': " + msg);

        try {
            if (topic.startsWith("mezzo/batteria/")) {
                String idMezzo = topic.split("/")[2];
                int batteria = Integer.parseInt(msg);

                rest.exchange(
                        "http://localhost:4567/api/v1.0/batteriaMezzo?id_mezzo=" + idMezzo + "&batteria=" + batteria,
                        HttpMethod.PUT,
                        HttpEntity.EMPTY,
                        Void.class
                );
                System.out.println("Batteria aggiornata per mezzo " + idMezzo + ": " + batteria + "%");

            } else if (topic.startsWith("mezzo/status/")) {
                String[] parts = topic.split("/");
                String idMezzo = parts[2];
                String stato = parts[3];

                rest.exchange(
                        "http://localhost:4567/api/v1.0/updateStatoMezzo?id_mezzo=" + idMezzo + "&stato=" + stato,
                        HttpMethod.PUT,
                        HttpEntity.EMPTY,
                        Void.class
                );

                System.out.println("✅ Stato aggiornato per mezzo " + idMezzo + ": " + stato);

            }/* else if (topic.equals("parkingLot/sensor")) {
                Message mqttMessage = gson.fromJson(msg, Message.class);
                String[] filters = mqttMessage.getMsg().split(" ");
                String id = filters[3];
                String availability = filters[1].equals("left") ? "0" : "1";

                rest.exchange(
                        "http://localhost:4567/api/v1.0/parkingSpots?id=" + id + "&value=" + availability,
                        HttpMethod.PUT,
                        HttpEntity.EMPTY,
                        Void.class
                );
            }   else if (topic.equals("parkingLot/MWbot")) {
                String json = rest.getForObject("http://localhost:4567/api/v1.0/recharges?username=" + msg, String.class);
                if (json == null) return;

                String[] parts = json.split("\\{");
                json = "{" + parts[2].substring(0, parts[2].length() - 1);
                Recharge recharge = gson.fromJson(json, Recharge.class);

                recharge.setCompleted(1);
                HttpEntity<Recharge> entity = new HttpEntity<>(recharge);

                rest.exchange("http://localhost:4567/api/v1.0/recharges?username=" + msg, HttpMethod.PUT, entity, Void.class);

                if (recharge.getNotification() == 1) {
                    String date = LocalDate.now() + " " + LocalTime.now().getHour() + ":" + LocalTime.now().getMinute();
                    Notification notification = new Notification(recharge.getUsername(), "La ricarica della tua auto è completata.", date);
                    HttpEntity<Notification> notReq = new HttpEntity<>(notification);
                    rest.postForObject("http://localhost:4567/api/v1.0/notifications", notReq, Notification.class);
                }

                System.out.println("✅ Ricarica completata per " + recharge.getUsername());
            }*/

        } catch (HttpClientErrorException e) {
            System.out.println("REST API Error per topic " + topic + ": " + e.getStatusCode());
        }
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        // Not used for subscriber
    }
}
