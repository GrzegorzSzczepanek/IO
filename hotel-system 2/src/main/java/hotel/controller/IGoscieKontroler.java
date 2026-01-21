package hotel.controller;

import hotel.model.Gosc;

import java.util.Optional;

/**
 * Interfejs kontrolera gości.
 * Definiuje operacje związane z zarządzaniem profilami gości.
 */
public interface IGoscieKontroler {
    
    /**
     * Przegląda profil gościa.
     * @param idGoscia ID gościa
     * @return Optional z profilem gościa
     */
    Optional<Gosc> przegladProfiluGoscia(int idGoscia);
    
    /**
     * Edytuje profil gościa.
     * @param idGoscia ID gościa
     * @param nowyEmail nowy adres email
     * @return true jeśli edycja się powiodła
     */
    boolean edytujProfilGoscia(int idGoscia, String nowyEmail);
    
    /**
     * Tworzy profil gościa.
     * @param imie imię gościa
     * @param nazwisko nazwisko gościa
     * @param email adres email
     * @return utworzony gość
     */
    Gosc utworzProfilGoscia(String imie, String nazwisko, String email);
}
