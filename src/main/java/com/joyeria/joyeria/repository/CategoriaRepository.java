package com.joyeria.joyeria.repository;

import com.joyeria.joyeria.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    @Query("SELECT c FROM Categoria c WHERE c.nombreCategoria = :nombreCategoria")
    List<Categoria> buscarPorNombre(@Param("nombreCategoria") String nombreCategoria);


}
