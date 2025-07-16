package com.grupoG.ProyectoSIG.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupoG.ProyectoSIG.dto.RutaDTO;
import com.grupoG.ProyectoSIG.dto.UbicacionDTO;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RutaService {
    @Value("${ors.api-key}")
    private String apiKey;

    @Value("${ors.url}")
    private String orsUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RutaDTO calcularRutaV1(UbicacionDTO origen, UbicacionDTO destino) throws Exception {
        // Crear body JSON
        Map<String, Object> body = new HashMap<>();
        List<List<Double>> coordinates = List.of(
                List.of(origen.getLon(), origen.getLat()),
                List.of(destino.getLon(), destino.getLat())
        );
        body.put("coordinates", coordinates);

        // Crear headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // Hacer la petición
        ResponseEntity<String> response = restTemplate.postForEntity(orsUrl, request, String.class);

        // Parsear respuesta
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode geometry = root.path("routes").get(0).path("geometry");
        JsonNode summary = root.path("routes").get(0).path("summary");

        // Decodificar coordenadas si está en formato GeoJSON (LineString)
        List<UbicacionDTO> puntosRuta = new ArrayList<>();
        JsonNode coords = geometry.path("coordinates");
        for (JsonNode coord : coords) {
            UbicacionDTO punto = new UbicacionDTO();
            punto.setLon(coord.get(0).asDouble());
            punto.setLat(coord.get(1).asDouble());
            puntosRuta.add(punto);
        }

        RutaDTO ruta = new RutaDTO();
        ruta.setCoordenadas(puntosRuta);
        ruta.setDistanciaKm(summary.path("distance").asDouble() / 1000.0);
        ruta.setDuracionMin(summary.path("duration").asDouble() / 60.0);

        return ruta;
    }
    public RutaDTO calcularRuta(Ubicacion origen, Ubicacion destino) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("coordinates", List.of(
                    List.of(origen.getLongitud(), origen.getLatitud()),
                    List.of(destino.getLongitud(), destino.getLatitud())
            ));
            body.put("radiuses", List.of(5000.0, 5000.0));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.openrouteservice.org/v2/directions/driving-car",
                    entity,
                    Map.class
            );

            Map<String, Object> route = (Map<String, Object>) ((List<?>) response.getBody().get("routes")).get(0);
            Map<String, Object> summary = (Map<String, Object>) route.get("summary");

            double distanciaKm = ((Number) summary.get("distance")).doubleValue() / 1000.0;
            double duracionMin = ((Number) summary.get("duration")).doubleValue() / 60.0;

            String geometry = (String) route.get("geometry");
            List<UbicacionDTO> coordenadas = decodePolyline(geometry);

            RutaDTO resp = new RutaDTO();
            resp.setCoordenadas(coordenadas);
            resp.setDistanciaKm(distanciaKm);
            resp.setDuracionMin(duracionMin);
            return resp;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Las coordenadas no estan cercas de una ruta valida");
            }
            throw e;
        } catch (Exception e){
            throw new RuntimeException("Error al calcular la ruta: " + e.getMessage());
        }
    }




    /*public RutaDTO calcularRuta(Ubicacion origen, Ubicacion destino) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("coordinates", List.of(
                List.of(origen.getLongitud(), origen.getLatitud()),
                List.of(destino.getLongitud(), destino.getLatitud())
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openrouteservice.org/v2/directions/driving-car",
                entity,
                Map.class
        );

        Map<String, Object> route = (Map<String, Object>) ((List<?>) response.getBody().get("routes")).get(0);
        Map<String, Object> summary = (Map<String, Object>) route.get("summary");

        double distanciaKm = ((Number) summary.get("distance")).doubleValue() / 1000.0;
        double duracionMin = ((Number) summary.get("duration")).doubleValue() / 60.0;

        String geometry = (String) route.get("geometry");
        List<UbicacionDTO> coordenadas = decodePolyline(geometry);

        return new RutaDTO(coordenadas, distanciaKm, duracionMin);
    }

    private List<UbicacionDTO> decodePolyline(String encoded) {
        //System.out.println(encoded);
        //List<List<Double>> poly = new ArrayList<>();
        List<UbicacionDTO> ubicaciones = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double latitude = lat / 1E5;
            double longitude = lng / 1E5;
            //poly.add(Arrays.asList(longitude, latitude)); // ORS usa [lng, lat]
            ubicaciones.add(new UbicacionDTO(latitude, longitude));
        }
        return ubicaciones;
        //return poly;
    }
}
