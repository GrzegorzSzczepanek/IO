# Kompletny przepływ: Anulowanie Rezerwacji
# Od interfejsu IHotelModel do testów jednostkowych

Ten dokument pokazuje pełną implementację funkcjonalności "Anuluj Rezerwację"
przechodząc przez wszystkie warstwy architektury zgodnie z diagramem UML.

## Architektura przepływu:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              WARSTWA PREZENTACJI                            │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    AnulujRezerwacje (UseCase)                        │   │
│  │            - wykonaj(rezerwacjaId): boolean                          │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                              WARSTWA KONTROLERA                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │              IRezerwacjeKontroler (Interface)                        │   │
│  │            - anulujRezerwacje(id): boolean                           │   │
│  │            - pobierzOplateZaAnulowanie(id): double                   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │              RezerwacjeKontroler (Implementacja)                     │   │
│  │            - model: IHotelModel                                      │   │
│  │            - strategiaAnulowania: IStrategiaAnulowaniaRezerwacji     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                              WARSTWA MODELU                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                   IHotelModel (Interface)                            │   │
│  │            - anulujRezerwacje(id): boolean                           │   │
│  │            - pobierzOplate(id): double                               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                   HotelModel (Implementacja)                         │   │
│  │            - rezerwacjeDAO: RezerwacjeDAO                            │   │
│  │            - pokojeDAO: PokojeDAO                                    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                              WARSTWA DAO                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                   IDAO<T> (Generic Interface)                        │   │
│  │            - pobierz(id): T                                          │   │
│  │            - aktualizuj(entity): void                                │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                   RezerwacjeDAO (Implementacja)                      │   │
│  │            - rezerwacje: Map<Integer, Rezerwacja>                    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                              WARSTWA ENCJI                                  │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      Rezerwacja (Entity)                             │   │
│  │            - id: int                                                 │   │
│  │            - status: StatusRezerwacji                                │   │
│  │            - dataOd, dataDo: LocalDate                               │   │
│  │            - pokoj: Pokoj                                            │   │
│  │            - gosc: Gosc                                              │   │
│  │            - obliczCene(): double                                    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Strategia anulowania (wzorzec Strategy):

```
┌─────────────────────────────────────────────────────────────────────────────┐
│              IStrategiaAnulowaniaRezerwacji (Interface)                     │
│            - obliczKareZaAnulowanie(rezerwacja): double                     │
│            - czyMoznaAnulowac(rezerwacja): boolean                          │
└─────────────────────────────────────────────────────────────────────────────┘
                    ▲                              ▲
                    │                              │
     ┌──────────────┴──────────┐    ┌─────────────┴────────────┐
     │  AnulowaniePrzezGoscia  │    │ AnulowaniePrzezRecepcje  │
     │  - kara zależna od      │    │ - bez kary               │
     │    terminu              │    │ - zawsze można           │
     └─────────────────────────┘    └──────────────────────────┘
```
