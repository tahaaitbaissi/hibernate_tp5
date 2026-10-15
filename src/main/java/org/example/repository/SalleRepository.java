package org.example.repository;

import org.example.model.Salle;

import java.time.LocalDateTime;
import java.util.List;

public interface SalleRepository {
    List<Salle> findDisponibles(LocalDateTime debut, LocalDateTime fin);
    List<Salle> search(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin);
    List<Salle> search(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin, int page, int size);
    long count(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin);
}
