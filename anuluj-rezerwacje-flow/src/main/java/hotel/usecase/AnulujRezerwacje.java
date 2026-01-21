package hotel.usecase;

import hotel.controller.IRezerwacjeKontroler;
import hotel.model.Rezerwacja;

/**
 * Przypadek użycia: Anuluj Rezerwację.
 * Warstwa: PRZYPADEK UŻYCIA (UseCase / Prezentacja)
 * 
 * Reprezentuje akcję użytkownika w systemie.
 * Komunikuje się z kontrolerem, nie bezpośrednio z modelem.
 */
public class AnulujRezerwacje {
    
    private final IRezerwacjeKontroler kontroler;
    
    /**
     * Konstruktor z wstrzykiwaniem kontrolera.
     * @param kontroler kontroler rezerwacji
     */
    public AnulujRezerwacje(IRezerwacjeKontroler kontroler) {
        this.kontroler = kontroler;
    }
    
    /**
     * Wykonuje anulowanie rezerwacji.
     * 
     * @param rezerwacjaId ID rezerwacji do anulowania
     * @return wynik operacji anulowania
     */
    public WynikAnulowania wykonaj(int rezerwacjaId) {
        // Pobierz rezerwację
        Rezerwacja rezerwacja = kontroler.pobierzRezerwacje(rezerwacjaId);
        
        if (rezerwacja == null) {
            return new WynikAnulowania(
                false, 
                "Rezerwacja o ID " + rezerwacjaId + " nie istnieje",
                0.0
            );
        }
        
        // Oblicz karę przed anulowaniem
        double kara;
        try {
            kara = kontroler.obliczOplateZaAnulowanieZTerminem(rezerwacjaId);
        } catch (Exception e) {
            kara = 0.0;
        }
        
        // Wykonaj anulowanie
        boolean sukces = kontroler.anulujRezerwacje(rezerwacjaId);
        
        if (sukces) {
            String komunikat = kara > 0 
                ? String.format("Rezerwacja anulowana. Opłata za anulowanie: %.2f PLN", kara)
                : "Rezerwacja anulowana bez opłat.";
            
            return new WynikAnulowania(true, komunikat, kara);
        } else {
            return new WynikAnulowania(
                false, 
                "Nie można anulować rezerwacji. Status: " + rezerwacja.getStatus(),
                0.0
            );
        }
    }
    
    /**
     * Sprawdza czy rezerwację można anulować (podgląd bez wykonania).
     * @param rezerwacjaId ID rezerwacji
     * @return informacje o możliwości anulowania
     */
    public InformacjeAnulowania sprawdzMozliwoscAnulowania(int rezerwacjaId) {
        Rezerwacja rezerwacja = kontroler.pobierzRezerwacje(rezerwacjaId);
        
        if (rezerwacja == null) {
            return new InformacjeAnulowania(false, 0.0, "Rezerwacja nie istnieje");
        }
        
        boolean moznaAnulowac = rezerwacja.czyMoznaAnulowac();
        double kara = 0.0;
        
        if (moznaAnulowac) {
            try {
                kara = kontroler.obliczOplateZaAnulowanieZTerminem(rezerwacjaId);
            } catch (Exception e) {
                // Ignoruj błędy obliczania kary
            }
        }
        
        String info = moznaAnulowac 
            ? String.format("Można anulować. Przewidywana opłata: %.2f PLN", kara)
            : "Nie można anulować rezerwacji o statusie: " + rezerwacja.getStatus();
        
        return new InformacjeAnulowania(moznaAnulowac, kara, info);
    }
    
    // ========== KLASY POMOCNICZE (DTO) ==========
    
    /**
     * Wynik operacji anulowania.
     */
    public static class WynikAnulowania {
        private final boolean sukces;
        private final String komunikat;
        private final double oplataPobrana;
        
        public WynikAnulowania(boolean sukces, String komunikat, double oplataPobrana) {
            this.sukces = sukces;
            this.komunikat = komunikat;
            this.oplataPobrana = oplataPobrana;
        }
        
        public boolean isSukces() {
            return sukces;
        }
        
        public String getKomunikat() {
            return komunikat;
        }
        
        public double getOplataPobrana() {
            return oplataPobrana;
        }
        
        @Override
        public String toString() {
            return "WynikAnulowania{" +
                    "sukces=" + sukces +
                    ", komunikat='" + komunikat + '\'' +
                    ", oplataPobrana=" + oplataPobrana +
                    '}';
        }
    }
    
    /**
     * Informacje o możliwości anulowania (podgląd).
     */
    public static class InformacjeAnulowania {
        private final boolean mozliwe;
        private final double przewidywanaKara;
        private final String opis;
        
        public InformacjeAnulowania(boolean mozliwe, double przewidywanaKara, String opis) {
            this.mozliwe = mozliwe;
            this.przewidywanaKara = przewidywanaKara;
            this.opis = opis;
        }
        
        public boolean isMozliwe() {
            return mozliwe;
        }
        
        public double getPrzewidywanaKara() {
            return przewidywanaKara;
        }
        
        public String getOpis() {
            return opis;
        }
    }
}
