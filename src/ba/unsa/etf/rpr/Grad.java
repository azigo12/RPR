package ba.unsa.etf.rpr;

public class Grad implements Comparable {
    private int id, postanskiBroj;
    private String naziv;
    private int brojStanovnika;
    private Drzava drzava;
    private String slika;

    public Grad() {
    }

    public Grad(int id, String nazivGrada, int brojStanovnika, Drzava drzava, String slika, int pb) {
        this.id = id;
        this.naziv = nazivGrada;
        this.brojStanovnika = brojStanovnika;
        this.drzava = drzava;
        this.slika = slika;
        this.postanskiBroj = pb;
    }

    public String getSlika() {
        return slika;
    }

    public void setSlika(String slika) {
        this.slika = slika;
    }

    public int getPostanskiBroj() {
        return postanskiBroj;
    }

    public void setPostanskiBroj(int postanskiBroj) {
        this.postanskiBroj = postanskiBroj;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        Grad g = (Grad) o;
        return Integer.compare(((Grad) o).getBrojStanovnika(), this.brojStanovnika);
    }

    @Override
    public String toString() {
        if(this.getDrzava() == null) return this.naziv + " ()" + " - " + this.brojStanovnika;
        return this.naziv + " (" + this.getDrzava().getNaziv() + ")" + " - " + this.brojStanovnika;
    }
}
