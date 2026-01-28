package hotel.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import hotel.controller.*;
import hotel.dao.*;
import hotel.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Panel widoku Klienta (Gościa) w systemie hotelowym.
 * Realizuje przypadki użycia:
 * - PU1. Wyszukiwanie dostępnych pokoi
 * - PU2. Dokonanie rezerwacji
 * - PU4. Anulowanie rezerwacji
 * - PU5. Przeglądanie historii rezerwacji
 * 
 * @author Grzegorz Szczepanek
 */
public class KlientPanel {

    // Referencje do warstw systemu
    private final RezerwacjeKontroler rezerwacjeKontroler;
    private final GoscieKontroler goscieKontroler;
    private final RezerwacjeDAO rezerwacjeDAO;
    private final PokojeDAO pokojeDAO;
    private final GoscieDAO goscieDAO;

    // Zalogowany klient
    private Gosc zalogowanyKlient;

    // Komponenty GUI
    private VBox mainPanel;
    private VBox panelLogowania;
    private VBox panelGlowny;
    private TableView<Pokoj> tabelaDostepnychPokoi;
    private TableView<Rezerwacja> tabelaMoichRezerwacji;
    private Label labelZalogowany;
    private TextArea logArea;
    private DatePicker datePickerOd;
    private DatePicker datePickerDo;
    private ComboBox<String> comboTypPokoju;

    /**
     * Konstruktor panelu klienta.
     */
    public KlientPanel(RezerwacjeKontroler rezerwacjeKontroler,
                       GoscieKontroler goscieKontroler,
                       RezerwacjeDAO rezerwacjeDAO,
                       PokojeDAO pokojeDAO,
                       GoscieDAO goscieDAO,
                       TextArea logArea) {
        this.rezerwacjeKontroler = rezerwacjeKontroler;
        this.goscieKontroler = goscieKontroler;
        this.rezerwacjeDAO = rezerwacjeDAO;
        this.pokojeDAO = pokojeDAO;
        this.goscieDAO = goscieDAO;
        this.logArea = logArea;

        utworzPanel();
    }

    /**
     * Zwraca główny panel do osadzenia w aplikacji.
     */
    public VBox getPanel() {
        return mainPanel;
    }

    /**
     * Tworzy strukturę panelu klienta.
     */
    private void utworzPanel() {
        mainPanel = new VBox(10);
        mainPanel.setPadding(new Insets(10));

        // Panel logowania
        panelLogowania = utworzPanelLogowania();

        // Panel główny (po zalogowaniu)
        panelGlowny = utworzPanelGlowny();
        panelGlowny.setVisible(false);
        panelGlowny.setManaged(false);

        mainPanel.getChildren().addAll(panelLogowania, panelGlowny);
    }

    // ==================== PANEL LOGOWANIA ====================

    private VBox utworzPanelLogowania() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");
        panel.setMaxWidth(500);

        Label tytul = new Label("🏨 Portal Rezerwacji Hotelowych");
        tytul.setFont(Font.font("System", FontWeight.BOLD, 24));
        tytul.setTextFill(Color.DARKBLUE);

        Label podtytul = new Label("Zaloguj się lub zarejestruj, aby dokonać rezerwacji");
        podtytul.setFont(Font.font("System", 14));
        podtytul.setTextFill(Color.GRAY);

        // Formularz logowania
        GridPane formLogowania = new GridPane();
        formLogowania.setHgap(10);
        formLogowania.setVgap(10);
        formLogowania.setAlignment(Pos.CENTER);

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Adres email");
        txtEmail.setPrefWidth(250);

        Button btnZaloguj = new Button("Zaloguj się");
        btnZaloguj.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnZaloguj.setPrefWidth(120);
        btnZaloguj.setOnAction(e -> zaloguj(txtEmail.getText()));

        Button btnRejestracja = new Button("Nowe konto");
        btnRejestracja.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnRejestracja.setPrefWidth(120);
        btnRejestracja.setOnAction(e -> pokazDialogRejestracji());

        formLogowania.add(new Label("Email:"), 0, 0);
        formLogowania.add(txtEmail, 1, 0);

        HBox przyciski = new HBox(10);
        przyciski.setAlignment(Pos.CENTER);
        przyciski.getChildren().addAll(btnZaloguj, btnRejestracja);

