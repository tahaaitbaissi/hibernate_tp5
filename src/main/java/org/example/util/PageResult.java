package org.example.util;

import java.util.List;

public class PageResult<T> {

    private List<T> contenu;
    private int page;
    private int taille;
    private int nbPages;
    private long total;

    public PageResult(List<T> contenu, int page, int taille, long total) {
        this.contenu = contenu;
        this.page = page;
        this.taille = taille;
        this.total = total;
        this.nbPages = (int) Math.ceil((double) total / taille);
    }

    public List<T> getContenu() {
        return contenu;
    }

    public int getPage() {
        return page;
    }

    public int getTaille() {
        return taille;
    }

    public int getNbPages() {
        return nbPages;
    }

    public long getTotal() {
        return total;
    }
}
