package org.example;

import org.example.model.Reservation;
import org.example.model.Salle;
import org.example.repository.SalleRepository;
import org.example.repository.SalleRepositoryImpl;
import org.example.service.SalleService;
import org.example.service.SalleServiceImpl;
import org.example.util.PageResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class SalleQueriesTest {

    private static EntityManagerFactory emf;

    @BeforeClass
    public static void init() {
        emf = Persistence.createEntityManagerFactory("SallePU");
    }

    @AfterClass
    public static void close() {
        if (emf != null) {
            emf.close();
        }
    }

    private String unique(String base) {
        return base + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    @Test
    public void testDisponiblesJPQL() {
        EntityManager em = emf.createEntityManager();
        SalleRepository repo = new SalleRepositoryImpl(em);

        String occupee = unique("Occupee");
        String libre = unique("Libre");
        LocalDateTime debut = LocalDateTime.of(2025, 6, 10, 9, 0);
        LocalDateTime fin = LocalDateTime.of(2025, 6, 10, 11, 0);

        em.getTransaction().begin();
        Salle s1 = new Salle(occupee, 20);
        Salle s2 = new Salle(libre, 20);
        em.persist(s1);
        em.persist(s2);
        em.persist(new Reservation(s1,
                LocalDateTime.of(2025, 6, 10, 10, 0),
                LocalDateTime.of(2025, 6, 10, 12, 0)));
        em.getTransaction().commit();

        List<Salle> dispos = repo.findDisponibles(debut, fin);
        assertFalse(dispos.isEmpty());
        assertTrue(dispos.stream().anyMatch(s -> s.getLibelle().equals(libre)));
        assertFalse(dispos.stream().anyMatch(s -> s.getLibelle().equals(occupee)));
        em.close();
    }

    @Test
    public void testSearchMultiCriteres() {
        EntityManager em = emf.createEntityManager();
        SalleRepository repo = new SalleRepositoryImpl(em);

        String prefix = unique("Python");
        em.getTransaction().begin();
        em.persist(new Salle(prefix + "_Lab", 40));
        em.persist(new Salle(prefix + "_Petit", 10));
        em.getTransaction().commit();

        List<Salle> large = repo.search(prefix.toLowerCase(), 30, null, null);
        assertEquals(1, large.size());
        assertEquals(40, large.get(0).getNbPlaces());

        List<Salle> all = repo.search(prefix.toLowerCase(), null, null, null);
        assertEquals(2, all.size());
        em.close();
    }

    @Test
    public void testPagination() {
        EntityManager em = emf.createEntityManager();
        SalleRepository repo = new SalleRepositoryImpl(em);

        String prefix = unique("Pag");
        em.getTransaction().begin();
        for (int i = 1; i <= 5; i++) {
            em.persist(new Salle(prefix + i, 10 + i));
        }
        em.getTransaction().commit();

        long total = repo.count(prefix.toLowerCase(), null, null, null);
        assertEquals(5, total);

        List<Salle> p1 = repo.search(prefix.toLowerCase(), null, null, null, 1, 2);
        List<Salle> p2 = repo.search(prefix.toLowerCase(), null, null, null, 2, 2);
        List<Salle> p3 = repo.search(prefix.toLowerCase(), null, null, null, 3, 2);
        assertEquals(2, p1.size());
        assertEquals(2, p2.size());
        assertEquals(1, p3.size());
        assertFalse(p1.get(0).getLibelle().equals(p2.get(0).getLibelle()));
        em.close();
    }

    @Test
    public void testServicePageResult() {
        EntityManager em = emf.createEntityManager();
        SalleService service = new SalleServiceImpl(new SalleRepositoryImpl(em));

        String prefix = unique("Yassine");
        em.getTransaction().begin();
        em.persist(new Salle(prefix + "A", 25));
        em.persist(new Salle(prefix + "B", 35));
        em.persist(new Salle(prefix + "C", 45));
        em.getTransaction().commit();

        PageResult<Salle> page = service.searchPage(prefix.toLowerCase(), 30, null, null, 1, 2);
        assertEquals(2, page.getTotal());
        assertEquals(1, page.getNbPages());
        assertEquals(2, page.getContenu().size());
        assertTrue(page.getContenu().stream().allMatch(s -> s.getNbPlaces() >= 30));
        em.close();
    }
}
