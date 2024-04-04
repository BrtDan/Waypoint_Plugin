# Waypoint

**Versione:** 1.0.0  
**API Versione:** 1.20  
**Autore:** BrtDan 

## Descrizione
Waypoint è un plugin Bukkit/Spigot che consente ai giocatori di impostare dei waypoint e teletrasportarsi tra di essi.

## Comandi
- `/setwaypoint <waypoint_name>`: Imposta un waypoint nella posizione corrente del giocatore.
- `/teleport <waypoint_name>`: Teletrasporta il giocatore al waypoint specificato.
- `/deletewaypoint <waypoint_name>`: Elimina un waypoint precedentemente impostato.
- `/waypoints`: Mostra un elenco dei waypoint impostati.

## Funzionalità aggiuntive
- Il comando `/teleport` ora include un countdown di 3 secondi prima del teletrasporto.
- Durante il countdown, se il giocatore si muove, l'azione di teletrasporto viene annullata.

## Licenza
Questo progetto è concesso in licenza secondo i termini della [Licenza MIT](LICENSE).
