package hotel.model;

import hotel.dao.GoscieDAO;
import hotel.dao.PokojeDAO;
import hotel.dao.RezerwacjeDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestHotelModelCancellations {

    private RezerwacjeDAO mockRezerwacjeDAO;
    private PokojeDAO mockPokojeDAO;
    private GoscieDAO mockGoscieDAO;
    private IGoscFactory mockFabryka;
    private HotelModel model;
    private Rezerwacja rezerwacja;

    @BeforeEach
    void setUp() {
        mockRezerwacjeDAO = mock(RezerwacjeDAO.class);
        mockPokojeDAO = mock(PokojeDAO.class);
        mockGoscieDAO = mock(GoscieDAO.class);
        mockFabryka = mock(IGoscFactory.class);
        
        model = new HotelModel(mockRezerwacjeDAO, mockPokojeDAO, mockGoscieDAO, mockFabryka);
    }

    @Test
    @DisplayName("pobierzOplate zwraca 0 dla nieistniejącej rezerwacji")
    @Tag("anulowanie")
    void testPobierzOplate_NieistniejacaRezerwacja() {
        // Dane testowe
        int idRezerwacji = 1;
        when(mockRezerwacjeDAO.pobierz(idRezerwacji)).thenReturn(java.util.Optional.empty());

        // Gdy: pobierana jest opłata za anulowanie nieistniejącej rezerwacji
        double oplata = model.pobierzOplate(idRezerwacji);

        // Wtedy: zwracana jest opłata 0
        assertEquals(0, oplata, "Opłata za anulowanie nieistniejącej rezerwacji powinna wynosić 0");
        verify(mockRezerwacjeDAO).pobierz(idRezerwacji);
    }

    @Test
    @DisplayName("pobierzOplate zwraca opłatę zależną od czasu przed przyjazdem (wcześnie)")
    @Tag("anulowanie")
    void testPobierzOplate_WczesneAnulowanie() {
        // Dane testowe: rezerwacja na 10 dni od dziś, więc anulowanie wcześnie
        LocalDate dataOd = LocalDate.now().plusDays(10); // 10 dni od dziś
        LocalDate dataDo = LocalDate.now().plusDays(12); // 2 dni później
        
        Gosc gosc = new Gosc("Jan", "Kowalski", "jan@example.com");
        Pokoj pokoj = new Pokoj(101, "Standard", 100.0);
        
        rezerwacja = new Rezerwacja(dataOd, dataDo, gosc, pokoj);
        
        int idRezerwacji = 1;
        when(mockRezerwacjeDAO.pobierz(idRezerwacji)).thenReturn(java.util.Optional.of(rezerwacja));

        // Gdy: pobierana jest opłata za anulowanie wcześnie
        double oplata = model.pobierzOplate(idRezerwacji);

        // Wtedy: zwracana jest opłata 0 (bo anulowanie wcześnie)
        assertEquals(0, oplata, "Opłata za wcześniejsze anulowanie powinna wynosić 0");
        verify(mockRezerwacjeDAO).pobierz(idRezerwacji);
    }

    @Test
    @DisplayName("pobierzOplate zwraca opłatę zależną od czasu przed przyjazdem (środek)")
    @Tag("anulowanie")
    void testPobierzOplate_SrodkoweAnulowanie() {
        // Dane testowe: rezerwacja na 2 dni od dziś, więc anulowanie w ostatnich dniach
        LocalDate dataOd = LocalDate.now().plusDays(2); // 2 dni od dziś
        LocalDate dataDo = LocalDate.now().plusDays(4); // 2 dni później
        
        Gosc gosc = new Gosc("Jan", "Kowalski", "jan@example.com");
        Pokoj pokoj = new Pokoj(101, "Standard", 100.0);
        
        rezerwacja = new Rezerwacja(dataOd, dataDo, gosc, pokoj);
        
        int idRezerwacji = 1;
        when(mockRezerwacjeDAO.pobierz(idRezerwacji)).thenReturn(java.util.Optional.of(rezerwacja));

        // Gdy: pobierana jest opłata za anulowanie 2 dni przed
        double oplata = model.pobierzOplate(idRezerwacji);

        // Wtedy: zwracana jest opłata 20% (środkowy współczynnik)
        double oczekiwanaOplata = rezerwacja.obliczCene() * 0.2; // 20% opłaty
        assertEquals(oczekiwanaOplata, oplata, 0.01, "Opłata za anulowanie 2 dni przed powinna wynosić 20%");
        verify(mockRezerwacjeDAO).pobierz(idRezerwacji);
    }

    @Test
    @DisplayName("pobierzOplate zwraca opłatę zależną od czasu przed przyjazdem (blisko)")
    @Tag("anulowanie")
    void testPobierzOplate_BliskieAnulowanie() {
        // Dane testowe: rezerwacja jutro, więc anulowanie blisko terminu
        LocalDate dataOd = LocalDate.now().plusDays(1); // Jutro
        LocalDate dataDo = LocalDate.now().plusDays(3); // 2 dni później
        
        Gosc gosc = new Gosc("Jan", "Kowalski", "jan@example.com");
        Pokoj pokoj = new Pokoj(101, "Standard", 100.0);
        
        rezerwacja = new Rezerwacja(dataOd, dataDo, gosc, pokoj);
        
        int idRezerwacji = 1;
        when(mockRezerwacjeDAO.pobierz(idRezerwacji)).thenReturn(java.util.Optional.of(rezerwacja));

        // Gdy: pobierana jest opłata za anulowanie jutro
        double oplata = model.pobierzOplate(idRezerwacji);

        // Wtedy: zwracana jest opłata 20% (środkowy współczynnik)
        double oczekiwanaOplata = rezerwacja.obliczCene() * 0.2; // 20% opłaty
        assertEquals(oczekiwanaOplata, oplata, 0.01, "Opłata za anulowanie jutro powinna wynosić 20%");
        verify(mockRezerwacjeDAO).pobierz(idRezerwacji);
    }

    @Test
    @DisplayName("anulujRezerwacje z przyczyną powoduje zmianę statusu i ustawienie dostępności pokoju")
    @Tag("anulowanie")
    void testAnulujRezerwacjeZPrzyczyna_Sukces() {
        // Dane testowe
        LocalDate dataOd = LocalDate.now().plusDays(10);
        LocalDate dataDo = LocalDate.now().plusDays(12);
        
        Gosc gosc = new Gosc("Jan", "Kowalski", "jan@example.com");
        Pokoj pokoj = new Pokoj(101, "Standard", 100.0);
        
        rezerwacja = new Rezerwacja(dataOd, dataDo, gosc, pokoj);
        int idRezerwacji = 1;
        
        when(mockRezerwacjeDAO.pobierz(idRezerwacji)).thenReturn(java.util.Optional.of(rezerwacja));
        when(mockRezerwacjeDAO.aktualizuj(rezerwacja)).thenReturn(true);

        // Gdy: anulowana jest rezerwacja z przyczyną
        String przyczyna = "Zmiana planów";
        boolean wynik = model.anulujRezerwacje(idRezerwacji, przyczyna);

        // Wtedy: rezerwacja anulowana i ustawiona dostępność pokoju
        assertTrue(wynik, "Anulowanie rezerwacji powinno się powieść");
        assertEquals(Rezerwacja.Status.ANULOWANA, rezerwacja.getStatus());
        assertEquals(przyczyna, rezerwacja.getPrzyczynaAnulowania());
        verify(mockPokojeDAO).ustawDostepnosc(pokoj.getNumer(), true);
        verify(mockRezerwacjeDAO).aktualizuj(rezerwacja);
    }

    @Test
    @DisplayName("anulujRezerwacje z przyczyną zwraca false dla nieistniejącej rezerwacji")
    @Tag("anulowanie")
    void testAnulujRezerwacjeZPrzyczyna_Nieistniejaca() {
        // Dane testowe
        int idRezerwacji = 1;
        when(mockRezerwacjeDAO.pobierz(idRezerwacji)).thenReturn(java.util.Optional.empty());

        // Gdy: próba anulowania nieistniejącej rezerwacji
        boolean wynik = model.anulujRezerwacje(idRezerwacji, "Zmiana planów");

        // Wtedy: zwracane false
        assertFalse(wynik, "Anulowanie nieistniejącej rezerwacji powinno zwrócić false");
        verify(mockRezerwacjeDAO).pobierz(idRezerwacji);
        verify(mockPokojeDAO, never()).ustawDostepnosc(anyInt(), anyBoolean());
    }
}