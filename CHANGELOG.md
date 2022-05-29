Details about this file’s format at <http://keepachangelog.com/>. 
TODO: The change log is parsed automatically when minting releases through Fastlane, see `Fastlane/Fastfile`.

## [1.1.6] - 2022-05-27Z
- Bug fix: Nešlo jít do oblíbených https://github.com/cesko-digital/movapp-android/issues/13
- Hvězdička oblíbených změněna na modrou, pokud je vybrána 

## [1.1.6] - 2022-05-27Z

- Chování vyhledávání: Při smazání vyhlédávacího výrazu přejde na seznam frází.Vyhledává ve všech výrazech nikoliv v názvech oborů. Vyhledává v oblíbených i ve všech slovech zároveň.
- Ikona oblíbených je pouze obrys hvězdy místo plné hvězdy
- Ve slovníku se zachovávají obrazovky a pozice seznamů když se odejde do jiné zóny (Děti, Abeceda, ..)
- Na úvodní obrazovce funguje tlačítko zpět a tlačítko start není kapitálkami

## [1.1.0] - 2022-04-29Z

- Design podle webu a Sketche
- Obrazovka 'O nás' pouze v českém a ukrajinském jazyce. Nastaví se podle systémového jazyka. Pokud není dostupný nastaví se ukrajinština
- Vyhledávání vždy vyhledává ve všech slovíčkách. Používá Levensteinovu vzdálenost. Když je pole vyhledávání prázdné nezobrazí se nic
- Abeceda: aktualizace dat abecedy 28.4.2022, aplikace si pamatuje místo prohlížení abecedy i když se vypne aplikace
- Překlad aplikace je zatím částečný
- Noční/tmavý režim