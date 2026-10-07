package com.clinica.clinica_backend.service;

import com.clinica.clinica_backend.entity.Cargo;
import com.clinica.clinica_backend.repository.CargoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;

    public CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    public List<Cargo> listar() {
        return cargoRepository.findAll();
    }
}