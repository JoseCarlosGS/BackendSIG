package com.grupoG.ProyectoSIG.services;

import org.springframework.stereotype.Service;
import com.google.firebase.database.*;
import com.google.api.core.ApiFuture;
import com.google.firebase.database.DatabaseReference;
import java.util.concurrent.CountDownLatch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
@Service
public class FirebaseDataService {
    public Map<String, Object> getActiveDistributorLocation(String distributorId) throws InterruptedException {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("distribuidores_activos/" + distributorId);

        final Map<String, Object>[] result = new Map[1];
        CountDownLatch latch = new CountDownLatch(1);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    System.out.println("Datos del distribuidor " + distributorId + ": " + snapshot.getValue());
                    result[0] = (Map<String, Object>) snapshot.getValue();
                } else {
                    System.out.println("Distribuidor " + distributorId + " no encontrado o inactivo.");
                    result[0] = null;
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al leer datos de Firebase: " + error.getMessage());
                latch.countDown();
            }
        });

        latch.await(); // Bloquea hasta recibir respuesta
        return result[0];
    }

    public Map<String, Map<String, Object>> getAllActiveDistributorLocations() throws InterruptedException {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("distribuidores_activos");

        Map<String, Map<String, Object>> allDistributors = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(1);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String distributorId = childSnapshot.getKey();
                        Map<String, Object> data = (Map<String, Object>) childSnapshot.getValue();
                        allDistributors.put(distributorId, data);
                    }
                    System.out.println("Todos los distribuidores activos: " + allDistributors);
                } else {
                    System.out.println("No hay distribuidores activos.");
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al obtener los datos: " + error.getMessage());
                latch.countDown();
            }
        });

        latch.await(); // Espera a que se reciba la respuesta
        return allDistributors;
    }

    // Ejemplo de cómo escribir/actualizar datos desde Spring Boot (útil para pruebas o administración)
    public void updateDistributorLocationFromBackend(String distributorId, double latitud, double longitud) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("distribuidores_activos").child(distributorId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("latitud", latitud);
        updates.put("longitud", longitud);
        updates.put("timestamp", ServerValue.TIMESTAMP); // Utiliza el timestamp del servidor de Firebase

        ref.updateChildrenAsync(updates).addListener(new Runnable() {
            @Override
            public void run() {
                System.out.println("Ubicación del distribuidor " + distributorId + " actualizada desde backend.");
            }
        }, Runnable::run); // Ejecutar en el mismo hilo
    }
}
