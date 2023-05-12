## Dlaczego aplikacja webowa?
Uzałem że w ten sposób będę mógł pokazać najlepiej swoje umiejętności gdyż mam chyba największą wiedzę jeśli chodzi o budowanie tego typu aplikacji, mimo iż dla tego typu zadania wydaje mi się że nie jest to najlepsza opcja.

## Działanie algorytmu
Algorytm wybiera najbliższe piętro w kierunku w którym porusza się winda(góra, dół), zatem najpierw wybierany jest kierunek windy w którym będzie się poruszać
a dopiero potem wybierane jest najbliższe piętro. Nie można wybrać piętra na którym znajduje się aktualnie winda.

* jeśli winda porusza się w górę to wybrane zostanie najbliższe piętro w tym kierunku mimo iż bliżej 
może znajdować się piętro w kierunku przeciwnym
* jeśli winda oczekuje czyli nie porusza się ani w góre ani w dół to winda zacznie poruszać 
się w kierunku piętra które zostało wybrane jako pierwsze a następnie znajdzie
najbliższe piętro na którym ma się zatrzymać
* gdy nie będzie już pięter na które ma sie udać winda w kierunku w którym porusza się winda 
to kierunek windy zostanie zmieniony i zostanie wybrane najbliższe piętro na które winda ma się udać
* winda przejdzie w stan oczekiwania gdy nie będzie musiała udać się na żadne piętro
a będzie oczekiwała na piętrze na którym się zatrzymała.

## Przykład
Winda oczekuje na piętrze 3, wchodzi trzech ludzi, najpierw zostaje przyciśnięty przycisk 10
potem 0 a potem 5 -> winda zacznie poruszać się do góry bo jako pierwsze zostało wybrane piętro
które znajduję się wyżej, winda najpierw zatrzyma się na piętrze 5 potem 10  a na końcu piętrze 0,
jeśli na 5 piętrze ktoś wsiadł i wybrał piętro 11 to kolejność pięter będzie następująca 5 -> 10 -> 11 -> 0.

## Oznaczenia na windzie

![](screenshots/img.png)
* przycisk "add elevator" dodaje winde
* przycisk "simulate" uruchamia wszystkie windy
* zielona obwódka przy numerze oznacza że winda oczekuje na tym piętrze
* czerwona obwódka przy numerze oznacza że piętro zostało wybrane

## Uruchomienie aplikacji
By uruchomić aplikacje należy mieć zainstalowanego dockera. <br>
Uruchomienie za pomocą dockera -> należy przejść do folderu z aplikacją - ten folder w którym znajduje się plik docker-compose.yml
następnie wpisać komende `docker-compose up` i poczekać na uruchomienie, frontend troche długo się buduje.
Następnie otworzyć przeglądarke i udać się pod adres `http://localhost:3000/`


## Działanie aplikacji
Frontend wysyła zapytanie do backendu by dostać informacje o aktualnym stanie wind co 1 sekunde. 
By zmiany stanu windy były widoczne dla użytkownika symulowane jest opóźnienie pomiędzy zmianą pięter, otwarciem drzwi czy zamknięciem drzwi. <br>
Czas opóźnienia jest konfigurowalny co można zobaczyć w klasie ElevatorConfig. <br>
Po stronie frontendowej założono że winda ma maksymalnie 16 pięter przy czym maksymalne piętro i minimalne można skonfigurować jedynie dla backendu.
