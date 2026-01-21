package hotel.model;

/**
 * Klasa reprezentująca dodatek parkingowy do rezerwacji.
 * Implementuje interfejs IDodatek (wzorzec Dekorator).
 */
public class Parking implements IDodatek {
    
    private double cena;
    private int liczbaDni;
    
    /**
     * Konstruktor tworzący dodatek parkingowy.
     * @param cena cena parkingu za dzień
     * @param liczbaDni liczba dni parkowania
     */
    public Parking(double cena, int liczbaDni) {
        if (cena < 0) {
            throw new IllegalArgumentException("Cena nie może być ujemna");
        }
        if (liczbaDni <= 0) {
            throw new IllegalArgumentException("Liczba dni musi być większa od 0");
        }
        this.cena = cena;
        this.liczbaDni = liczbaDni;
    }
    
    /**
     * Konstruktor z domyślną ceną parkingu (30 zł/dzień).
     * @param liczbaDni liczba dni parkowania
     */
    public Parking(int liczbaDni) {
        this(30.0, liczbaDni);
    }
    
    @Override
    public double obliczDodatkowyKoszt() {
        return cena * liczbaDni;
    }
    
    @Override
    public String getOpis() {
        return "Parking (" + liczbaDni + " dni x " + cena + " zł)";
    }
    
    /**
     * Zwraca cenę za jeden dzień parkingu.
     * @return cena jednostkowa
     */
    public double getCena() {
        return cena;
    }
    
    /**
     * Zwraca liczbę dni.
     * @return liczba dni
     */
    public int getLiczbaDni() {
        return liczbaDni;
    }
}
