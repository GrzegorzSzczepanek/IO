package hotel.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import hotel.controller.*;
import hotel.dao.*;
import hotel.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Główna klasa aplikacji JavaFX dla systemu hotelowego.
 * Integruje się z istniejącymi kontrolerami i modelem.
 */
public class HotelApplication extends Application {

    // Warstwa DAO
    private RezerwacjeDAO rezerwacjeDAO;
    private PokojeDAO pokojeDAO;
    private GoscieDAO goscieDAO;

    // Warstwa modelu
    private HotelModel model;

    // Warstwa kontrolerów
    private RezerwacjeKontroler rezerwacjeKontroler;
    private ZameldowanieKontroler zameldowanieKontroler;
    private WymeldowanieKontroler wymeldowanieKontroler;
    private GoscieKontroler goscieKontroler;

    // Komponenty GUI
    private TableView<Rezerwacja> tabelaRezerwacji;
    private TableView<Pokoj> tabelaPokoi;
    private TableView<Gosc> tabelaGosci;
    private TextArea logArea;
    private KlientPanel klientPanel;

    @Override
    public void start(Stage primaryStage) {
        inicjalizujSystem();
        dodajPrzykladoweDane();

        primaryStage.setTitle("System Zarządzania Hotelem");

        // Główny TabPane z widokami dla różnych aktorów
        TabPane mainTabPane = new TabPane();

        // === WIDOK KLIENTA (GOŚCIA) ===
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(100);
        logArea.setStyle("-fx-font-family: monospace;");

        klientPanel = new KlientPanel(rezerwacjeKontroler, goscieKontroler,
                rezerwacjeDAO, pokojeDAO, goscieDAO, logArea);

        Tab tabKlient = new Tab("🧑 Panel Klienta", klientPanel.getPanel());
        tabKlient.setClosable(false);
        tabKlient.setStyle("-fx-font-size: 14;");

        // === WIDOK RECEPCJI ===
        TabPane recepcjaTabPane = new TabPane();

        Tab tabRezerwacje = new Tab("Rezerwacje", utworzPanelRezerwacji());
        tabRezerwacje.setClosable(false);

        Tab tabPokoje = new Tab("Pokoje", utworzPanelPokoi());
        tabPokoje.setClosable(false);

        Tab tabGoscie = new Tab("Goście", utworzPanelGosci());
        tabGoscie.setClosable(false);

        recepcjaTabPane.getTabs().addAll(tabRezerwacje, tabPokoje, tabGoscie);

        VBox recepcjaPanel = new VBox(10);
        recepcjaPanel.setPadding(new Insets(10));
        recepcjaPanel.getChildren().add(recepcjaTabPane);
        VBox.setVgrow(recepcjaTabPane, Priority.ALWAYS);

        Tab tabRecepcja = new Tab("🏨 Panel Recepcji", recepcjaPanel);
        tabRecepcja.setClosable(false);
        tabRecepcja.setStyle("-fx-font-size: 14;");

        mainTabPane.getTabs().addAll(tabKlient, tabRecepcja);

        // Panel logów na dole
        log("System hotelowy uruchomiony.");

        VBox root = new VBox(10);
        root.getChildren().addAll(mainTabPane, new Label("Log operacji:"), logArea);
        VBox.setVgrow(mainTabPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setScene(scene);
        primaryStage.show();

        odswiezWszystko();
    }

    /**
     * Inicjalizuje wszystkie warstwy systemu.
     */
    private void inicjalizujSystem() {
        // Warstwa DAO
        rezerwacjeDAO = new RezerwacjeDAO();
        pokojeDAO = new PokojeDAO();
        goscieDAO = new GoscieDAO();

        // Warstwa modelu
        model = new HotelModel(rezerwacjeDAO, pokojeDAO, goscieDAO, new FabrykaGosci());

        // Warstwa kontrolerów
        rezerwacjeKontroler = new RezerwacjeKontroler(model);
        zameldowanieKontroler = new ZameldowanieKontroler(model);
        wymeldowanieKontroler = new WymeldowanieKontroler(model);
        goscieKontroler = new GoscieKontroler(model);
    }

    /**
     * Dodaje przykładowe dane do systemu.
     */
    private void dodajPrzykladoweDane() {
        // Pokoje
        pokojeDAO.zapisz(new Pokoj(101, "Jednoosobowy", 150.0));
        pokojeDAO.zapisz(new Pokoj(102, "Jednoosobowy", 150.0));
        pokojeDAO.zapisz(new Pokoj(201, "Dwuosobowy", 250.0));
        pokojeDAO.zapisz(new Pokoj(202, "Dwuosobowy", 250.0));
        pokojeDAO.zapisz(new Pokoj(301, "Apartament", 500.0));

        // Goście
        Gosc gosc1 = new Gosc("Jan", "Kowalski", "jan.kowalski@email.pl");
        Gosc gosc2 = new Gosc("Anna", "Nowak", "anna.nowak@email.pl");
        Gosc gosc3 = new Gosc("Piotr", "Wiśniewski", "piotr.wisniewski@email.pl");
        goscieDAO.zapisz(gosc1);
        goscieDAO.zapisz(gosc2);
        goscieDAO.zapisz(gosc3);

        // Rezerwacje
        Pokoj pokoj101 = pokojeDAO.pobierz(101).get();
        Pokoj pokoj201 = pokojeDAO.pobierz(201).get();

        Rezerwacja rez1 = new Rezerwacja(LocalDate.now(), LocalDate.now().plusDays(3), gosc1, pokoj101);
        rez1.setStatus(Rezerwacja.Status.POTWIERDZONA);
        rezerwacjeDAO.zapisz(rez1);

        Rezerwacja rez2 = new Rezerwacja(LocalDate.now().plusDays(5), LocalDate.now().plusDays(8), gosc2, pokoj201);
        rezerwacjeDAO.zapisz(rez2);
    }

    // ==================== PANEL REZERWACJI ====================

    private VBox utworzPanelRezerwacji() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));

        // Tabela rezerwacji
        tabelaRezerwacji = new TableView<>();

        TableColumn<Rezerwacja, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colId.setPrefWidth(50);

        TableColumn<Rezerwacja, String> colGosc = new TableColumn<>("Gość");
        colGosc.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getGosc().getPelneNazwisko()));
        colGosc.setPrefWidth(150);

        TableColumn<Rezerwacja, Integer> colPokoj = new TableColumn<>("Pokój");
        colPokoj.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPokoj().getNumer()).asObject());
        colPokoj.setPrefWidth(70);

        TableColumn<Rezerwacja, String> colDataOd = new TableColumn<>("Od");
        colDataOd.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDataOd().toString()));
        colDataOd.setPrefWidth(100);

        TableColumn<Rezerwacja, String> colDataDo = new TableColumn<>("Do");
        colDataDo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDataDo().toString()));
        colDataDo.setPrefWidth(100);

        TableColumn<Rezerwacja, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().name()));
        colStatus.setPrefWidth(120);

        TableColumn<Rezerwacja, Double> colCena = new TableColumn<>("Cena");
        colCena.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().obliczCene()).asObject());
        colCena.setPrefWidth(80);

        tabelaRezerwacji.getColumns().addAll(colId, colGosc, colPokoj, colDataOd, colDataDo, colStatus, colCena);

        // Przyciski akcji
        HBox przyciski = new HBox(10);
        przyciski.setAlignment(Pos.CENTER_LEFT);

        Button btnNowaRezerwacja = new Button("Nowa rezerwacja");
        btnNowaRezerwacja.setOnAction(e -> pokazDialogNowejRezerwacji());

        Button btnZamelduj = new Button("Zamelduj");
        btnZamelduj.setOnAction(e -> zameldujWybrana());

        Button btnWymelduj = new Button("Wymelduj");
        btnWymelduj.setOnAction(e -> wymeldujWybrana());

        Button btnAnuluj = new Button("Anuluj rezerwację");
        btnAnuluj.setOnAction(e -> anulujWybrana());

        Button btnOdswiez = new Button("Odśwież");
        btnOdswiez.setOnAction(e -> odswiezRezerwacje());

        przyciski.getChildren().addAll(btnNowaRezerwacja, btnZamelduj, btnWymelduj, btnAnuluj, btnOdswiez);

        panel.getChildren().addAll(new Label("Lista rezerwacji:"), tabelaRezerwacji, przyciski);
        VBox.setVgrow(tabelaRezerwacji, Priority.ALWAYS);

        return panel;
    }

    private void pokazDialogNowejRezerwacji() {
        Dialog<Rezerwacja> dialog = new Dialog<>();
        dialog.setTitle("Nowa rezerwacja");
        dialog.setHeaderText("Wprowadź dane rezerwacji");

        ButtonType btnUtworzType = new ButtonType("Utwórz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnUtworzType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<Gosc> comboGosc = new ComboBox<>();
        comboGosc.getItems().addAll(goscieDAO.pobierzWszystkie());
        comboGosc.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Gosc g) {
                return g == null ? "" : g.getPelneNazwisko() + " (" + g.getEmail() + ")";
            }
            @Override
            public Gosc fromString(String s) { return null; }
        });

        ComboBox<Pokoj> comboPokoj = new ComboBox<>();
        comboPokoj.getItems().addAll(pokojeDAO.pobierzWszystkie());
        comboPokoj.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Pokoj p) {
                return p == null ? "" : "Pokój " + p.getNumer() + " (" + p.getTyp() + ", " + p.getCenaBazowa() + " zł)";
            }
            @Override
            public Pokoj fromString(String s) { return null; }
        });

        DatePicker dataOd = new DatePicker(LocalDate.now());
        DatePicker dataDo = new DatePicker(LocalDate.now().plusDays(3));

        grid.add(new Label("Gość:"), 0, 0);
        grid.add(comboGosc, 1, 0);
        grid.add(new Label("Pokój:"), 0, 1);
        grid.add(comboPokoj, 1, 1);
        grid.add(new Label("Data od:"), 0, 2);
        grid.add(dataOd, 1, 2);
        grid.add(new Label("Data do:"), 0, 3);
        grid.add(dataDo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnUtworzType) {
                Gosc gosc = comboGosc.getValue();
                Pokoj pokoj = comboPokoj.getValue();
                if (gosc != null && pokoj != null && dataOd.getValue() != null && dataDo.getValue() != null) {
                    return rezerwacjeKontroler.utworzRezerwacje(gosc, pokoj, dataOd.getValue(), dataDo.getValue());
                }
            }
            return null;
        });

        Optional<Rezerwacja> result = dialog.showAndWait();
        result.ifPresent(rez -> {
            if (rez != null) {
                log("Utworzono rezerwację ID=" + rez.getId() + " dla " + rez.getGosc().getPelneNazwisko());
                odswiezRezerwacje();
            } else {
                pokazBlad("Nie udało się utworzyć rezerwacji. Sprawdź dostępność pokoju.");
            }
        });
    }

    private void zameldujWybrana() {
        Rezerwacja wybrana = tabelaRezerwacji.getSelectionModel().getSelectedItem();
        if (wybrana == null) {
            pokazBlad("Wybierz rezerwację do zameldowania.");
            return;
        }

        boolean sukces = zameldowanieKontroler.zameldujGoscia(wybrana.getId());
        if (sukces) {
            log("Zameldowano gościa: " + wybrana.getGosc().getPelneNazwisko() + " (rezerwacja ID=" + wybrana.getId() + ")");
            odswiezWszystko();
        } else {
            pokazBlad("Nie można zameldować. Sprawdź status rezerwacji.");
        }
    }

    private void wymeldujWybrana() {
        Rezerwacja wybrana = tabelaRezerwacji.getSelectionModel().getSelectedItem();
        if (wybrana == null) {
            pokazBlad("Wybierz rezerwację do wymeldowania.");
            return;
        }

        boolean sukces = wymeldowanieKontroler.wymeldujGoscia(wybrana.getId());
        if (sukces) {
            log("Wymeldowano gościa: " + wybrana.getGosc().getPelneNazwisko() + " (rezerwacja ID=" + wybrana.getId() + ")");
            odswiezWszystko();
        } else {
            pokazBlad("Nie można wymeldować. Sprawdź status rezerwacji.");
        }
    }

    private void anulujWybrana() {
        Rezerwacja wybrana = tabelaRezerwacji.getSelectionModel().getSelectedItem();
        if (wybrana == null) {
            pokazBlad("Wybierz rezerwację do anulowania.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potwierdzenie");
        confirm.setHeaderText("Czy na pewno anulować rezerwację?");
        confirm.setContentText("Rezerwacja ID=" + wybrana.getId() + " dla " + wybrana.getGosc().getPelneNazwisko());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean sukces = rezerwacjeKontroler.anulujRezerwacje(wybrana.getId());
            if (sukces) {
                log("Anulowano rezerwację ID=" + wybrana.getId());
                odswiezWszystko();
            } else {
                pokazBlad("Nie można anulować rezerwacji.");
            }
        }
    }

    private void odswiezRezerwacje() {
        tabelaRezerwacji.getItems().clear();
        tabelaRezerwacji.getItems().addAll(rezerwacjeDAO.pobierzWszystkie());
    }

    // ==================== PANEL POKOI ====================

    private VBox utworzPanelPokoi() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));

        tabelaPokoi = new TableView<>();

        TableColumn<Pokoj, Integer> colNumer = new TableColumn<>("Numer");
        colNumer.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getNumer()).asObject());
        colNumer.setPrefWidth(80);

        TableColumn<Pokoj, String> colTyp = new TableColumn<>("Typ");
        colTyp.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTyp()));
        colTyp.setPrefWidth(150);

        TableColumn<Pokoj, Double> colCena = new TableColumn<>("Cena/noc");
        colCena.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getCenaBazowa()).asObject());
        colCena.setPrefWidth(100);

        TableColumn<Pokoj, String> colDostepnosc = new TableColumn<>("Dostępność");
        colDostepnosc.setCellValueFactory(data -> {
            boolean dostepny = pokojeDAO.czyDostepny(data.getValue().getNumer());
            return new javafx.beans.property.SimpleStringProperty(dostepny ? "Dostępny" : "Zajęty");
        });
        colDostepnosc.setPrefWidth(100);

        tabelaPokoi.getColumns().addAll(colNumer, colTyp, colCena, colDostepnosc);

        HBox przyciski = new HBox(10);
        Button btnNowyPokoj = new Button("Dodaj pokój");
        btnNowyPokoj.setOnAction(e -> pokazDialogNowegoPokoju());
        Button btnOdswiez = new Button("Odśwież");
        btnOdswiez.setOnAction(e -> odswiezPokoje());
        przyciski.getChildren().addAll(btnNowyPokoj, btnOdswiez);

        panel.getChildren().addAll(new Label("Lista pokoi:"), tabelaPokoi, przyciski);
        VBox.setVgrow(tabelaPokoi, Priority.ALWAYS);

        return panel;
    }

    private void pokazDialogNowegoPokoju() {
        Dialog<Pokoj> dialog = new Dialog<>();
        dialog.setTitle("Nowy pokój");

        ButtonType btnDodajType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnDodajType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtNumer = new TextField();
        txtNumer.setPromptText("np. 101");
        ComboBox<String> comboTyp = new ComboBox<>();
        comboTyp.getItems().addAll("Jednoosobowy", "Dwuosobowy", "Apartament");
        TextField txtCena = new TextField();
        txtCena.setPromptText("np. 150.00");

        grid.add(new Label("Numer pokoju:"), 0, 0);
        grid.add(txtNumer, 1, 0);
        grid.add(new Label("Typ:"), 0, 1);
        grid.add(comboTyp, 1, 1);
        grid.add(new Label("Cena za noc:"), 0, 2);
        grid.add(txtCena, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnDodajType) {
                try {
                    int numer = Integer.parseInt(txtNumer.getText());
                    String typ = comboTyp.getValue();
                    double cena = Double.parseDouble(txtCena.getText());
                    if (typ != null) {
                        Pokoj pokoj = new Pokoj(numer, typ, cena);
                        pokojeDAO.zapisz(pokoj);
                        return pokoj;
                    }
                } catch (NumberFormatException ex) {
                    pokazBlad("Nieprawidłowy format danych.");
                }
            }
            return null;
        });

        Optional<Pokoj> result = dialog.showAndWait();
        result.ifPresent(p -> {
            log("Dodano pokój nr " + p.getNumer());
            odswiezPokoje();
        });
    }

    private void odswiezPokoje() {
        tabelaPokoi.getItems().clear();
        tabelaPokoi.getItems().addAll(pokojeDAO.pobierzWszystkie());
    }

    // ==================== PANEL GOŚCI ====================

    private VBox utworzPanelGosci() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));

        tabelaGosci = new TableView<>();

        TableColumn<Gosc, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colId.setPrefWidth(50);

        TableColumn<Gosc, String> colImie = new TableColumn<>("Imię");
        colImie.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getImie()));
        colImie.setPrefWidth(120);

        TableColumn<Gosc, String> colNazwisko = new TableColumn<>("Nazwisko");
        colNazwisko.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNazwisko()));
        colNazwisko.setPrefWidth(150);

        TableColumn<Gosc, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colEmail.setPrefWidth(200);

        tabelaGosci.getColumns().addAll(colId, colImie, colNazwisko, colEmail);

        HBox przyciski = new HBox(10);
        Button btnNowyGosc = new Button("Dodaj gościa");
        btnNowyGosc.setOnAction(e -> pokazDialogNowegoGoscia());
        Button btnOdswiez = new Button("Odśwież");
        btnOdswiez.setOnAction(e -> odswiezGosci());
        przyciski.getChildren().addAll(btnNowyGosc, btnOdswiez);

        panel.getChildren().addAll(new Label("Lista gości:"), tabelaGosci, przyciski);
        VBox.setVgrow(tabelaGosci, Priority.ALWAYS);

        return panel;
    }

    private void pokazDialogNowegoGoscia() {
        Dialog<Gosc> dialog = new Dialog<>();
        dialog.setTitle("Nowy gość");

        ButtonType btnDodajType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnDodajType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtImie = new TextField();
        TextField txtNazwisko = new TextField();
        TextField txtEmail = new TextField();

        grid.add(new Label("Imię:"), 0, 0);
        grid.add(txtImie, 1, 0);
        grid.add(new Label("Nazwisko:"), 0, 1);
        grid.add(txtNazwisko, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(txtEmail, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnDodajType) {
                String imie = txtImie.getText().trim();
                String nazwisko = txtNazwisko.getText().trim();
                String email = txtEmail.getText().trim();
                if (!imie.isEmpty() && !nazwisko.isEmpty() && email.contains("@")) {
                    Gosc gosc = goscieKontroler.utworzProfilGoscia(imie, nazwisko, email);
                    return gosc;
                } else {
                    pokazBlad("Wypełnij wszystkie pola poprawnie.");
                }
            }
            return null;
        });

        Optional<Gosc> result = dialog.showAndWait();
        result.ifPresent(g -> {
            if (g != null) {
                log("Dodano gościa: " + g.getPelneNazwisko());
                odswiezGosci();
            }
        });
    }

    private void odswiezGosci() {
        tabelaGosci.getItems().clear();
        tabelaGosci.getItems().addAll(goscieDAO.pobierzWszystkie());
    }

    // ==================== POMOCNICZE ====================

    private void odswiezWszystko() {
        odswiezRezerwacje();
        odswiezPokoje();
        odswiezGosci();
    }

    private void log(String msg) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        if (logArea != null) {
            logArea.appendText("[" + timestamp + "] " + msg + "\n");
        }
    }

    private void pokazBlad(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
