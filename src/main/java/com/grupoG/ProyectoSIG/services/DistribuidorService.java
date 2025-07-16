package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.models.Distribuidor;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.repositories.ClienteRepository;
import com.grupoG.ProyectoSIG.repositories.DistribuidorRepository;
import com.grupoG.ProyectoSIG.repositories.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DistribuidorService {
    @Autowired
    private DistribuidorRepository distribuidorRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UbicacionService ubicacionService;


    public <S extends Distribuidor> S save(S entity) {
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        Distribuidor distribuidor = new Distribuidor();
        Ubicacion ubicacion = new Ubicacion();

        distribuidor.setPassword(passwordEncoder.encode(entity.getPassword()));
        distribuidor.setEmail(entity.getEmail());
        distribuidor.setNombre(entity.getNombre());
        distribuidor.setEmail(entity.getEmail());
        distribuidor.setTelefono(entity.getTelefono());
        distribuidor.setDisponible(entity.getDisponible());
        distribuidor.setCapacidadCarga(entity.getCapacidadCarga());
        distribuidor.setTipoVehiculo(entity.getTipoVehiculo());
        ubicacion.setDireccion(entity.getDireccion());
        ubicacion.setLongitud(entity.getLongitud());
        ubicacion.setLatitud(entity.getLatitud());


        distribuidor.setUbicacionActual(ubicacion);

        return new DistribuidorResponseDTO(distribuidorRepository.save(distribuidor));
    }

    public List<DistribuidorResponseDTO> findAll() {
        return distribuidorRepository.findAll()
                .stream()
                .map(DistribuidorResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Distribuidor findById(long id){

        return distribuidorRepository.findById(id).orElseThrow(()-> new RuntimeException("Distribuidor no encontrado"));
    }

    public Optional<Distribuidor> getClienteById(Long id) {
        return distribuidorRepository.findById(id);
    }

    public Optional<Distribuidor> getMasCercano(Ubicacion ubicacion){
        Distribuidor cercano = distribuidorRepository.findAll().getFirst();
        return distribuidorRepository.findById(cercano.getId());
    }

    public Ubicacion getUbicacionById(Long id){
        Distribuidor distribuidor = findById(id);
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLatitud(-17.800576745238438);
        ubicacion.setLongitud(-63.18439910641412);
        Ubicacion saved = ubicacionService.save(ubicacion);
        return distribuidor.getUbicacionActual();
    }

    @Transactional
    public void cambiarDisponibilidad(Long id){
        Distribuidor distribuidor = distribuidorRepository.findById(id).orElseThrow();
        distribuidor.setDisponible(!distribuidor.getDisponible());
        distribuidorRepository.save(distribuidor);
    }

    @Transactional
    public void updateAllLocations() throws InterruptedException {
        List<Distribuidor> disponibles = distribuidorRepository.findByDisponibleTrue();
        if (disponibles.isEmpty()) {
            throw new RuntimeException("No hay distribuidores disponibles");
        }

        Map<String, Map<String, Object>> allLocations = firebaseDataService.getAllActiveDistributorLocations();

        for (Distribuidor distribuidor : disponibles) {
            String distribuidorId = String.valueOf(distribuidor.getId());

            if (allLocations.containsKey(distribuidorId)) {
                Map<String, Object> ubicacionMap = allLocations.get(distribuidorId);

                Double latitud = (Double) ubicacionMap.get("latitud");
                Double longitud = (Double) ubicacionMap.get("longitud");

                // Algunas veces Firebase serializa números como Long o Double dependiendo del valor
                if (latitud == null && ubicacionMap.get("latitud") instanceof Number) {
                    latitud = ((Number) ubicacionMap.get("latitud")).doubleValue();
                }
                if (longitud == null && ubicacionMap.get("longitud") instanceof Number) {
                    longitud = ((Number) ubicacionMap.get("longitud")).doubleValue();
                }

                if (latitud != null && longitud != null) {
                    Ubicacion ubicacion = distribuidor.getUbicacionActual();
                    System.out.println("obteniendo ubicacion del distribuidor: "+distribuidor.getEmail());
                    if (ubicacion == null) {
                        ubicacion = new Ubicacion(); // o lanzar error según tu modelo
                        distribuidor.setUbicacionActual(ubicacion);
                    }

                    ubicacion.setLatitud(latitud);
                    ubicacion.setLongitud(longitud);
                    System.out.println("guardando nueva ubicacion: "+ubicacion.getLongitud()+", "+ubicacion.getLatitud());
                    ubicacionRepository.save(ubicacion);
                    // opcional: también puedes setear el timestamp si lo necesitas
                }
            }
        }

        // Guardar todos los distribuidores (o usar saveAll si es más eficiente)
        distribuidorRepository.saveAll(disponibles);
    }
}
