package hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Klasa reprezentująca rezerwację hotelową.
 * Łączy gościa z pokojem i przechowuje informacje o datach pobytu oraz dodatkach.
 */
public class Rezerwacja {
    
    public enum Status {
        NOWA, POTWIERDZONA, ZAMELDOWANA, WYMELDOWANA, ANULOWANA
    }
    
    private int id;
    private LocalDate dataOd;
    private LocalDate dataDo;
    private Gosc gosc;
    private Pokoj pokoj;
    private List<IDodatek> dodatki;
    private Status status;
    private String przyczynaAnulowania;
    
    private static int nextId = 1;
    
    /**
     * Konstruktor tworzący nową rezerwację.
     * @param dataOd data rozpoczęcia pobytu
     * @param dataDo data zakończenia pobytu
     * @param gosc gość dokonujący rezerwacji
     * @param pokoj rezerwowany pokój
     */
    public Rezerwacja(LocalDate dataOd, LocalDate dataDo, Gosc gosc, Pokoj pokoj) {
        validateDates(dataOd, dataDo);
        if (gosc == null) {
            throw new IllegalArgumentException("Gość nie może być null");
        }
        if (pokoj == null) {
            throw new IllegalArgumentException("Pokój nie może być null");
        }
        
        this.id = nextId++;
        this.dataOd = dataOd;
        this.dataDo = dataDo;
        this.gosc = gosc;
        this.pokoj = pokoj;
        this.dodatki = new ArrayList<>();
        this.status = Status.NOWA;
    }
    
    /**
     * Konstruktor z podanym ID (do odtwarzania z bazy danych).
     */
    public Rezerwacja(int id, LocalDate dataOd, LocalDate dataDo, Gosc gosc, Pokoj pokoj) {
        validateDates(dataOd, dataDo);
        if (id <= 0) {
            throw new IllegalArgumentException("ID musi być większe od 0");
        }
        if (gosc == null) {
            throw new IllegalArgumentException("Gość nie może być null");
        }
        if (pokoj == null) {
            throw new IllegalArgumentException("Pokój nie może być null");
        }
        
        this.id = id;
        this.dataOd = dataOd;
        this.dataDo = dataDo;
        this.gosc = gosc;
        this.pokoj = pokoj;
        this.dodatki = new ArrayList<>();
        this.status = Status.NOWA;
    }
    
    private void validateDates(LocalDate dataOd, LocalDate dataDo) {
        if (dataOd == null || dataDo == null) {
            throw new IllegalArgumentException("Daty nie mogą być null");
        }
        if (dataOd.isAfter(dataDo)) {
            throw new IllegalArgumentException("Data rozpoczęcia nie może być późniejsza niż data zakończenia");
        }
        if (dataOd.isEqual(dataDo)) {
            throw new IllegalArgumentException("Rezerwacja musi trwać co najmniej jeden dzień");
        }
    }
    
    /**
     * Zwraca ID rezerwacji.
     * @return identyfikator rezerwacji
     */
    public int getId() {
        return id;
    }
    
    /**
     * Zwraca datę rozpoczęcia pobytu.
     * @return data od
     */
    public LocalDate getDataOd() {
        return dataOd;
    }
    
    /**
     * Zwraca datę zakończenia pobytu.
     * @return data do
     */
    public LocalDate getDataDo() {
        return dataDo;
    }
    
    /**
     * Zwraca gościa przypisanego do rezerwacji.
     * @return gość
     */
    public Gosc getGosc() {
        return gosc;
    }
    
    /**
     * Zwraca zarezerwowany pokój.
     * @return pokój
     */
    public Pokoj getPokoj() {
        return pokoj;
    }
    
    /**
     * Zwraca status rezerwacji.
     * @return status
     */
    public Status getStatus() {
        return status;
    }
    
    /**
     * Ustawia status rezerwacji.
     * @param status nowy status
     */
    public void setStatus(Status status) {
        this.status = status;
    }
    
    /**
     * Oblicza liczbę nocy rezerwacji.
     * @return liczba nocy
     */
    public long getLiczbaNocy() {
        return ChronoUnit.DAYS.between(dataOd, dataDo);
    }
    
