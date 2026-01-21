package hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa encji reprezentująca rezerwację hotelową.
 * Warstwa: ENCJA (Entity)
 */
public class Rezerwacja {
    
    /**
     * Enum określający możliwe statusy rezerwacji.
     */
    public enum StatusRezerwacji {
        NOWA,           // Rezerwacja właśnie utworzona
        POTWIERDZONA,   // Rezerwacja potwierdzona przez gościa/hotel
        ZAMELDOWANA,    // Gość się zameldował
        WYMELDOWANA,    // Gość się wymeldował
        ANULOWANA       // Rezerwacja anulowana
    }
    
    private int id;
    private LocalDate dataOd;
    private LocalDate dataDo;
    private Pokoj pokoj;
    private Gosc gosc;
    private StatusRezerwacji status;
    private List<IDodatek> dodatki;
    
    // ========== KONSTRUKTORY ==========
    
    public Rezerwacja() {
        this.status = StatusRezerwacji.NOWA;
        this.dodatki = new ArrayList<>();
    }
    
    public Rezerwacja(int id, LocalDate dataOd, LocalDate dataDo, Pokoj pokoj, Gosc gosc) {
        this();
        this.id = id;
        setDataOd(dataOd);
        setDataDo(dataDo);
        this.pokoj = pokoj;
        this.gosc = gosc;
    }
    
    // ========== METODY BIZNESOWE ==========
    
    /**
     * Oblicza całkowitą cenę rezerwacji (pokój + dodatki).
     * @return cena rezerwacji
     */
    public double obliczCene() {
        if (pokoj == null || dataOd == null || dataDo == null) {
            return 0.0;
        }
        
        long liczbaDni = obliczLiczbeDni();
        double cenaZaNoc = pokoj.getCena();
        
        // Dodaj koszty dodatków
        for (IDodatek dodatek : dodatki) {
            cenaZaNoc += dodatek.obliczDodatkowyKoszt();
        }
        
        return liczbaDni * cenaZaNoc;
    }
    
    /**
     * Oblicza liczbę dni (nocy) rezerwacji.
     * @return liczba dni
     */
    public long obliczLiczbeDni() {
        if (dataOd == null || dataDo == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dataOd, dataDo);
    }
    
    /**
     * Sprawdza czy rezerwację można anulować.
     * @return true jeśli można anulować
     */
    public boolean czyMoznaAnulowac() {
        return status == StatusRezerwacji.NOWA || 
               status == StatusRezerwacji.POTWIERDZONA;
    }
    
    /**
     * Anuluje rezerwację (zmienia status).
     * @return true jeśli anulowano pomyślnie
     */
    public boolean anuluj() {
        if (!czyMoznaAnulowac()) {
            return false;
        }
        this.status = StatusRezerwacji.ANULOWANA;
        return true;
    }
    
    /**
     * Oblicza ile dni zostało do rozpoczęcia rezerwacji.
     * @return liczba dni do rezerwacji (może być ujemna jeśli już minęła)
     */
    public long dniDoRozpoczecia() {
        if (dataOd == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dataOd);
    }
    
    // ========== ZARZĄDZANIE DODATKAMI ==========
    
    public void dodajDodatek(IDodatek dodatek) {
        if (dodatek != null) {
            this.dodatki.add(dodatek);
        }
    }
    
    public boolean usunDodatek(IDodatek dodatek) {
        return this.dodatki.remove(dodatek);
    }
    
    public List<IDodatek> pobierzDodatki() {
        return new ArrayList<>(dodatki);
    }
    
    public void wyczyscDodatki() {
        this.dodatki.clear();
    }
    
    // ========== GETTERY I SETTERY ==========
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LocalDate getDataOd() {
        return dataOd;
    }
    
    public void setDataOd(LocalDate dataOd) {
        this.dataOd = dataOd;
    }
    
    public LocalDate getDataDo() {
        return dataDo;
    }
    
    public void setDataDo(LocalDate dataDo) {
        this.dataDo = dataDo;
    }
    
    public Pokoj getPokoj() {
        return pokoj;
    }
    
    public void setPokoj(Pokoj pokoj) {
        this.pokoj = pokoj;
    }
    
    public Gosc getGosc() {
        return gosc;
    }
    
    public void setGosc(Gosc gosc) {
        this.gosc = gosc;
    }
    
    public StatusRezerwacji getStatus() {
        return status;
    }
    
    public void setStatus(StatusRezerwacji status) {
        this.status = status;
    }
    
    // ========== EQUALS, HASHCODE, TOSTRING ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rezerwacja that = (Rezerwacja) o;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "Rezerwacja{" +
                "id=" + id +
                ", dataOd=" + dataOd +
                ", dataDo=" + dataDo +
                ", pokoj=" + (pokoj != null ? pokoj.getNumer() : "brak") +
                ", gosc=" + (gosc != null ? gosc.getPelneNazwisko() : "brak") +
                ", status=" + status +
                '}';
    }
}
