# System Zarządzania Hotelem - Projekt Java z Testami JUnit 5

## Informacje o projekcie

**Temat:** Testowanie jednostkowe operacji klas (Laboratoria 12-13)  
**Przedmiot:** Inżynieria Oprogramowania  
**Technologie:** Java 17+, JUnit 5, Mockito 5

---

## Struktura projektu

```
hotel-system/
├── pom.xml                          # Konfiguracja Maven
├── src/
│   ├── main/java/hotel/
│   │   ├── model/                   # Warstwa encji
│   │   │   ├── Pokoj.java
│   │   │   ├── Gosc.java
│   │   │   ├── Rezerwacja.java
│   │   │   ├── IDodatek.java        # Interfejs dekoratora
│   │   │   ├── Sniadanie.java       # Implementacja IDodatek
│   │   │   ├── Parking.java         # Implementacja IDodatek
│   │   │   ├── IGoscFactory.java    # Interfejs fabryki
│   │   │   ├── FabrykaGosci.java    # Implementacja fabryki
│   │   │   ├── IHotelModel.java     # Interfejs modelu
│   │   │   └── HotelModel.java      # Główny model biznesowy
│   │   ├── controller/              # Warstwa kontroli
│   │   │   ├── IRezerwacjeKontroler.java
│   │   │   ├── RezerwacjeKontroler.java
│   │   │   ├── IZameldowanieKontroler.java
│   │   │   ├── ZameldowanieKontroler.java
│   │   │   ├── IWymeldowanieKontroler.java
│   │   │   ├── WymeldowanieKontroler.java
│   │   │   ├── IGoscieKontroler.java
│   │   │   └── GoscieKontroler.java
│   │   ├── dao/                     # Warstwa dostępu do danych
│   │   │   ├── IDAO.java
│   │   │   ├── GoscieDAO.java
│   │   │   ├── PokojeDAO.java
│   │   │   └── RezerwacjeDAO.java
│   │   └── Main.java                # Klasa demonstracyjna
│   └── test/java/hotel/
│       ├── model/                   # Testy warstwy encji
│       │   ├── TestPokoj.java
│       │   ├── TestGosc.java
│       │   ├── TestRezerwacja.java
│       │   └── TestDodatki.java
│       ├── controller/              # Testy z mockowaniem
│       │   ├── TestRezerwacjeKontrolerMock.java
│       │   ├── TestZameldowanieKontrolerMock.java
│       │   └── TestWymeldowanieKontrolerMock.java
│       ├── dao/                     # Testy DAO
│       │   └── TestGoscieDAO.java
│       └── suite/                   # Zestawy testów
│           ├── SuiteTestyEncji.java
│           ├── SuiteTestyKontroli.java
│           ├── SuiteTestyWalidacji.java
│           ├── SuiteTestyParametryzowane.java
│           └── SuiteTestyMock.java
```

---

## Instalacja i uruchomienie

### Wymagania wstępne

- **Java JDK 17+** (zalecana 21)
- **Maven 3.8+** lub **Gradle 8+**
- IDE: IntelliJ IDEA, Eclipse, VS Code z rozszerzeniem Java

---

## 🍎 Instrukcja dla macOS

### 1. Instalacja Java (jeśli nie masz)

```bash
# Przez Homebrew (zalecane)
brew install openjdk@21

# Dodaj do PATH (dla zsh - domyślny shell na macOS)
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Sprawdź instalację
java -version
```

### 2. Instalacja Maven

```bash
brew install maven

# Sprawdź instalację
mvn -version
```

### 3. Rozpakowanie i uruchomienie projektu

```bash
# Rozpakuj archiwum
unzip hotel-system-projekt.zip
cd hotel-system

# Kompilacja projektu
mvn compile

# Uruchomienie demonstracji
mvn exec:java -Dexec.mainClass="hotel.Main"

# Uruchomienie WSZYSTKICH testów
   mvn test

# Uruchomienie konkretnej klasy testów
mvn test -Dtest=TestPokoj

# Uruchomienie testów z określonym tagiem
mvn test -Dgroups="encja"

# Uruchomienie zestawu testów
mvn test -Dtest=SuiteTestyEncji
```

### 4. Import do IntelliJ IDEA (macOS)

1. Otwórz IntelliJ IDEA
2. `File` → `Open` → wybierz folder `hotel-system`
3. IntelliJ automatycznie wykryje projekt Maven
4. Poczekaj na indeksowanie i pobranie zależności
5. Uruchom testy: `Run` → `Run All Tests` lub kliknij ▶️ przy klasie testowej

---

