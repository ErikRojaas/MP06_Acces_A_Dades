package com.project.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exemplars")
public class Exemplar implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="exemplarId", unique=true, nullable=false)
    private Long exemplarId;

    @Column(nullable = false, length = 100)
    private String codiBarres;
    
    @Column(nullable = false, length = 10)
    private boolean disponible;

    @ManyToOne
    @JoinColumn(name = "llibre_id")
    private Llibre llibre;

    @ManyToOne
    @JoinColumn(name = "biblioteca_id")
    private Biblioteca biblioteca;

    @OneToMany(mappedBy = "exemplar", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Prestec> historialPrestecs = new HashSet<>();

    public Exemplar() {
    // Constructor por defecto
    }

    public Exemplar(String codiBarres, Llibre llibre, Biblioteca biblioteca) {
        this.codiBarres = codiBarres;
        this.llibre = llibre;
        this.biblioteca = biblioteca;
        this.historialPrestecs = new HashSet<>();
        this.disponible = true;
    }

    public Long getExemplarId() {
        return exemplarId;
    }

    public void setExemplarId(Long exemplarId) {
        this.exemplarId = exemplarId;
    }

    public String getCodiBarres() {
        return codiBarres;
    }

    public void setCodiBarres(String codiBarres) {
        this.codiBarres = codiBarres;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Llibre getLlibre() {
        return llibre;
    }

    public void setLlibre(Llibre llibre) {
        this.llibre = llibre;
    }

    public Biblioteca getBiblioteca() {
        return biblioteca;
    }

    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    public Set<Prestec> getHistorialPrestecs() {
        return historialPrestecs;
    }

    public void setHistorialPrestecs(Set<Prestec> historialPrestecs) {
        this.historialPrestecs = historialPrestecs;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Exemplar[id=%d, codi='%s', disponible=%s", 
            exemplarId, codiBarres, disponible));
        
        if (llibre != null) {
            sb.append(String.format(", llibre='%s'", llibre.getTitol()));
        }
        
        if (biblioteca != null) {
            sb.append(String.format(", biblioteca='%s'", biblioteca.getNom()));
        }
        
        if (!historialPrestecs.isEmpty()) {
            long prestectsActius = historialPrestecs.stream()
                .filter(p -> p.isActiu())
                .count();
            sb.append(String.format(", prestecs=%d (actius=%d)", 
                historialPrestecs.size(), prestectsActius));
        }
        
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exemplar exemplar = (Exemplar) o;
        return exemplarId == exemplar.exemplarId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(exemplarId);
    }
}