package hotel.model;

/**
 * Klasa reprezentująca dodatek śniadaniowy do rezerwacji.
 * Implementuje interfejs IDodatek (wzorzec Dekorator).
 */
public class Sniadanie implements IDodatek {
    
    private double cena;
    private int liczbaDni;
    
    /**
     * Konstruktor tworzący dodatek śniadaniowy.
     * @param cena cena śniadania za dzień
     * @param liczbaDni liczba dni, na które zamówiono śniadanie
     */
    public Sniadanie(double cena, int liczbaDni) {
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
     * Konstruktor z domyślną ceną śniadania (50 zł).
     * @param liczbaDni liczba dni, na które zamówiono śniadanie
     */
    public Sniadanie(int liczbaDni) {
        this(50.0, liczbaDni);
    }
    
    @Override
    public double obliczDodatkowyKoszt() {
        return cena * liczbaDni;
    }
    
    @Override
    public String getOpis() {
        return "Śniadanie (" + liczbaDni + " dni x " + cena + " zł)";
    }
    
    /**
     * Zwraca cenę za jedno śniadanie.
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
