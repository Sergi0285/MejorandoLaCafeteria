package com.disenocreativo.Diseno.Config;

import com.disenocreativo.Diseno.Entidad.administrador;
import com.disenocreativo.Diseno.Entidad.cafeteria;
import com.disenocreativo.Diseno.Entidad.producto;
import com.disenocreativo.Diseno.Entidad.interaccion;
import com.disenocreativo.Diseno.Entidad.aviso;
import com.disenocreativo.Diseno.Entidad.role;

import com.disenocreativo.Diseno.Repositorio.administradorCRUDrepositorio;
import com.disenocreativo.Diseno.Repositorio.cafeteriaCRUDrepositorio;
import com.disenocreativo.Diseno.Repositorio.productoCRUDRepositorio;
import com.disenocreativo.Diseno.Repositorio.interaccionCRUDrepositorio;
import com.disenocreativo.Diseno.Repositorio.avisoCRUDrepositorio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

@Component
public class DataLoader implements CommandLineRunner {

    private final administradorCRUDrepositorio administradorRepo;
    private final cafeteriaCRUDrepositorio cafeteriaRepo;
    private final productoCRUDRepositorio productoRepo;
    private final interaccionCRUDrepositorio interaccionRepo;
    private final avisoCRUDrepositorio avisoRepo;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(administradorCRUDrepositorio administradorRepo,
                    cafeteriaCRUDrepositorio cafeteriaRepo,
                    productoCRUDRepositorio productoRepo,
                    interaccionCRUDrepositorio interaccionRepo,
                    avisoCRUDrepositorio avisoRepo,
                    PasswordEncoder passwordEncoder) {
        this.administradorRepo = administradorRepo;
        this.cafeteriaRepo = cafeteriaRepo;
        this.productoRepo = productoRepo;
        this.interaccionRepo = interaccionRepo;
        this.avisoRepo = avisoRepo;
        this.passwordEncoder = passwordEncoder;
    }

