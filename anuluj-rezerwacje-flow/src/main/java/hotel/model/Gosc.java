package hotel.model;

/**
 * Klasa encji reprezentująca gościa hotelowego.
 */
public class Gosc {
    private static int idCounter = 1;
    
    private int id;
    private String imie;
    private String nazwisko;
    private String email;
    
    public Gosc(String imie, String nazwisko, String email) {
        this(idCounter++, imie, nazwisko, email);
    }
    
    public Gosc(int id, String imie, String nazwisko, String email) {
        walidujImie(imie);
        walidujNazwisko(nazwisko);
        walidujEmail(email);
        
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.email = email;
    }
    
    private void walidujImie(String imie) {
        if (imie == null || imie.trim().isEmpty()) {
            throw new IllegalArgumentException("Imię nie może być puste");
        }
    }
    
    private void walidujNazwisko(String nazwisko) {
        if (nazwisko == null || nazwisko.trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwisko nie może być puste");
        }
    }
    
    private void walidujEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Nieprawidłowy adres email");
        }
    }
    
    public static void resetIdCounter() {
        idCounter = 1;
    }
    
    public int getId() { return id; }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
    public String getEmail() { return email; }
    public String getPelneNazwisko() { return imie + " " + nazwisko; }
    
    public void setEmail(String email) {
        walidujEmail(email);
        this.email = email;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Gosc) o).id;
    }
    
    @Override
    public int hashCode() { return Integer.hashCode(id); }
    
    @Override
    public String toString() {
        return "Gosc{id=" + id + ", imie='" + imie + "', nazwisko='" + nazwisko + "'}";
    }
}
