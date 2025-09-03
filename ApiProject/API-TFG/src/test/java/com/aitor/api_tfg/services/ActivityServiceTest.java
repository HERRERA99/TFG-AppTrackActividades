package com.aitor.api_tfg.services;

import com.aitor.api_tfg.mappers.ActivityMapper;
import com.aitor.api_tfg.model.db.Activity;
import com.aitor.api_tfg.model.db.Modalidades;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.model.dto.ActivityDTO;
import com.aitor.api_tfg.repositories.ActivityRepository;
import com.aitor.api_tfg.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivityMapper activityMapper;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void crearActividadDevuelveDtoSiEntradaValida() {
        String username = "testuser";
        User user = new User();
        ActivityDTO dtoEntrada = new ActivityDTO();
        Activity entidad = new Activity();
        Activity entidadGuardada = new Activity();
        ActivityDTO dtoSalida = new ActivityDTO();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(activityMapper.mapToActivityEntity(dtoEntrada, user)).thenReturn(entidad);
        when(activityRepository.save(entidad)).thenReturn(entidadGuardada);
        when(activityMapper.mapToActivityDTO(entidadGuardada)).thenReturn(dtoSalida);

        ActivityDTO resultado = activityService.createActivity(dtoEntrada, username);

        assertEquals(dtoSalida, resultado);
    }

    @Test
    void crearActividadLanzaExcepcionSiUsuarioNoExiste() {
        when(userRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                activityService.createActivity(new ActivityDTO(), "fantasma"));
    }

    @Test
    void obtenerActividadesDevuelveListaSiExistenActividades() {
        Activity actividad = new Activity();
        actividad.setActivityType(Modalidades.CICLISMO_CARRETERA);
        actividad.setRoute(List.of());

        ActivityDTO dto = new ActivityDTO();
        dto.setActivityType("CICLISMO_CARRETERA");
        dto.setRoute(List.of());

        when(activityRepository.findAll()).thenReturn(List.of(actividad));

        List<ActivityDTO> resultado = activityService.getActivities();

        assertEquals(1, resultado.size());
        assertEquals(dto, resultado.get(0));
    }

    @Test
    void obtenerActividadesLanzaExcepcionSiNoHayActividades() {
        when(activityRepository.findAll()).thenReturn(List.of());

        assertThrows(NoSuchElementException.class, () -> activityService.getActivities());
    }
}

