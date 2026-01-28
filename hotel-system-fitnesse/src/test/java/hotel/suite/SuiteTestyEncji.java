package hotel.suite;

import org.junit.platform.suite.api.*;

/**
 * Zestaw testów dla warstwy encji (model).
 * Zawiera testy klas: Pokoj, Gosc, Rezerwacja, Sniadanie, Parking.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@Suite
@SuiteDisplayName("Zestaw testów warstwy encji (Model)")
@SelectPackages("hotel.model")
@IncludePackages("hotel.model")
@ExcludeTags("mock")
public class SuiteTestyEncji {
    // Klasa zestawu testów - konfiguracja przez adnotacje
}
