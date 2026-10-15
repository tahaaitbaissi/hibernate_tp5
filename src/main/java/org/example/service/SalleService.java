package org.example.service;

import org.example.model.Salle;
import org.example.util.PageResult;

import java.time.LocalDateTime;
import java.util.List;

public interface SalleService {
    List<Salle> findDisponibles(LocalDateTime debut, LocalDateTime fin);
    List<Salle> search(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin, int page, int size);
    PageResult<Salle> searchPage(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin, int page, int size);
    long count(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin);
}
