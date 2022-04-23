# Verzování

Používáme schéma `x.y.z`, kde změny `z` znamenají pouze interní releasy, například nové testovací verze pro interní tým, `y` se zvedá při posílání nové verze do Google Play a `x` si necháváme v záloze pro zásadnější změny.

# Releasing

Pro automatizaci releasů používáme [Fastlane](https://fastlane.tools).

 - Release pro interní testování se provede na větvi `internal_release`. 
 - Produkční release se provede na větvi `release`. Tato větev v současnosti vede do alpha kanálu na Google Play. Po vydání produkční verze se nastaví na produkci.