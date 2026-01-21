package hotel.model;

/**
 * Klasa encji reprezentująca pokój hotelowy.
 */
public class Pokoj {
    
    public enum StatusPokoju {
        DOSTEPNY,
        ZAJETY,
        W_SPRZATANIU,
        NIEDOSTEPNY
    }
    
    private int numer;
    private String typ;
    private double cena;
    private StatusPokoju status;
    
    public Pokoj(int numer, String typ, double cena) {
        this.numer = numer;
        this.typ = typ;
        this.cena = cena;
        this.status = StatusPokoju.DOSTEPNY;
    }
    
    public int getNumer() { return numer; }
    public String getTyp() { return typ; }
    public double getCena() { return cena; }
    public StatusPokoju getStatus() { return status; }
    
    public void setStatus(StatusPokoju status) { this.status = status; }
    public void setCena(double cena) { this.cena = cena; }
    
    public boolean czyDostepny() {
        return status == StatusPokoju.DOSTEPNY;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return numer == ((Pokoj) o).numer;
    }
    
    @Override
    public int hashCode() { return Integer.hashCode(numer); }
    
    @Override
    public String toString() {
        return "Pokoj{numer=" + numer + ", typ='" + typ + "', cena=" + cena + ", status=" + status + "}";
    }
}
