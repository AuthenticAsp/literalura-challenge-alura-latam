package com.alurachallenge.literalura.principal;

import com.alurachallenge.literalura.model.Autor;
import com.alurachallenge.literalura.model.DatosConsulta;
import com.alurachallenge.literalura.model.DatosLibro;
import com.alurachallenge.literalura.model.Libro;
import com.alurachallenge.literalura.repository.AutorRepository;
import com.alurachallenge.literalura.repository.LibrosRepository;
import com.alurachallenge.literalura.service.ConsumirApi;
import com.alurachallenge.literalura.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumirApi consumoApi = new ConsumirApi();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibrosRepository libroRepo;
    private AutorRepository autorRepo;
    private List<Libro> librosBD;
    private List<Autor> autoresBD;



    public Principal(LibrosRepository libroRepo, AutorRepository autorRepo) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;

    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    ***********************************************
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    ***********************************************
                    Ingrese una opción:
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroWeb();
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    listarAutores();
                    break;
                case 4:
                    listarAutoresVivosEnAnio();
                    break;
                case 5:
                    listarLibroPorIdioma();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("Introduce el año a buscar: ");
        var anio = teclado.nextInt();
        autoresBD = autorRepo.findAll();
        autoresBD.stream()
                .filter(autor -> autor.getNacimiento() != null && autor.getFallecimiento() != null)
                .filter(autor -> autor.getNacimiento() <= anio && autor.getFallecimiento() >= anio)
                .forEach(autor -> {
                    System.out.println("------------- AUTOR -------------");
                    System.out.println("Nombre: " + autor.getNombre());
                    System.out.println("Nacimiento: " + autor.getNacimiento());
                    System.out.println("Fallecimiento: " + autor.getFallecimiento());
                    System.out.print("Libros: [");
                    StringBuilder libros = new StringBuilder();
                    autor.getLibros().forEach(libro -> libros.append(libro.getTitulo()).append(", "));
                    if (libros.length() > 0) {
                        libros.setLength(libros.length() - 2);
                    }
                    System.out.println(libros + "]");
                    System.out.println("---------------------------------\n");
                });
    }

    private void listarAutores() {
        autoresBD = autorRepo.findAll();
        autoresBD.forEach(autor -> {
            System.out.println("------------- AUTOR -------------");
            System.out.println("Nombre: " + autor.getNombre());
            System.out.println("Nacimiento: " + autor.getNacimiento());
            System.out.println("Fallecimiento: " + autor.getFallecimiento());
            System.out.print("Libros: [");
            StringBuilder libros = new StringBuilder();
            autor.getLibros().forEach(libro -> libros.append(libro.getTitulo()).append(", "));
            if (libros.length() > 0) {
                libros.setLength(libros.length() - 2);
            }
            System.out.println(libros + "]");
            System.out.println("---------------------------------\n");
        });
    }

    private void listarLibroPorIdioma() {
        System.out.println("Introduce el idioma del libro a buscar: ");
        var idioma = teclado.nextLine();
        librosBD = libroRepo.findAll();
        librosBD.stream()
                .filter(libro -> libro.getIdiomas().contains(idioma))
                .forEach(libro -> {
                    System.out.println("------------- LIBRO -------------");
                    System.out.println("Título: " + libro.getTitulo());
                    System.out.println("Idiomas: " + libro.getIdiomas());
                    System.out.println("Descargas: " + libro.getDescargas());
                    System.out.print("Autor/Autores: [");
                    StringBuilder autores = new StringBuilder();
                    libro.getAutor().forEach(autor -> autores.append(autor.getNombre()).append(", "));
                    if (autores.length() > 0) {
                        autores.setLength(autores.length() - 2);
                    }
                    System.out.println(autores + "]");
                    System.out.println("---------------------------------\n");
                });
    }

    private void listarLibros() {
        librosBD = libroRepo.findAll();
        librosBD.forEach(libro -> {
            System.out.println("------------- LIBRO -------------");
            System.out.println("Título: " + libro.getTitulo());
            System.out.println("Idiomas: " + libro.getIdiomas());
            System.out.println("Descargas: " + libro.getDescargas());
            System.out.print("Autor/Autores: [");
            StringBuilder autores = new StringBuilder();
            libro.getAutor().forEach(autor -> autores.append(autor.getNombre()).append(", "));
            if (autores.length() > 0) {
                autores.setLength(autores.length() - 2);
            }
            System.out.println(autores + "]");
            System.out.println("---------------------------------\n");

        });
    }

    private void buscarLibroWeb() {
        System.out.println("Introduce el título del libro a buscar: ");
        var titulo = teclado.nextLine();
        try {
            var json = consumoApi.consumirApi(URL_BASE + titulo.replace(" ", "+"));
            DatosConsulta datosConsulta = conversor.obtenerDatos(json, DatosConsulta.class);

            DatosLibro datosLibro = datosConsulta.resultados().stream().findFirst().orElse(null);
            if (datosLibro != null) {
                if (libroEnBaseDatos(datosLibro)) {
                    System.out.println("El libro ya se encuentra en la base de datos");
                    return;
                } else {
                    Libro libro = new Libro(datosLibro);
                    List<Autor> autores = datosLibro.autor().stream()
                            .map(autor -> new Autor(autor))
                            .collect(Collectors.toList());

                    autoresBD = autorRepo.findAll();
                    var si = false;

                    for (Autor autor : autores) {
                        Optional<Autor> autorBD = autoresBD.stream()
                                .filter(autorBase -> autorBase.getNombre().equals(autor.getNombre()))
                                .findFirst();
                        if (autorBD.isPresent()) {
                            var autorEncontrado = autorBD.get();
                            autorEncontrado.addLibro(libro);
                            List<Autor> autoresLibro = new ArrayList<>();
                            autoresLibro.add(autorEncontrado);
                            libro.setAutor(autoresLibro);
                            autorRepo.save(autorEncontrado);
                            si = true;
                            autores.remove(autor);
                        }
                    }

                    if (!autores.isEmpty()) {
                        if (si) {
                            librosBD = libroRepo.findAll();
                            Optional<Libro> libroBaseDatos = librosBD.stream()
                                    .filter(libroBase -> libroBase.getTitulo().equals(libro.getTitulo()))
                                    .findFirst();
                            if (libroBaseDatos.isPresent()) {
                                var libroEncontrado = libroBaseDatos.get();
                                libroEncontrado.setAutor(autores);
                                for (Autor autor : autores) {
                                    autor.addLibro(libroEncontrado);
                                }
                                libroRepo.save(libroEncontrado);
                                autorRepo.saveAll(autores);
                            }

                        } else {
                            libro.setAutor(autores);

                            for (Autor autor : autores) {
                                autor.addLibro(libro);
                            }

                            autorRepo.saveAll(autores);

                        }


                    } else {
                        libroRepo.save(libro);
                    }

                    System.out.println("Libro guardado en la base de datos\n");

                    //imprimir el libro
                    System.out.println("------------- LIBRO -------------");
                    System.out.println("Título: " + libro.getTitulo());
                    System.out.println("Idiomas: " + libro.getIdiomas());
                    System.out.println("Descargas: " + libro.getDescargas());
                    System.out.print("Autor/Autores: [");
                    StringBuilder autoresNuevo = new StringBuilder();
                    libro.getAutor().forEach(autor -> autoresNuevo.append(autor.getNombre()).append(", "));
                    if (autoresNuevo.length() > 0) {
                        autoresNuevo.setLength(autoresNuevo.length() - 2);
                    }
                    System.out.println(autoresNuevo + "]");
                    System.out.println("---------------------------------\n");


                }
            } else {
                System.out.println("No se encontraron libros con ese título\n");
            }

        } catch (Exception e) {
            System.out.println("Error al consumir la API: " + e.getMessage());
        }
    }

    private boolean libroEnBaseDatos(DatosLibro datosLibro) {
        librosBD = libroRepo.findAll();
        for (Libro libro : librosBD) {
            if (libro.getTitulo().equals(datosLibro.titulo())) {
                return true;
            }
        }
        return false;
    }
}
