package com.joyeria.joyeria.repository;

import com.joyeria.joyeria.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{

    @Query("select p from Producto p where p.nombreProducto=:nom")
    List<Producto> buscarPorNombre(@Param("nom") String nombreProducto);

    @Query("select p from Producto p where p.idProducto=:id")
    Optional<Producto> buscarPorId(@Param("id") Integer idProducto);
}
