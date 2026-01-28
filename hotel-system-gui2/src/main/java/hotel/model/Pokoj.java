package hotel.model;

import java.util.Objects;

/**
 * Klasa reprezentująca pokój hotelowy.
 * Zawiera podstawowe informacje o pokoju: numer, typ i cenę bazową.
 */
public class Pokoj {
    
    private int numer;
    private String typ;
    private double cena;
    
    /**
     * Konstruktor tworzący nowy pokój.
     * @param numer numer pokoju
     * @param typ typ pokoju (np. "Jednoosobowy", "Dwuosobowy", "Apartament")
     * @param cena cena bazowa za dobę
     */
    public Pokoj(int numer, String typ, double cena) {
        if (numer <= 0) {
            throw new IllegalArgumentException("Numer pokoju musi być większy od 0");
        }
        if (typ == null || typ.trim().isEmpty()) {
            throw new IllegalArgumentException("Typ pokoju nie może być pusty");
        }
        if (cena < 0) {
            throw new IllegalArgumentException("Cena nie może być ujemna");
        }
        this.numer = numer;
        this.typ = typ;
        this.cena = cena;
    }
    
    /**
     * Zwraca numer pokoju.
     * @return numer pokoju
     */
    public int getNumer() {
        return numer;
    }
    
    /**
     * Zwraca typ pokoju.
     * @return typ pokoju
     */
    public String getTyp() {
        return typ;
    }
    
    /**
     * Zwraca cenę bazową pokoju za dobę.
     * @return cena bazowa
     */
    public double getCenaBazowa() {
        return cena;
    }
    
    /**
     * Ustawia cenę bazową pokoju.
     * @param cena nowa cena bazowa
     */
    public void setCena(double cena) {
        if (cena < 0) {
            throw new IllegalArgumentException("Cena nie może być ujemna");
        }
        this.cena = cena;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokoj pokoj = (Pokoj) o;
        return numer == pokoj.numer;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(numer);
    }
    
    @Override
    public String toString() {
        return "Pokoj{" +
                "numer=" + numer +
                ", typ='" + typ + '\'' +
                ", cena=" + cena +
                '}';
    }
}
