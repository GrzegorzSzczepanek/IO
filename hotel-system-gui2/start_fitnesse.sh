#!/bin/bash

# =============================================================================
# Skrypt uruchamiający FitNesse dla projektu Hotel System
# Automatycznie wybiera wolny port jeśli domyślny jest zajęty
# 
# Autorzy: Grzegorz Szczepanek, Iuliia Kapustinskaia, Lukerya Prakofyeva
# =============================================================================

# Kolory dla lepszej czytelności
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Ścieżka do projektu (dostosuj do swojej lokalizacji)
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
FITNESSE_JAR="$PROJECT_DIR/lib/fitnesse-standalone.jar"

# Domyślny port
DEFAULT_PORT=8080
MAX_PORT=8090

echo -e "${BLUE}=============================================${NC}"
echo -e "${BLUE}   FitNesse - System Hotelowy              ${NC}"
echo -e "${BLUE}=============================================${NC}"
echo ""

# Sprawdź czy fitnesse-standalone.jar istnieje
if [ ! -f "$FITNESSE_JAR" ]; then
    echo -e "${YELLOW}[INFO] Biblioteka fitnesse-standalone.jar nie znaleziona.${NC}"
    echo -e "${YELLOW}[INFO] Pobieram z oficjalnej strony FitNesse...${NC}"
    
    mkdir -p "$PROJECT_DIR/lib"
    
    # Pobieranie FitNesse
    curl -L -o "$FITNESSE_JAR" \
        "https://github.com/unclebob/fitnesse/releases/download/v20241026/fitnesse-standalone.jar" \
        2>/dev/null
    
    if [ $? -eq 0 ] && [ -f "$FITNESSE_JAR" ]; then
        echo -e "${GREEN}[OK] Pobrano fitnesse-standalone.jar${NC}"
    else
        echo -e "${RED}[BŁĄD] Nie udało się pobrać FitNesse.${NC}"
        echo -e "${YELLOW}Pobierz ręcznie z: https://github.com/unclebob/fitnesse/releases${NC}"
        echo -e "${YELLOW}Umieść plik jako: $FITNESSE_JAR${NC}"
        exit 1
    fi
fi

# Funkcja sprawdzająca czy port jest wolny
is_port_free() {
    local port=$1
    if command -v lsof &> /dev/null; then
        lsof -i :$port &> /dev/null
        return $?
    elif command -v netstat &> /dev/null; then
        netstat -tuln | grep -q ":$port "
        return $?
    else
        # Próba połączenia
        (echo >/dev/tcp/localhost/$port) &>/dev/null
        return $?
    fi
}

# Znajdź wolny port
find_free_port() {
    local port=$DEFAULT_PORT
    while [ $port -le $MAX_PORT ]; do
        if ! is_port_free $port; then
            echo $port
            return 0
        fi
        echo -e "${YELLOW}[INFO] Port $port jest zajęty, próbuję następny...${NC}"
        port=$((port + 1))
    done
    echo -1
    return 1
}

# Kompilacja projektu przed uruchomieniem testów
echo -e "${BLUE}[INFO] Kompilacja projektu Maven...${NC}"
cd "$PROJECT_DIR"

if command -v mvn &> /dev/null; then
    mvn compile -q
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}[OK] Kompilacja zakończona pomyślnie${NC}"
    else
        echo -e "${RED}[BŁĄD] Kompilacja nie powiodła się${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}[UWAGA] Maven nie znaleziony, pomijam kompilację${NC}"
fi

# Znajdź wolny port
PORT=$(find_free_port)

if [ "$PORT" -eq "-1" ]; then
    echo -e "${RED}[BŁĄD] Nie znaleziono wolnego portu w zakresie $DEFAULT_PORT-$MAX_PORT${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}=============================================${NC}"
echo -e "${GREEN}   Uruchamiam FitNesse na porcie: $PORT     ${NC}"
echo -e "${GREEN}=============================================${NC}"
echo ""
echo -e "${BLUE}[INFO] Otwórz przeglądarkę: ${NC}${GREEN}http://localhost:$PORT${NC}"
echo -e "${BLUE}[INFO] Aby zatrzymać, naciśnij Ctrl+C${NC}"
echo ""

# Uruchom FitNesse
java -jar "$FITNESSE_JAR" -p $PORT -d "$PROJECT_DIR"