    private byte[] readImage(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            System.err.println("Error leyendo la imagen " + path + ": " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Anotar el método run() como @Transactional para que todas las entidades
     * recuperadas con repo.findAll() se mantengan adjuntas (attached) y no se trate de re-persistir
     * objetos ya existentes.
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1) CARGAR ADMINISTRADORES (solo si no hay ninguno en la BD)
        if (administradorRepo.count() == 0) {
            administrador admin1 = administrador.builder()
                    .nombre("Juan Perez")
                    .correo("juan@correo.com")
                    .telefono("555-1111")
                    .password(passwordEncoder.encode("Contrasena1"))
                    .rol(role.ADMIN)
                    .build();

            administrador admin2 = administrador.builder()
                    .nombre("Maria Lopez")
                    .correo("maria@correo.com")
                    .telefono("555-2222")
                    .password(passwordEncoder.encode("Contrasena1"))
                    .rol(role.ADMIN)
                    .build();

            administrador admin3 = administrador.builder()
                    .nombre("Carlos Ruiz")
                    .correo("carlos@correo.com")
                    .telefono("555-3333")
                    .password(passwordEncoder.encode("Contrasena1"))
                    .rol(role.ADMIN)
                    .build();

            administradorRepo.saveAll(List.of(admin1, admin2, admin3));
        }

        // Obtengo TODOS los administradores (ahora ya existen)
        List<administrador> admins = administradorRepo.findAll();

        // 2) CARGAR CAFETERÍAS (solo si no hay ninguna en la BD)
        if (cafeteriaRepo.count() == 0) {
            cafeteria cafe1 = new cafeteria();
            cafe1.setIdCafeteria(1);
            cafe1.setNombreCafeteria("Cafeteria Principal");
            cafe1.setHorarioApertura(LocalTime.of(8, 0));
            cafe1.setHorarioCierre(LocalTime.of(20, 0));
            cafe1.setUbicacion("Al lado del bloque A");
            cafe1.setAdministrador(admins.get(0));
            cafe1.setLogoCafeteria(readImage("static/Recursos/img/universidad.jpg"));

            cafeteria cafe2 = new cafeteria();
            cafe2.setIdCafeteria(2);
            cafe2.setNombreCafeteria("Cafeteria Prime");
            cafe2.setHorarioApertura(LocalTime.of(7, 30));
            cafe2.setHorarioCierre(LocalTime.of(22, 0));
            cafe2.setUbicacion("Bajo el bloque del prime al lado de las sombrillas azules");
            cafe2.setAdministrador(admins.get(1));
            cafe2.setLogoCafeteria(readImage("static/Recursos/img/universidad.jpg"));

            cafeteria cafe3 = new cafeteria();
            cafe3.setIdCafeteria(3);
            cafe3.setNombreCafeteria("Sombrillas azules");
            cafe3.setHorarioApertura(LocalTime.of(9, 0));
            cafe3.setHorarioCierre(LocalTime.of(19, 0));
            cafe3.setUbicacion("Cerca a la cafeteria del prime frente a la biblioteca");
            cafe3.setAdministrador(admins.get(2));
            cafe3.setLogoCafeteria(readImage("static/Recursos/img/universidad.jpg"));

            cafeteriaRepo.saveAll(List.of(cafe1, cafe2, cafe3));
        }

        // Obtengo todas las cafeterías (ahora ya existen)
        List<cafeteria> cafes = (List<cafeteria>) cafeteriaRepo.findAll();

        // 3) CARGAR PRODUCTOS (solo si no hay ninguno en la BD)
        if (productoRepo.count() == 0) {
            // Productos de ejemplo anteriores
            producto p1 = new producto(
                "Café Americano",
                "Café negro simple",
                25.0,
                null,
                "no",
                false,
                readImage("static/Recursos/img/plato1.jpg"),
                10,
                cafes.get(0)
            );

            producto p2 = new producto(
                "Tostada",
                "Tostada con mantequilla",
                15.0,
                null,
                "no",
                false,
                readImage("static/Recursos/img/plato2.jpg"),
                20,
                cafes.get(0)
            );

            producto p3 = new producto(
                "Smoothie de Fresa",
                "Smoothie natural",
                30.0,
                null,
                "no",
                false,
                readImage("static/Recursos/img/plato3.jpg"),
                5,
                cafes.get(0)
            );

            producto p4 = new producto(
                "Café Latte",
                "Café con leche",
                35.0,
                null,
                "no",
                false,
                readImage("static/Recursos/img/plato1.jpg"),
                15,
                cafes.get(1)
            );

            producto p5 = new producto(
                "Sandwich",
                "Sandwich mixto",
                40.0,
                null,
                "no",
                false,
                readImage("static/Recursos/img/plato2.jpg"),
                8,
                cafes.get(1)
            );

            producto p6 = new producto(
                "Ensalada Bowl",
                "Ensalada saludable",
                50.0,
                null,
                "no",
                false,
                readImage("static/Recursos/img/plato3.jpg"),
                7,
                cafes.get(1)
            );

            producto p7 = new producto(
                "Espresso",
                "Café fuerte",
                20.0,
                null,
                "no",
                false,
                readImage("static/Recursos/img/plato1.jpg"),
                12,
                cafes.get(2)
            );

            producto p8 = new producto(
                "Muffin",
                "Muffin de chocolate",
                18.0,
                null,
                "no",
                false,
                readImage("static/Recursos/img/plato2.jpg"),
                25,
                cafes.get(2)
            );

            producto p9 = new producto(
                "Jugo Natural",
                "Jugo de naranja",
                22.0,
                null,
                "no",
                false,
                readImage("static/Recursos/img/plato3.jpg"),
                30,
                cafes.get(2)
            );

            // ------------------------
            // NUEVOS PRODUCTOS: ALMUERZOS (solo en Cafetería 1)
            // ------------------------
            cafeteria cafeUno = cafes.get(0);

            // 1) Arroz (Carbohidrato), disponible LUN,MAR,MIER,JUE,VIER
            producto lunch1 = new producto(
                "Arroz",
                "LUN,MAR,MIER,JUE,VIER",
                4600.0,
                "Carbohidrato",
                "Almuerzo",
                false,
                null,
                0,
                cafeUno
            );

            // 2) Papa (Carbohidrato), disponible LUN,MIER,VIER
            producto lunch2 = new producto(
                "Papa",
                "LUN,MIER,VIER",
                4600.0,
                "Carbohidrato",
                "Almuerzo",
                false,
                null,
                0,
                cafeUno
            );

            // 3) Tomate (Ensalada), disponible LUN,MIER,VIER
            producto lunch3 = new producto(
                "Tomate",
                "LUN,MIER,VIER",
                4600.0,
                "Ensalada",
                "Almuerzo",
                false,
                null,
                0,
                cafeUno
            );

            // 4) Res (Proteína), disponible LUN,MAR,MIER,VIER
            producto lunch4 = new producto(
                "Res",
                "LUN,MAR,MIER,VIER",
                4600.0,
                "Proteina",
                "Almuerzo",
                false,
                null,
                0,
                cafeUno
            );

            // 5) Jugo Tropical (Jugo), disponible LUN,MAR,MIER,JUE,VIER
            producto lunch5 = new producto(
                "Jugo Tropical",
                "LUN,MAR,MIER,JUE,VIER",
                4600.0,
                "Jugo",
                "Almuerzo",
                false,
                null,
                0,
                cafeUno
            );

            // ------------------------
            // NUEVOS PRODUCTOS: DESAYUNO (solo en Cafetería 1)
            // ------------------------

            producto desayuno = new producto(
                "Panqueques",
                null,
                4600.0,
                null,
                "Desayuno",
                false,
                null,
                0,
                cafeUno
            );

            // Guardar TODO en un único saveAll()
            List<producto> todos = new ArrayList<>();
            todos.addAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9));
            todos.addAll(List.of(lunch1, lunch2, lunch3, lunch4, lunch5));
            todos.add(desayuno);

