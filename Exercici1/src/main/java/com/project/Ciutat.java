package com.project;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ciutats")
public class Ciutat implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ciutatId", unique=true, nullable=false)
    private long ciutatId;
    private String nom;
    private String pais;
    private int poblacio;
    
    @OneToMany(mappedBy = "ciutat",  fetch = FetchType.EAGER)
    private Set<Ciutada> ciutadans = new HashSet<>();
    
    public Ciutat() {
        this.ciutadans = new HashSet<>();
    }
    
    public Ciutat(String nom, String pais, int poblacio) {
        this.nom = nom;
        this.pais = pais;
        this.poblacio = poblacio;
        this.ciutadans = new HashSet<>();
    }

    public long getCiutatId() {
        return ciutatId;
    }

    public void setCiutatId(long ciutatId) {
        this.ciutatId = ciutatId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public int getPoblacio() {
        return poblacio;
    }

    public void setPoblacio(int poblacio) {
        this.poblacio = poblacio;
    }

    public Set<Ciutada> getCiutadans() {
        return ciutadans;
    }

    public void setCiutadans(Set<Ciutada> ciutadans) {
        this.ciutadans = ciutadans;
    }
    
    public void addCiutada(Ciutada ciutada) {
        ciutadans.add(ciutada);
        ciutada.setCiutat(this);
    }

    public void removeCiutada(Ciutada ciutada) {
        ciutadans.remove(ciutada);
        ciutada.setCiutat(null);
    }
    
     @Override
    public String toString() {
    StringBuilder str = new StringBuilder();
    for (Ciutada ciutada : ciutadans) {
        if (str.length() > 0) {
            str.append(" | ");
        }
        str.append(ciutada.getNom());
    }
    return this.getCiutatId() + ": " + this.getNom() + ", " + this.getPais() + ", " + this.getPoblacio() + ", Ciutadans: [" + str + "]";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Ciutat ciutat = (Ciutat) o;
        return ciutatId == ciutat.ciutatId;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(ciutatId);
    }    
}