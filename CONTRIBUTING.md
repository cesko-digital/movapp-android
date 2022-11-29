If you want to contribute just take some issue here on github. If you want to ge deeper into project planning or get social get access to [Slack](https://cesko-digital.slack.com/archives/C036GLKL7ME)

- App is aiming to be in sync with features from [Web](https://github.com/cesko-digital/movapp) and [iOS](https://github.com/cesko-digital/movapp-apple)

# App Architecture
 Techstack: Kotlin, XML layouts, Jetpack (Navigation, DataStore, Room, Material.io), Kotlin coroutines, Mockito
 MVVM, Repo
- feature based packaging

# Git Branching
- always use 'rebase'
- 'main' - development branch
- 'release' - Do not use merge. Once the main branch is ready for internal testing, use 'rebase main'. It will push by CI to Gplay for internal testing.
- use 'feat/featureName','dev/developerName','bugfix/fixDesc' ...

# Links
We communicate thru slack, google meet, github issues and rarely trello (Mostly in czech language but it won't be problem to switch to english. It is used mainly for ideas kickoff, UX, QA)
[Trello](https://trello.com/b/XumGa4K8/movapp-backlog)
[Slack](https://cesko-digital.slack.com/archives/C036GLKL7ME) search for channels 'movapp'

# CI
see github actions pane
[Fastlane](https://fastlane.tools) - push to 'release' branch goes to Gplay internal testing, when tested by QA, they are pushed to production.

# Versioning
We use x.y.z versioning, where increasing x is reserved for major changes, y should be increased for public release, and z for internal testing. Always add a tag with version increasing.

# Release Process
1. in `app/build.gradle` increase `versionName`
2. merge branch `main` to `release` (this executes release to internal testing on Gplay)
3. add git tag with version ie. `1.2.3` from `versionName`, tag message should be `Version 1.2.3` (just for info, no automation relates to it)
4. manage testing requirements thru [Movapp-Gdrive/Testovani](https://drive.google.com/drive/folders/1hthF_hLV7QykVr4M2iNrZeaNjvkCF2Wf?usp=sharing)
5. contact QA thru slack to start testing
6. once the version is ready for public release, log into [google console](https://play.google.com/console) and move the version from internal testing to production

members having release signing keys and access to Gplay console: [zoul](https://github.com/zoul), [vchlum](https://github.com/vchlum), [OndrejMalek](https://github.com/OndrejMalek)