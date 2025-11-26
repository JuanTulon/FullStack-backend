package com.joyeria.joyeria.repository;

import com.joyeria.joyeria.model.RolUsuario;
import com.joyeria.joyeria.model.RolUsuario.NombreRol;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Integer> {

    Optional<RolUsuario> findByNombreRol(NombreRol nombreRol);
}