            productoRepo.saveAll(todos);
        }

        // Obtengo todos los productos (ahora ya existen y siguen “attached” por la misma transacción)
        List<producto> todosProductos = productoRepo.findAll();

        // 4) CARGAR INTERACCIONES (solo si no hay ninguna en la BD)
        if (interaccionRepo.count() == 0) {
            Random rnd = new Random();
            for (producto p : todosProductos) {
                // Como p fue obtenido con findAll() dentro de la misma transacción,
                // se mantiene “attached” y no se lanza DetachedEntityException.
                int meGusta = rnd.nextInt(100);
                int noGusta = rnd.nextInt(50);
                interaccion inter = new interaccion(p);
                inter.setMeGusta(meGusta);
                inter.setNoGusta(noGusta);
                interaccionRepo.save(inter);
            }
        }

        // 5) CARGAR AVISOS (solo si no hay ninguno en la BD)
        if (avisoRepo.count() == 0) {
            List<cafeteria> cafeterias = (List<cafeteria>) cafeteriaRepo.findAll();
            aviso av1 = new aviso(cafeterias.get(0), readImage("static/Recursos/img/principal-post.jpg"), LocalDate.now().minusDays(2));
            aviso av2 = new aviso(cafeterias.get(1), readImage("static/Recursos/img/prime-post.jpg"), LocalDate.now().minusDays(1));
            aviso av3 = new aviso(cafeterias.get(2), readImage("static/Recursos/img/sombrillas-post.jpeg"), LocalDate.now());

            avisoRepo.saveAll(List.of(av1, av2, av3));
        }

        System.out.println("Datos de prueba cargados correctamente.");
    }
}