        panel.getChildren().addAll(tytul, podtytul, formLogowania, przyciski);

        return panel;
    }

    private void zaloguj(String email) {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            pokazBlad("Wprowadź poprawny adres email.");
            return;
        }

        // Szukaj gościa po email
        Optional<Gosc> gosc = goscieDAO.pobierzWszystkie().stream()
                .filter(g -> g.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();

        if (gosc.isPresent()) {
            zalogowanyKlient = gosc.get();
            pokazPanelGlowny();
            log("Zalogowano jako: " + zalogowanyKlient.getPelneNazwisko());
        } else {
            pokazBlad("Nie znaleziono konta z tym adresem email.\nUtwórz nowe konto.");
        }
    }

    private void pokazDialogRejestracji() {
        Dialog<Gosc> dialog = new Dialog<>();
        dialog.setTitle("Rejestracja nowego konta");
        dialog.setHeaderText("Wprowadź swoje dane");

        ButtonType btnZarejestrujType = new ButtonType("Zarejestruj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnZarejestrujType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtImie = new TextField();
        txtImie.setPromptText("Jan");
        TextField txtNazwisko = new TextField();
        txtNazwisko.setPromptText("Kowalski");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("jan.kowalski@email.pl");

        grid.add(new Label("Imię:"), 0, 0);
        grid.add(txtImie, 1, 0);
        grid.add(new Label("Nazwisko:"), 0, 1);
        grid.add(txtNazwisko, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(txtEmail, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnZarejestrujType) {
                String imie = txtImie.getText().trim();
                String nazwisko = txtNazwisko.getText().trim();
                String email = txtEmail.getText().trim();

                if (!imie.isEmpty() && !nazwisko.isEmpty() && email.contains("@")) {
                    // Sprawdź czy email już istnieje
                    boolean emailIstnieje = goscieDAO.pobierzWszystkie().stream()
                            .anyMatch(g -> g.getEmail().equalsIgnoreCase(email));
                    if (emailIstnieje) {
                        pokazBlad("Konto z tym adresem email już istnieje.");
                        return null;
                    }
                    return goscieKontroler.utworzProfilGoscia(imie, nazwisko, email);
                } else {
                    pokazBlad("Wypełnij wszystkie pola poprawnie.");
                }
            }
            return null;
        });

        Optional<Gosc> result = dialog.showAndWait();
        result.ifPresent(g -> {
            if (g != null) {
                zalogowanyKlient = g;
                pokazPanelGlowny();
                log("Zarejestrowano i zalogowano: " + g.getPelneNazwisko());
                pokazInfo("Konto utworzone pomyślnie!\nZalogowano jako: " + g.getPelneNazwisko());
            }
        });
    }

    private void pokazPanelGlowny() {
        panelLogowania.setVisible(false);
        panelLogowania.setManaged(false);
        panelGlowny.setVisible(true);
        panelGlowny.setManaged(true);
        labelZalogowany.setText("Zalogowany: " + zalogowanyKlient.getPelneNazwisko() +
                " (" + zalogowanyKlient.getEmail() + ")");
        odswiezMojeRezerwacje();
    }

    private void wyloguj() {
        zalogowanyKlient = null;
        panelGlowny.setVisible(false);
        panelGlowny.setManaged(false);
        panelLogowania.setVisible(true);
        panelLogowania.setManaged(true);
        log("Wylogowano.");
    }

    // ==================== PANEL GŁÓWNY (PO ZALOGOWANIU) ====================

    private VBox utworzPanelGlowny() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));

        // Nagłówek z info o zalogowanym użytkowniku
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 10; -fx-background-radius: 5;");

        labelZalogowany = new Label();
        labelZalogowany.setFont(Font.font("System", FontWeight.BOLD, 14));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnWyloguj = new Button("Wyloguj");
        btnWyloguj.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnWyloguj.setOnAction(e -> wyloguj());

        header.getChildren().addAll(labelZalogowany, spacer, btnWyloguj);

        // Zakładki dla klienta
        TabPane tabPane = new TabPane();

        Tab tabWyszukaj = new Tab("🔍 Wyszukaj pokój", utworzPanelWyszukiwania());
        tabWyszukaj.setClosable(false);

        Tab tabMojeRezerwacje = new Tab("📋 Moje rezerwacje", utworzPanelMoichRezerwacji());
        tabMojeRezerwacje.setClosable(false);

        tabPane.getTabs().addAll(tabWyszukaj, tabMojeRezerwacje);

        panel.getChildren().addAll(header, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        return panel;
    }

    // ==================== PU1: WYSZUKIWANIE DOSTĘPNYCH POKOI ====================

    private VBox utworzPanelWyszukiwania() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(15));

        // Sekcja filtrów
        Label labelFiltry = new Label("Wyszukaj dostępne pokoje:");
        labelFiltry.setFont(Font.font("System", FontWeight.BOLD, 16));

        GridPane filtry = new GridPane();
        filtry.setHgap(15);
        filtry.setVgap(10);

        datePickerOd = new DatePicker(LocalDate.now());
        datePickerDo = new DatePicker(LocalDate.now().plusDays(3));

        comboTypPokoju = new ComboBox<>();
        comboTypPokoju.getItems().addAll("Wszystkie", "Jednoosobowy", "Dwuosobowy", "Apartament");
        comboTypPokoju.setValue("Wszystkie");

        Button btnSzukaj = new Button("🔍 Szukaj");
        btnSzukaj.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSzukaj.setOnAction(e -> wyszukajDostepnePokoje());

        filtry.add(new Label("Data przyjazdu:"), 0, 0);
        filtry.add(datePickerOd, 1, 0);
        filtry.add(new Label("Data wyjazdu:"), 2, 0);
        filtry.add(datePickerDo, 3, 0);
        filtry.add(new Label("Typ pokoju:"), 0, 1);
        filtry.add(comboTypPokoju, 1, 1);
        filtry.add(btnSzukaj, 3, 1);

        // Tabela dostępnych pokoi
        tabelaDostepnychPokoi = new TableView<>();

        TableColumn<Pokoj, Integer> colNumer = new TableColumn<>("Nr pokoju");
        colNumer.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getNumer()).asObject());
        colNumer.setPrefWidth(80);

        TableColumn<Pokoj, String> colTyp = new TableColumn<>("Typ");
        colTyp.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTyp()));
        colTyp.setPrefWidth(150);

        TableColumn<Pokoj, Double> colCena = new TableColumn<>("Cena/noc (zł)");
        colCena.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(data.getValue().getCenaBazowa()).asObject());
        colCena.setPrefWidth(100);

        TableColumn<Pokoj, String> colSuma = new TableColumn<>("Suma za pobyt");
        colSuma.setCellValueFactory(data -> {
            long dni = java.time.temporal.ChronoUnit.DAYS.between(
                    datePickerOd.getValue(), datePickerDo.getValue());
            double suma = data.getValue().getCenaBazowa() * dni;
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f zł", suma));
        });
        colSuma.setPrefWidth(120);

        tabelaDostepnychPokoi.getColumns().addAll(colNumer, colTyp, colCena, colSuma);

        // Przycisk rezerwacji
        Button btnRezerwuj = new Button("📝 Zarezerwuj wybrany pokój");
        btnRezerwuj.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");
        btnRezerwuj.setOnAction(e -> dokonajRezerwacji());

        panel.getChildren().addAll(labelFiltry, filtry, new Separator(),
                new Label("Dostępne pokoje:"), tabelaDostepnychPokoi, btnRezerwuj);
        VBox.setVgrow(tabelaDostepnychPokoi, Priority.ALWAYS);

        return panel;
    }

    /**
     * PU1: Wyszukiwanie dostępnych pokoi
     */
    private void wyszukajDostepnePokoje() {
        LocalDate dataOd = datePickerOd.getValue();
        LocalDate dataDo = datePickerDo.getValue();

        if (dataOd == null || dataDo == null) {
            pokazBlad("Wybierz daty pobytu.");
            return;
        }

        if (!dataDo.isAfter(dataOd)) {
            pokazBlad("Data wyjazdu musi być późniejsza niż data przyjazdu.");
            return;
        }

        if (dataOd.isBefore(LocalDate.now())) {
            pokazBlad("Data przyjazdu nie może być w przeszłości.");
            return;
        }

        String typPokoju = comboTypPokoju.getValue();

        // Pobierz wszystkie pokoje
        List<Pokoj> wszystkiePokoje = pokojeDAO.pobierzWszystkie();

        // Filtruj po typie
        if (!"Wszystkie".equals(typPokoju)) {
            wszystkiePokoje = wszystkiePokoje.stream()
                    .filter(p -> p.getTyp().equals(typPokoju))
                    .collect(Collectors.toList());
        }

        // Filtruj pokoje które NIE mają kolidujących rezerwacji
        List<Rezerwacja> wszystkieRezerwacje = rezerwacjeDAO.pobierzWszystkie();

        List<Pokoj> dostepnePokoje = wszystkiePokoje.stream()
                .filter(pokoj -> {
                    // Sprawdź czy pokój ma kolidującą rezerwację
                    return wszystkieRezerwacje.stream()
                            .filter(r -> r.getPokoj().getNumer() == pokoj.getNumer())
                            .filter(r -> r.getStatus() != Rezerwacja.Status.ANULOWANA &&
                                    r.getStatus() != Rezerwacja.Status.WYMELDOWANA)
                            .noneMatch(r -> czyDatyKoliduja(r.getDataOd(), r.getDataDo(), dataOd, dataDo));
                })
                .collect(Collectors.toList());

        tabelaDostepnychPokoi.getItems().clear();
        tabelaDostepnychPokoi.getItems().addAll(dostepnePokoje);

        long dni = java.time.temporal.ChronoUnit.DAYS.between(dataOd, dataDo);
        log("Wyszukano pokoje na okres " + dataOd + " - " + dataDo + " (" + dni + " nocy). " +
                "Znaleziono: " + dostepnePokoje.size() + " dostępnych pokoi.");
    }

    private boolean czyDatyKoliduja(LocalDate rezOd, LocalDate rezDo, LocalDate nowaOd, LocalDate nowaDo) {
        // Rezerwacje kolidują jeśli się nakładają
        return !rezDo.isBefore(nowaOd) && !nowaDo.isBefore(rezOd);
    }

    // ==================== PU2: DOKONANIE REZERWACJI ====================

    /**
     * PU2: Dokonanie rezerwacji
     */
    private void dokonajRezerwacji() {
        Pokoj wybranyPokoj = tabelaDostepnychPokoi.getSelectionModel().getSelectedItem();

        if (wybranyPokoj == null) {
            pokazBlad("Wybierz pokój z listy.");
            return;
        }

        LocalDate dataOd = datePickerOd.getValue();
        LocalDate dataDo = datePickerDo.getValue();
        long dni = java.time.temporal.ChronoUnit.DAYS.between(dataOd, dataDo);
        double cena = wybranyPokoj.getCenaBazowa() * dni;

        // Dialog potwierdzenia
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potwierdzenie rezerwacji");
        confirm.setHeaderText("Czy potwierdzasz rezerwację?");
        confirm.setContentText(
                "Pokój: " + wybranyPokoj.getNumer() + " (" + wybranyPokoj.getTyp() + ")\n" +
                        "Termin: " + dataOd + " - " + dataDo + " (" + dni + " nocy)\n" +
                        "Cena za noc: " + wybranyPokoj.getCenaBazowa() + " zł\n" +
                        "SUMA DO ZAPŁATY: " + String.format("%.2f", cena) + " zł\n\n" +
                        "Gość: " + zalogowanyKlient.getPelneNazwisko()
        );

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Utwórz rezerwację
            Rezerwacja nowaRezerwacja = rezerwacjeKontroler.utworzRezerwacje(
                    zalogowanyKlient, wybranyPokoj, dataOd, dataDo);

            if (nowaRezerwacja != null) {
                log("Utworzono rezerwację ID=" + nowaRezerwacja.getId() +
                        " dla pokoju " + wybranyPokoj.getNumer());

                pokazInfo("Rezerwacja utworzona pomyślnie!\n\n" +
                        "Numer rezerwacji: " + nowaRezerwacja.getId() + "\n" +
                        "Status: " + nowaRezerwacja.getStatus() + "\n\n" +
                        "Potwierdzenie zostanie wysłane na adres:\n" +
                        zalogowanyKlient.getEmail());

                // Odśwież widoki
                wyszukajDostepnePokoje();
                odswiezMojeRezerwacje();
            } else {
                pokazBlad("Nie udało się utworzyć rezerwacji.\nPokój może być już zajęty.");
            }
        }
    }

    // ==================== PU4 & PU5: MOJE REZERWACJE ====================

    private VBox utworzPanelMoichRezerwacji() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(15));

        Label labelTytul = new Label("Moje rezerwacje:");
        labelTytul.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Tabela rezerwacji klienta
        tabelaMoichRezerwacji = new TableView<>();

        TableColumn<Rezerwacja, Integer> colId = new TableColumn<>("Nr rez.");
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colId.setPrefWidth(60);

        TableColumn<Rezerwacja, Integer> colPokoj = new TableColumn<>("Pokój");
        colPokoj.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPokoj().getNumer()).asObject());
        colPokoj.setPrefWidth(60);

        TableColumn<Rezerwacja, String> colTyp = new TableColumn<>("Typ");
        colTyp.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPokoj().getTyp()));
        colTyp.setPrefWidth(120);

        TableColumn<Rezerwacja, String> colDataOd = new TableColumn<>("Od");
        colDataOd.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDataOd().toString()));
        colDataOd.setPrefWidth(100);

        TableColumn<Rezerwacja, String> colDataDo = new TableColumn<>("Do");
        colDataDo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDataDo().toString()));
        colDataDo.setPrefWidth(100);

        TableColumn<Rezerwacja, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().name()));
        colStatus.setPrefWidth(120);
        // Kolorowanie statusu
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "NOWA" -> setStyle("-fx-background-color: #fff9c4;");
                        case "POTWIERDZONA" -> setStyle("-fx-background-color: #c8e6c9;");
                        case "ZAMELDOWANA" -> setStyle("-fx-background-color: #bbdefb;");
                        case "WYMELDOWANA" -> setStyle("-fx-background-color: #e0e0e0;");
                        case "ANULOWANA" -> setStyle("-fx-background-color: #ffcdd2;");
                        default -> setStyle("");
                    }
                }
            }
        });

        TableColumn<Rezerwacja, Double> colCena = new TableColumn<>("Cena");
        colCena.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(data.getValue().obliczCene()).asObject());
        colCena.setPrefWidth(80);

        tabelaMoichRezerwacji.getColumns().addAll(colId, colPokoj, colTyp, colDataOd, colDataDo, colStatus, colCena);

        // Przyciski akcji
        HBox przyciski = new HBox(10);

        Button btnAnuluj = new Button("❌ Anuluj rezerwację");
        btnAnuluj.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnAnuluj.setOnAction(e -> anulujRezerwacje());

        Button btnSzczegoly = new Button("ℹ️ Szczegóły");
        btnSzczegoly.setOnAction(e -> pokazSzczegolyRezerwacji());

        Button btnOdswiez = new Button("🔄 Odśwież");
        btnOdswiez.setOnAction(e -> odswiezMojeRezerwacje());

        przyciski.getChildren().addAll(btnAnuluj, btnSzczegoly, btnOdswiez);

        // Legenda statusów
        HBox legenda = new HBox(15);
        legenda.setAlignment(Pos.CENTER_LEFT);
        legenda.getChildren().addAll(
                utworzLegende("NOWA", "#fff9c4"),
                utworzLegende("POTWIERDZONA", "#c8e6c9"),
                utworzLegende("ZAMELDOWANA", "#bbdefb"),
                utworzLegende("WYMELDOWANA", "#e0e0e0"),
                utworzLegende("ANULOWANA", "#ffcdd2")
        );

        panel.getChildren().addAll(labelTytul, tabelaMoichRezerwacji, przyciski, new Separator(), legenda);
        VBox.setVgrow(tabelaMoichRezerwacji, Priority.ALWAYS);

        return panel;
    }

    private HBox utworzLegende(String tekst, String kolor) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        Label kwadrat = new Label("  ");
        kwadrat.setStyle("-fx-background-color: " + kolor + "; -fx-border-color: gray;");
        Label label = new Label(tekst);
        label.setFont(Font.font("System", 10));
        box.getChildren().addAll(kwadrat, label);
        return box;
    }

    /**
     * PU5: Przeglądanie historii rezerwacji
     */
    private void odswiezMojeRezerwacje() {
        if (zalogowanyKlient == null) return;

        List<Rezerwacja> mojeRezerwacje = rezerwacjeDAO.pobierzWszystkie().stream()
                .filter(r -> r.getGosc().getId() == zalogowanyKlient.getId())
                .collect(Collectors.toList());

        tabelaMoichRezerwacji.getItems().clear();
        tabelaMoichRezerwacji.getItems().addAll(mojeRezerwacje);
    }

    /**
     * PU4: Anulowanie rezerwacji
     */
    private void anulujRezerwacje() {
        Rezerwacja wybrana = tabelaMoichRezerwacji.getSelectionModel().getSelectedItem();

        if (wybrana == null) {
            pokazBlad("Wybierz rezerwację do anulowania.");
            return;
        }

        if (wybrana.getStatus() == Rezerwacja.Status.ANULOWANA) {
            pokazBlad("Ta rezerwacja jest już anulowana.");
            return;
        }

        if (wybrana.getStatus() == Rezerwacja.Status.ZAMELDOWANA ||
                wybrana.getStatus() == Rezerwacja.Status.WYMELDOWANA) {
            pokazBlad("Nie można anulować rezerwacji ze statusem: " + wybrana.getStatus());
            return;
        }

        // Oblicz ewentualną opłatę za anulowanie
        double oplata = rezerwacjeKontroler.pobierzOplateZaAnulowanie(wybrana.getId());

        String komunikat = "Czy na pewno chcesz anulować rezerwację?\n\n" +
                "Rezerwacja nr: " + wybrana.getId() + "\n" +
                "Pokój: " + wybrana.getPokoj().getNumer() + "\n" +
                "Termin: " + wybrana.getDataOd() + " - " + wybrana.getDataDo();

        if (oplata > 0) {
            komunikat += "\n\nOPŁATA ZA ANULOWANIE: " + String.format("%.2f", oplata) + " zł";
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potwierdzenie anulowania");
        confirm.setHeaderText("Anulowanie rezerwacji");
        confirm.setContentText(komunikat);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean sukces = rezerwacjeKontroler.anulujRezerwacje(wybrana.getId());

            if (sukces) {
                log("Anulowano rezerwację ID=" + wybrana.getId());
                pokazInfo("Rezerwacja została anulowana.");
                odswiezMojeRezerwacje();
            } else {
                pokazBlad("Nie udało się anulować rezerwacji.");
            }
        }
    }

    private void pokazSzczegolyRezerwacji() {
        Rezerwacja wybrana = tabelaMoichRezerwacji.getSelectionModel().getSelectedItem();

        if (wybrana == null) {
            pokazBlad("Wybierz rezerwację.");
            return;
        }

        long dni = java.time.temporal.ChronoUnit.DAYS.between(
                wybrana.getDataOd(), wybrana.getDataDo());

        String szczegoly = "═══════════════════════════════════\n" +
                "       SZCZEGÓŁY REZERWACJI\n" +
                "═══════════════════════════════════\n\n" +
                "Numer rezerwacji: " + wybrana.getId() + "\n" +
                "Status: " + wybrana.getStatus() + "\n\n" +
                "───────────────────────────────────\n" +
                "POKÓJ\n" +
                "───────────────────────────────────\n" +
                "Numer: " + wybrana.getPokoj().getNumer() + "\n" +
                "Typ: " + wybrana.getPokoj().getTyp() + "\n" +
                "Cena za noc: " + wybrana.getPokoj().getCenaBazowa() + " zł\n\n" +
                "───────────────────────────────────\n" +
                "TERMIN POBYTU\n" +
                "───────────────────────────────────\n" +
                "Data przyjazdu: " + wybrana.getDataOd() + "\n" +
                "Data wyjazdu: " + wybrana.getDataDo() + "\n" +
                "Liczba nocy: " + dni + "\n\n" +
                "───────────────────────────────────\n" +
                "PŁATNOŚĆ\n" +
                "───────────────────────────────────\n" +
                "SUMA: " + String.format("%.2f", wybrana.obliczCene()) + " zł\n\n" +
                "═══════════════════════════════════";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły rezerwacji");
        alert.setHeaderText("Rezerwacja nr " + wybrana.getId());

        TextArea textArea = new TextArea(szczegoly);
        textArea.setEditable(false);
        textArea.setFont(Font.font("Monospaced", 12));
        textArea.setPrefSize(400, 400);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    // ==================== POMOCNICZE ====================

    private void log(String msg) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        if (logArea != null) {
            logArea.appendText("[" + timestamp + "] [KLIENT] " + msg + "\n");
        }
    }

    private void pokazBlad(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void pokazInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
