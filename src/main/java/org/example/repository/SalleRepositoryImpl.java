package org.example.repository;

import org.example.model.Reservation;
import org.example.model.Salle;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SalleRepositoryImpl implements SalleRepository {

    private EntityManager em;

    public SalleRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Salle> findDisponibles(LocalDateTime debut, LocalDateTime fin) {
        String jpql = "SELECT s FROM Salle s WHERE s.id NOT IN (" +
                "SELECT r.salle.id FROM Reservation r WHERE " +
                "(:debut < r.dateFin AND :fin > r.dateDebut))";
        TypedQuery<Salle> query = em.createQuery(jpql, Salle.class);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getResultList();
    }

    @Override
    public List<Salle> search(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Salle> cq = cb.createQuery(Salle.class);
        Root<Salle> salle = cq.from(Salle.class);
        List<Predicate> predicates = buildPredicates(cb, cq, salle, libelle, nbPlacesMin, debut, fin);
        cq.select(salle).where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.asc(salle.get("libelle")));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<Salle> search(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Salle> cq = cb.createQuery(Salle.class);
        Root<Salle> salle = cq.from(Salle.class);
        List<Predicate> predicates = buildPredicates(cb, cq, salle, libelle, nbPlacesMin, debut, fin);
        cq.select(salle).where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.asc(salle.get("libelle")));
        TypedQuery<Salle> query = em.createQuery(cq);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long count(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Salle> salle = cq.from(Salle.class);
        List<Predicate> predicates = new ArrayList<>();

        if (libelle != null && !libelle.isEmpty()) {
            predicates.add(cb.like(cb.lower(salle.get("libelle")), "%" + libelle.toLowerCase() + "%"));
        }
        if (nbPlacesMin != null) {
            predicates.add(cb.ge(salle.get("nbPlaces"), nbPlacesMin));
        }
        if (debut != null && fin != null) {
            Subquery<Long> sub = cq.subquery(Long.class);
            Root<Reservation> r = sub.from(Reservation.class);
            sub.select(r.get("salle").get("id"));
            sub.where(cb.and(
                    cb.lessThan(r.get("dateDebut"), fin),
                    cb.greaterThan(r.get("dateFin"), debut)
            ));
            predicates.add(cb.not(salle.get("id").in(sub)));
        }

        cq.select(cb.count(salle)).where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(cq).getSingleResult();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, CriteriaQuery<Salle> cq, Root<Salle> salle,
                                            String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin) {
        List<Predicate> predicates = new ArrayList<>();
        if (libelle != null && !libelle.isEmpty()) {
            predicates.add(cb.like(cb.lower(salle.get("libelle")), "%" + libelle.toLowerCase() + "%"));
        }
        if (nbPlacesMin != null) {
            predicates.add(cb.ge(salle.get("nbPlaces"), nbPlacesMin));
        }
        if (debut != null && fin != null) {
            Subquery<Long> sub = cq.subquery(Long.class);
            Root<Reservation> r = sub.from(Reservation.class);
            sub.select(r.get("salle").get("id"));
            sub.where(cb.and(
                    cb.lessThan(r.get("dateDebut"), fin),
                    cb.greaterThan(r.get("dateFin"), debut)
            ));
            predicates.add(cb.not(salle.get("id").in(sub)));
        }
        return predicates;
    }
}
