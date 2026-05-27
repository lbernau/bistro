# Aufgabe: Mini-Bestellsystem für ein Bistro
Entwicklung eines Spring Boot Projekts, das eine einfache REST-API für ein Bistro bereitstellt. Kunden 
können Produkte anzeigen und Bestellungen aufgeben. Die Produkte werden automatisiert über eine CSV-
Datei mittels Spring Integration importiert.

Anforderungen:
1. Entities / Datenmodell:
   ◦ Product
   ◦ Order
   ◦ OrderItem
2. REST-Endpoints:
   ◦ GET /products — Liste aller Produkte
   ◦ GET /products/{id} — Produktdetails anzeigen
   ◦ POST /orders — Neue Bestellung aufgeben
   ◦ GET /orders/{id} — Bestelldetails anzeigen
3. Validierung:
   ◦ Es dürfen nur existierende Produkte bestellt werden
   ◦ Die Menge eines Produkts muss > 0 sein
4. Persistenz:
   ◦ Nutzung von JPA
5. Business-Logik:
   ◦ Berechnung des Gesamtpreises einer Bestellung
   ◦ Rabatte:
   ▪ Happy Hour: Täglich zwischen 17:00 und 19:00 Uhr, z.B. 2 Pizzen zum Preis von einer
   ◦ Die Bestellung soll im folgenden Format als Beleg dargestellt werden:
```text
-------------------------
Table Nr. 10
-------------------------
2 x Pizza @ 10.0 = 20.0
2 x Cola @ 2.5 = 7.5
-------------------------
Subtotal: 27.5
Discount: 10%
Total: 24.75
```

◦ Unit- und Integration-Tests für Service- und Controller-Layer
◦ Verwendung von DTOs
◦ Swagger/OpenAPI Integration

---

## Verwendung des Imports
Der Produkt-Import überwacht das Verzeichnis ./data/import 
Zu Test-Zwecken liegen im Archiv (./data/archive) zwei Dateien um einen erfolgreichen bzw. nicht erfolgreichen Import anzustoßen

[!NOTE]  
Aufgrund der Anforderungen wurde auf das Testing des Integration-Flows verzichtet.