package org.example.service;

import org.example.model.Salle;
import org.example.repository.SalleRepository;
import org.example.util.PageResult;

import java.time.LocalDateTime;
import java.util.List;

public class SalleServiceImpl implements SalleService {

    private SalleRepository repository;

    public SalleServiceImpl(SalleRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Salle> findDisponibles(LocalDateTime debut, LocalDateTime fin) {
        return repository.findDisponibles(debut, fin);
    }

    @Override
    public List<Salle> search(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin, int page, int size) {
        return repository.search(libelle, nbPlacesMin, debut, fin, page, size);
    }

    @Override
    public PageResult<Salle> searchPage(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin, int page, int size) {
        List<Salle> contenu = repository.search(libelle, nbPlacesMin, debut, fin, page, size);
        long total = repository.count(libelle, nbPlacesMin, debut, fin);
        return new PageResult<>(contenu, page, size, total);
    }

    @Override
    public long count(String libelle, Integer nbPlacesMin, LocalDateTime debut, LocalDateTime fin) {
        return repository.count(libelle, nbPlacesMin, debut, fin);
    }
}