    /**
     * Oblicza całkowitą cenę rezerwacji (pokój + dodatki).
     * @return całkowita cena
     */
    public double obliczCene() {
        double cenaBazowa = pokoj.getCenaBazowa() * getLiczbaNocy();
        double cenaDodatkow = 0;
        for (IDodatek dodatek : dodatki) {
            cenaDodatkow += dodatek.obliczDodatkowyKoszt();
        }
        return cenaBazowa + cenaDodatkow;
    }
    
    /**
     * Dodaje dodatek do rezerwacji.
     * @param dodatek dodatek do dodania
     * @return true jeśli dodano pomyślnie
     */
    public boolean dodajDodatek(IDodatek dodatek) {
        if (dodatek == null) {
            return false;
        }
        if (status == Status.WYMELDOWANA || status == Status.ANULOWANA) {
            return false;
        }
        return dodatki.add(dodatek);
    }
    
    /**
     * Usuwa dodatek z rezerwacji.
     * @param dodatek dodatek do usunięcia
     * @return true jeśli usunięto pomyślnie
     */
    public boolean usunDodatek(IDodatek dodatek) {
        if (status == Status.WYMELDOWANA || status == Status.ANULOWANA) {
            return false;
        }
        return dodatki.remove(dodatek);
    }
    
    /**
     * Zwraca niemodyfikowalną listę dodatków.
     * @return lista dodatków
     */
    public List<IDodatek> pobierzDodatki() {
        return Collections.unmodifiableList(dodatki);
    }
    
    /**
     * Sprawdza czy rezerwacja ma jakieś dodatki.
     * @return true jeśli ma dodatki
     */
    public boolean maDodatki() {
        return !dodatki.isEmpty();
    }
    
    /**
     * Zmienia daty rezerwacji.
     * @param nowaDataOd nowa data rozpoczęcia
     * @param nowaDataDo nowa data zakończenia
     */
    public void zmienDaty(LocalDate nowaDataOd, LocalDate nowaDataDo) {
        if (status == Status.WYMELDOWANA || status == Status.ANULOWANA) {
            throw new IllegalStateException("Nie można zmienić dat zakończonej rezerwacji");
        }
        validateDates(nowaDataOd, nowaDataDo);
        this.dataOd = nowaDataOd;
        this.dataDo = nowaDataDo;
    }
    
    /**
     * Anuluje rezerwację.
     * @return true jeśli anulowano pomyślnie
     */
    public boolean anuluj() {
        if (status == Status.WYMELDOWANA || status == Status.ANULOWANA) {
            return false;
        }
        this.status = Status.ANULOWANA;
        return true;
    }
    
    /**
     * Anuluje rezerwację z podaniem przyczyny.
     * @param przyczyna przyczyna anulowania
     * @return true jeśli anulowano pomyślnie
     */
    public boolean anuluj(String przyczyna) {
        if (status == Status.WYMELDOWANA || status == Status.ANULOWANA) {
            return false;
        }
        this.status = Status.ANULOWANA;
        this.przyczynaAnulowania = przyczyna;
        return true;
    }

    /**
     * Resetuje licznik ID (używane w testach).
     */
    public static void resetIdCounter() {
        nextId = 1;
    }

    /**
     * Zwraca przyczynę anulowania rezerwacji.
     * @return przyczyna anulowania lub null jeśli rezerwacja nie została anulowana
     */
    public String getPrzyczynaAnulowania() {
        return przyczynaAnulowania;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rezerwacja that = (Rezerwacja) o;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rezerwacja{");
        sb.append("id=").append(id);
        sb.append(", dataOd=").append(dataOd);
        sb.append(", dataDo=").append(dataDo);
        sb.append(", gosc=").append(gosc.getPelneNazwisko());
        sb.append(", pokoj=").append(pokoj.getNumer());
        sb.append(", status=").append(status);
        sb.append(", cena=").append(obliczCene());
        if (status == Status.ANULOWANA && przyczynaAnulowania != null) {
            sb.append(", przyczyna_anulowania='").append(przyczynaAnulowania).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }
}
