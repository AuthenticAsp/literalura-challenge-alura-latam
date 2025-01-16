package com.alurachallenge.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String titulo;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> idiomas;
    private Integer descargas;
    @ManyToMany(mappedBy = "libros", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Autor> autor;

    public Libro(){}

    public Libro (DatosLibro datosLibro){
        this.titulo = datosLibro.titulo();
        this.idiomas = datosLibro.idiomas();
        this.descargas = datosLibro.descargas();
    }

    @Override
    public String toString() {
        return "--------- LIBRO ---------"+
                "Id=" + Id +
                ", titulo='" + titulo + '\'' +
                ", idiomas=" + idiomas +
                ", descargas=" + descargas +
                ", autor=" + autor +
                "-------------------------";
    }

    public void addAutor(Autor autor){
        if(this.autor == null){
            this.autor = new ArrayList<>();
        }
        this.autor.add(autor);
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public List<Autor> getAutor() {
        return autor;
    }

    public void setAutor(List<Autor> autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }

    public Integer getDescargas() {
        return descargas;
    }

    public void setDescargas(Integer descargas) {
        this.descargas = descargas;
    }
}
