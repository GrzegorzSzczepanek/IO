package hotel.suite;

import org.junit.platform.suite.api.*;

/**
 * Zestaw testów dla warstwy kontroli (kontrolery).
 * Zawiera testy klas: RezerwacjeKontroler, ZameldowanieKontroler, WymeldowanieKontroler.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@Suite
@SuiteDisplayName("Zestaw testów warstwy kontroli (Controller)")
@SelectPackages("hotel.controller")
@IncludePackages("hotel.controller")
public class SuiteTestyKontroli {
    // Klasa zestawu testów - konfiguracja przez adnotacje
}