## 🪟 Instrukcja dla Windows

### 1. Instalacja Java

**Opcja A: Instalator Oracle/OpenJDK**
1. Pobierz JDK 21 z https://adoptium.net/ (Temurin) lub https://www.oracle.com/java/
2. Uruchom instalator i postępuj zgodnie z instrukcjami
3. Dodaj JAVA_HOME do zmiennych środowiskowych:
   - `Win + X` → `System` → `Zaawansowane ustawienia systemu` → `Zmienne środowiskowe`
   - Nowa zmienna systemowa: `JAVA_HOME` = `C:\Program Files\Java\jdk-21`
   - Edytuj `Path` i dodaj: `%JAVA_HOME%\bin`

**Opcja B: Przez winget (Windows 11)**
```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```

**Opcja C: Przez Chocolatey**
```powershell
choco install temurin21
```

Sprawdź instalację:
```cmd
java -version
javac -version
```

### 2. Instalacja Maven

**Opcja A: Ręczna instalacja**
1. Pobierz Maven z https://maven.apache.org/download.cgi
2. Rozpakuj do `C:\Program Files\Apache\maven`
3. Dodaj zmienne środowiskowe:
   - `MAVEN_HOME` = `C:\Program Files\Apache\maven`
   - Dodaj do `Path`: `%MAVEN_HOME%\bin`

**Opcja B: Przez Chocolatey**
```powershell
choco install maven
```

**Opcja C: Przez winget**
```powershell
winget install Apache.Maven
```

Sprawdź instalację:
```cmd
mvn -version
```

### 3. Rozpakowanie i uruchomienie projektu

```cmd
:: Rozpakuj archiwum (lub użyj Eksploratora Windows)
tar -xf hotel-system-projekt.zip
cd hotel-system

:: Kompilacja projektu
mvn compile

:: Uruchomienie demonstracji
mvn exec:java -Dexec.mainClass="hotel.Main"

:: Uruchomienie WSZYSTKICH testów
mvn test

:: Uruchomienie konkretnej klasy testów
mvn test -Dtest=TestPokoj

:: Uruchomienie testów z tagiem
mvn test -Dgroups="walidacja"
```

### 4. Import do IntelliJ IDEA (Windows)

1. Otwórz IntelliJ IDEA
2. `File` → `Open` → wybierz folder `hotel-system`
3. Wybierz "Trust Project"
4. Poczekaj na pobranie zależności Maven
5. Uruchom testy: PPM na folderze `src/test/java` → `Run 'All Tests'`

### 5. Import do Eclipse (Windows/macOS)

1. `File` → `Import` → `Maven` → `Existing Maven Projects`
2. Wybierz folder `hotel-system`
3. Kliknij `Finish`
4. PPM na projekcie → `Run As` → `JUnit Test`

---

## Uruchamianie testów - szczegóły

### Wszystkie testy
```bash
mvn test
```

### Konkretna klasa testowa
```bash
mvn test -Dtest=TestPokoj
mvn test -Dtest=TestGosc
mvn test -Dtest=TestRezerwacja
mvn test -Dtest=TestRezerwacjeKontrolerMock
```

### Testy po tagach
```bash
# Testy warstwy encji
mvn test -Dgroups="encja"

# Testy walidacji
mvn test -Dgroups="walidacja"

# Testy parametryzowane
mvn test -Dgroups="parametryzowany"

# Testy z mockowaniem
mvn test -Dgroups="mock"

# Wykluczenie tagów
mvn test -DexcludedGroups="mock"
```

### Zestawy testów (Suite)
```bash
mvn test -Dtest=SuiteTestyEncji
mvn test -Dtest=SuiteTestyKontroli
mvn test -Dtest=SuiteTestyWalidacji
mvn test -Dtest=SuiteTestyParametryzowane
mvn test -Dtest=SuiteTestyMock
```

### Raport z testów
```bash
# Generowanie raportu HTML
mvn surefire-report:report

# Raport będzie w: target/site/surefire-report.html
```

---

## Kompilacja i uruchomienie BEZ Maven (ręcznie)

Jeśli nie chcesz używać Maven, możesz skompilować ręcznie:

### Pobierz biblioteki

Pobierz następujące pliki JAR z https://repo1.maven.org/maven2/:

```
junit-jupiter-api-5.10.0.jar
junit-jupiter-engine-5.10.0.jar
junit-jupiter-params-5.10.0.jar
junit-platform-commons-1.10.0.jar
junit-platform-engine-1.10.0.jar
junit-platform-launcher-1.10.0.jar
junit-platform-suite-api-1.10.0.jar
junit-platform-suite-engine-1.10.0.jar
mockito-core-5.7.0.jar
mockito-junit-jupiter-5.7.0.jar
byte-buddy-1.14.9.jar
byte-buddy-agent-1.14.9.jar
objenesis-3.3.jar
opentest4j-1.3.0.jar
apiguardian-api-1.1.2.jar
```

