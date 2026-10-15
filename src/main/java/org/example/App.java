package org.example;

import org.example.model.Reservation;
import org.example.model.Salle;
import org.example.repository.SalleRepositoryImpl;
import org.example.service.SalleService;
import org.example.service.SalleServiceImpl;
import org.example.util.PageResult;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SallePU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Salle atlas = new Salle("Atlas", 20);
        Salle toubkal = new Salle("Toubkal", 50);
        Salle sahara = new Salle("Sahara", 30);
        Salle yassine = new Salle("Yassine", 15);
        Salle pythonLab = new Salle("Python Lab", 40);
        em.persist(atlas);
        em.persist(toubkal);
        em.persist(sahara);
        em.persist(yassine);
        em.persist(pythonLab);

        LocalDateTime debut = LocalDateTime.of(2025, 6, 1, 9, 0);
        LocalDateTime fin = LocalDateTime.of(2025, 6, 1, 11, 0);
        em.persist(new Reservation(atlas, debut, fin));
        em.persist(new Reservation(sahara,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                LocalDateTime.of(2025, 6, 1, 12, 0)));

        em.getTransaction().commit();

        SalleService service = new SalleServiceImpl(new SalleRepositoryImpl(em));

        System.out.println("--- Disponibles " + debut + " -> " + fin + " ---");
        List<Salle> disponibles = service.findDisponibles(debut, fin);
        for (Salle s : disponibles) {
            System.out.println(s.getLibelle() + " (" + s.getNbPlaces() + " places)");
        }

        System.out.println("--- Recherche libelle=a, min=20 ---");
        List<Salle> recherche = service.search("a", 20, null, null, 1, 10);
        for (Salle s : recherche) {
            System.out.println(s.getLibelle() + " (" + s.getNbPlaces() + " places)");
        }

        System.out.println("--- Page 1 (taille 2) ---");
        PageResult<Salle> p1 = service.searchPage(null, null, null, null, 1, 2);
        for (Salle s : p1.getContenu()) {
            System.out.println(s.getLibelle());
        }
        System.out.println("total=" + p1.getTotal() + " pages=" + p1.getNbPages());

        System.out.println("--- Page 2 (taille 2) ---");
        PageResult<Salle> p2 = service.searchPage(null, null, null, null, 2, 2);
        for (Salle s : p2.getContenu()) {
            System.out.println(s.getLibelle());
        }
        System.out.println("total=" + p2.getTotal() + " pages=" + p2.getNbPages());

        em.close();
        emf.close();
    }
}
