# Verzování

Používáme schéma `x.y.z`, kde změny `z` znamenají pouze interní releasy, například nové testovací verze pro interní tým, `y` se zvedá při posílání nové verze do Google Play a `x` si necháváme v záloze pro zásadnější změny.

# Releasing

Pro automatizaci releasů používáme [Fastlane](https://fastlane.tools).

 - Release se provede na větvi `release`. Tato větev vede do `internal` kanálu na Google Play. Pro vydání produkční verze se v Google Play Console přesune vydání do produkčního kanálu.