Umieść je w folderze `lib/`.

### Kompilacja (macOS/Linux)

```bash
# Kompilacja klas głównych
mkdir -p target/classes
javac -d target/classes $(find src/main/java -name "*.java")

# Uruchomienie demo
java -cp target/classes hotel.Main

# Kompilacja testów (wymaga bibliotek w lib/)
mkdir -p target/test-classes
javac -d target/test-classes -cp "target/classes:lib/*" $(find src/test/java -name "*.java")

# Uruchomienie testów
java -jar lib/junit-platform-console-standalone-1.10.0.jar \
  --class-path "target/classes:target/test-classes" \
  --scan-class-path
```

### Kompilacja (Windows CMD)

```cmd
:: Kompilacja klas głównych
mkdir target\classes
dir /s /b src\main\java\*.java > sources.txt
javac -d target\classes @sources.txt

:: Uruchomienie demo
java -cp target\classes hotel.Main

:: Kompilacja testów (wymaga bibliotek w lib\)
mkdir target\test-classes
dir /s /b src\test\java\*.java > test-sources.txt
javac -d target\test-classes -cp "target\classes;lib\*" @test-sources.txt
```

---

## Opis testów

### Zadanie 1: Testy bez mockowania
- `TestPokoj` - 14 testów konstruktora, getterów, setterów
- `TestGosc` - 18 testów z parametryzacją
- `TestRezerwacja` - 20 testów operacji biznesowych
- `TestDodatki` - 16 testów wzorca Dekorator
- `TestGoscieDAO` - 15 testów operacji CRUD

### Zadanie 2: Testy z Mockito
- `TestRezerwacjeKontrolerMock` - 16 testów z `@Mock`, `@InjectMocks`
- `TestZameldowanieKontrolerMock` - 5 testów
- `TestWymeldowanieKontrolerMock` - 9 testów

### Zadanie 3: Zestawy testów
- `SuiteTestyEncji` - pakiet `hotel.model`
- `SuiteTestyKontroli` - pakiet `hotel.controller`
- `SuiteTestyWalidacji` - tag "walidacja", bez "mock"
- `SuiteTestyParametryzowane` - tag "parametryzowany"
- `SuiteTestyMock` - tag "mock"

---

## Użyte adnotacje JUnit 5

| Adnotacja | Opis |
|-----------|------|
| `@Test` | Oznacza metodę testową |
| `@DisplayName` | Czytelna nazwa testu |
| `@Order` | Kolejność wykonania |
| `@Tag` | Tagowanie testów |
| `@BeforeAll` | Przed wszystkimi testami |
| `@BeforeEach` | Przed każdym testem |
| `@AfterAll` | Po wszystkich testach |
| `@AfterEach` | Po każdym teście |
| `@ParameterizedTest` | Test parametryzowany |
| `@ValueSource` | Źródło wartości prostych |
| `@CsvSource` | Źródło danych CSV |
| `@MethodSource` | Źródło z metody |
| `@NullAndEmptySource` | Null i puste wartości |

## Użyte adnotacje Mockito

| Adnotacja | Opis |
|-----------|------|
| `@Mock` | Tworzy mock obiektu |
| `@InjectMocks` | Wstrzykuje mocki |
| `when().thenReturn()` | Definiuje zachowanie |
| `when().thenThrow()` | Definiuje wyjątek |
| `verify()` | Weryfikuje wywołanie |
| `times()` | Liczba wywołań |
| `never()` | Brak wywołań |
| `inOrder()` | Kolejność wywołań |

---

## Rozwiązywanie problemów

### "java: command not found"
- Sprawdź czy Java jest zainstalowana: `java -version`
- Sprawdź zmienną `JAVA_HOME`
- Na Windows: uruchom ponownie terminal po instalacji

### "mvn: command not found"
- Sprawdź czy Maven jest zainstalowany: `mvn -version`
- Sprawdź zmienną `MAVEN_HOME` i `PATH`

### Błędy kompilacji w IDE
- PPM na projekcie → `Maven` → `Reload Project`
- Sprawdź wersję JDK w ustawieniach projektu (17+)

### Testy nie przechodzą
- Upewnij się, że wszystkie zależności zostały pobrane
- Sprawdź czy nie ma konfliktów wersji bibliotek
# IO
