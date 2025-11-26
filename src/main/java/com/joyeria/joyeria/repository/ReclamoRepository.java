package com.joyeria.joyeria.repository;

import com.joyeria.joyeria.model.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReclamoRepository extends JpaRepository<Reclamo, Integer> {
    
    @Query("SELECT r FROM Reclamo r WHERE r.problema = :problema")
    Reclamo buscarPorProblema(@Param("problema") String problema);
}
