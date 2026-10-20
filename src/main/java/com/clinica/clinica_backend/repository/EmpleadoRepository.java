package com.clinica.clinica_backend.repository;

import com.clinica.clinica_backend.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    boolean existsByDni(String dni);

    boolean existsByDniAndIdEmpleadoNot(String dni, int idEmpleado);

    List<Empleado> findByCargoNombreCargoAndEstadoTrue(String nombreCargo);

    List<Empleado> findByCargoNombreCargoAndEstadoTrueAndEspecialidadIdEspecialidad(
            String nombreCargo, int idEspecialidad);
}
