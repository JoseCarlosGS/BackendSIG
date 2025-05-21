package com.grupoG.ProyectoSIG.config;

import com.grupoG.ProyectoSIG.dto.UbicacionDTO;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // De Ubicacion a UbicacionDTO
        mapper.addMappings(new PropertyMap<Ubicacion, UbicacionDTO>() {
            @Override
            protected void configure() {
                map().setLat(source.getLatitud());
                map().setLon(source.getLongitud());
            }
        });

        // De UbicacionDTO a Ubicacion
        mapper.addMappings(new PropertyMap<UbicacionDTO, Ubicacion>() {
            @Override
            protected void configure() {
                map().setLatitud(source.getLat());
                map().setLongitud(source.getLon());
            }
        });

        return mapper;
    }
}
