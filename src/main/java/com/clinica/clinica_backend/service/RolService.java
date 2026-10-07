package com.clinica.clinica_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinica.clinica_backend.entity.Rol;
import com.clinica.clinica_backend.repository.RolRepository;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> listar() {
        return rolRepository.findAll();
    }
}