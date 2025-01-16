package com.alurachallenge.literalura.repository;

import com.alurachallenge.literalura.model.Autor;
import com.alurachallenge.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {

}
