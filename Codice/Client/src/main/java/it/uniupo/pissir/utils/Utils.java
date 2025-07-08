package it.uniupo.pissir.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    /**
     * <h2>Convertitore double to int</h2>
     * Converte i valori di tipo Double in Integer se il valore Double è un intero (es. 5.0 diventa 5).
     * Questa funzione è ricorsiva e gestisce anche mappe annidate e liste.
     *
     * @param map La mappa da processare.
     * @return Una nuova mappa con i valori convertiti.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertDoublesToInts(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Double) {
                Double d = (Double) value;
                if (d == Math.floor(d)) {
                    // è un intero "in double"
                    result.put(entry.getKey(), d.intValue());
                } else {
                    result.put(entry.getKey(), d);
                }
            } else if (value instanceof Map) {
                // ricorsivamente processa mappe annidate
                result.put(entry.getKey(), convertDoublesToInts((Map<String, Object>) value));
            } else if (value instanceof List) {
                // se ci sono liste, processa i loro elementi
                result.put(entry.getKey(), processList((List<Object>) value));
            } else {
                result.put(entry.getKey(), value);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> processList(List<Object> list) {
        List<Object> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Double) {
                Double d = (Double) item;
                if (d == Math.floor(d)) {
                    result.add(d.intValue());
                } else {
                    result.add(d);
                }
            } else if (item instanceof Map) {
                result.add(convertDoublesToInts((Map<String, Object>) item));
            } else if (item instanceof List) {
                result.add(processList((List<Object>) item));
            } else {
                result.add(item);
            }
        }
        return result;
    }
}
