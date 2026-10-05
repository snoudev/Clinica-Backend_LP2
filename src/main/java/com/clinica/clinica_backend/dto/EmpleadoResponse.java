package com.clinica.clinica_backend.dto;

import com.clinica.clinica_backend.entity.Empleado;

public record EmpleadoResponse(
        int idEmpleado,
        int idCargo,
        String cargo,
        Integer idEspecialidad,
        String especialidad,
        String nombre,
        String apellido,
        String dni,
        String telefono,
        boolean estado
) {

    public static EmpleadoResponse desde(Empleado empleado) {

        Integer idEspecialidad = null;
        String especialidad = null;

        if (empleado.getEspecialidad() != null) {
            idEspecialidad = empleado.getEspecialidad().getIdEspecialidad();
            especialidad = empleado.getEspecialidad().getNombre();
        }

        return new EmpleadoResponse(
                empleado.getIdEmpleado(),
                empleado.getCargo().getIdCargo(),
                empleado.getCargo().getNombreCargo(),
                idEspecialidad,
                especialidad,
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getDni(),
                empleado.getTelefono(),
                empleado.isEstado()
        );
    }
}
