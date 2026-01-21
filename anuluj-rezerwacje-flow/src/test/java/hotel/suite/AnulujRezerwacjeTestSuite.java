package hotel.suite;

import org.junit.platform.suite.api.*;

/**
 * Zestaw testów dla funkcjonalności "Anuluj Rezerwację".
 * Zadanie 3: Zestawy testów z @Suite i @Tag.
 */
@Suite
@SuiteDisplayName("Zestaw testów: Anuluj Rezerwację")
@SelectPackages({"hotel.usecase", "hotel.controller"})
@IncludeTags("anulowanie")
public class AnulujRezerwacjeTestSuite {
}
