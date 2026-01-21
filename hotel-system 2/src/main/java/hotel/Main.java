package hotel;

import hotel.model.*;
import hotel.controller.*;
import hotel.dao.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Klasa demonstracyjna systemu zarządzania hotelem.
 * Pokazuje podstawowe operacje: tworzenie gości, pokoi, rezerwacji.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   SYSTEM ZARZĄDZANIA HOTELEM - DEMO");
        System.out.println("========================================\n");
        
        // Reset liczników ID dla demonstracji
        Gosc.resetIdCounter();
        Rezerwacja.resetIdCounter();
        
        // Tworzenie modelu hotelu
        HotelModel model = new HotelModel();
        
        // ========== DEMONSTRACJA WARSTWY ENCJI ==========
        System.out.println(">>> WARSTWA ENCJI <<<\n");
        
        // Tworzenie pokoi
        System.out.println("1. Tworzenie pokoi:");
        Pokoj pokoj101 = new Pokoj(101, "Jednoosobowy", 150.0);
        Pokoj pokoj201 = new Pokoj(201, "Dwuosobowy", 250.0);
        Pokoj pokoj301 = new Pokoj(301, "Apartament", 500.0);
        
        System.out.println("   - " + pokoj101);
        System.out.println("   - " + pokoj201);
        System.out.println("   - " + pokoj301);
        
        // Zapisanie pokoi do DAO
        model.getPokojeDAO().zapisz(pokoj101);
        model.getPokojeDAO().zapisz(pokoj201);
        model.getPokojeDAO().zapisz(pokoj301);
        
        // Tworzenie gości
        System.out.println("\n2. Tworzenie gości:");
        Gosc gosc1 = model.utworzProfilGoscia("Jan", "Kowalski", "jan.kowalski@email.com");
        Gosc gosc2 = model.utworzProfilGoscia("Anna", "Nowak", "anna.nowak@email.com");
        
        System.out.println("   - " + gosc1);
        System.out.println("   - " + gosc2);
        
        // Tworzenie rezerwacji
        System.out.println("\n3. Tworzenie rezerwacji:");
        LocalDate dataOd = LocalDate.of(2025, 6, 1);
        LocalDate dataDo = LocalDate.of(2025, 6, 5);
        
        Rezerwacja rez1 = model.utworzRezerwacje(gosc1, pokoj201, dataOd, dataDo);
        System.out.println("   - " + rez1);
        System.out.println("   - Liczba nocy: " + rez1.getLiczbaNocy());
        System.out.println("   - Cena bazowa: " + rez1.obliczCene() + " zł");
        
        // Dodawanie dodatków
        System.out.println("\n4. Dodawanie dodatków do rezerwacji:");
        Sniadanie sniadanie = new Sniadanie(50.0, 4);
        Parking parking = new Parking(30.0, 4);
        
        rez1.dodajDodatek(sniadanie);
        rez1.dodajDodatek(parking);
        
        System.out.println("   - " + sniadanie.getOpis() + " = " + sniadanie.obliczDodatkowyKoszt() + " zł");
        System.out.println("   - " + parking.getOpis() + " = " + parking.obliczDodatkowyKoszt() + " zł");
        System.out.println("   - CENA CAŁKOWITA: " + rez1.obliczCene() + " zł");
        
        // ========== DEMONSTRACJA WARSTWY KONTROLI ==========
        System.out.println("\n>>> WARSTWA KONTROLI <<<\n");
        
        // Kontroler rezerwacji
        RezerwacjeKontroler rezKontroler = new RezerwacjeKontroler(model);
        ZameldowanieKontroler zamKontroler = new ZameldowanieKontroler(model);
        WymeldowanieKontroler wymKontroler = new WymeldowanieKontroler(model);
        
        // Zameldowanie gościa
        System.out.println("5. Zameldowanie gościa:");
        System.out.println("   - Status przed: " + rez1.getStatus());
        boolean zameldowany = zamKontroler.zameldujGoscia(rez1.getId());
        System.out.println("   - Zameldowanie: " + (zameldowany ? "SUKCES" : "BŁĄD"));
        System.out.println("   - Status po: " + rez1.getStatus());
        
        // Wymeldowanie gościa
        System.out.println("\n6. Wymeldowanie gościa:");
        boolean wymeldowany = wymKontroler.wymeldujGoscia(rez1.getId());
        System.out.println("   - Wymeldowanie: " + (wymeldowany ? "SUKCES" : "BŁĄD"));
        System.out.println("   - Status po: " + rez1.getStatus());
        
        // Opłata za późne wymeldowanie
        System.out.println("\n7. Symulacja opłaty za późne wymeldowanie (3h):");
        // Tworzymy nową rezerwację dla testu
        Rezerwacja rez2 = model.utworzRezerwacje(gosc2, pokoj301, 
            LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 3));
        model.zameldujGoscia(rez2.getId());
        double oplata = wymKontroler.naliczOplateZaPozneWymeldowanie(rez2.getId(), 3);
        System.out.println("   - Opłata: " + oplata + " zł (3h x 25 zł/h)");
        
        // Anulowanie rezerwacji
        System.out.println("\n8. Anulowanie rezerwacji:");
        Rezerwacja rez3 = model.utworzRezerwacje(gosc1, pokoj101, 
            LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5));
        System.out.println("   - Rezerwacja: " + rez3.getId() + ", Status: " + rez3.getStatus());
        double oplataAnulowanie = rezKontroler.pobierzOplateZaAnulowanie(rez3.getId());
        System.out.println("   - Opłata za anulowanie (20%): " + oplataAnulowanie + " zł");
        boolean anulowano = rezKontroler.anulujRezerwacje(rez3.getId());
        System.out.println("   - Anulowanie: " + (anulowano ? "SUKCES" : "BŁĄD"));
        System.out.println("   - Status po: " + rez3.getStatus());
        
        // ========== PODSUMOWANIE ==========
        System.out.println("\n>>> PODSUMOWANIE <<<\n");
        System.out.println("Liczba pokoi w systemie: " + model.getPokojeDAO().liczba());
        System.out.println("Liczba gości w systemie: " + model.getGoscieDAO().liczba());
        System.out.println("Liczba rezerwacji w systemie: " + model.getRezerwacjeDAO().liczba());
        
        System.out.println("\n========================================");
        System.out.println("          KONIEC DEMONSTRACJI");
        System.out.println("========================================");
    }
}
