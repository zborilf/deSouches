# deSouches
System for MAPC 2019 - 2022



## Instalation

### Spusteni serveru
Nejprve je nutné sestavit pomocí `mvn package`.

Ve složce `massim_2022/server` pomocí  `java -jar target/server-2020-2.0-jar-with-dependencies.jar --monitor 8000`.

### Spusteni klienta
- [ ] nastavit JDK17
- [ ] stahnout a nalinkovat knihovnu [jade](https://medium.com/@jmackie97/setting-up-jade-with-intellij-2406f0495e9)
- [ ] nalinkovat sestavenou knihovnu z `massim_2022/target/massim-2022-X.X-bin.tar.gz`
- [ ] nastavit konfiguraci spuštění:
  - hlavní třída: `jade.Boot`
  - parametry: `-nomtp [-gui] general:dsAgents.DeSouches`

pokud jede Server (volba scenare, at to jede po iteracich), tak by se mely objevovat vypisy, co ma na senzorech za data


agenti nic nedelaj, protoze je to stare pro predminuly rocnik, ale zaklad by tam mel byt

tymy agentu pracuji na spolecne veci, ktera je koordinovana automate

pokud agent plni ulohu, plni, dokud neskonci, nebo neselze, v obou pripadech referuje deSouches, jak dopadl, ten pak na zaklade automatu dava dalsi pokyny


## Implementace

### Trida deSouches

vytvari agentni populaci
pokud agent zada praci, nejakou mu najde (prace = prirazeni scenare)

pokud agent dostane ranu, tak narika deSouches (asi by se melo resit v ramci scenaru a neotravovat deSouches)

pokud scenar selze, .... reaguje   (kdo reportuje?, asi komander scenare?)
	tuto informaci obdrzi od objektu tridy DSScenariou, kdyz v automatu scenare dojde do stavu 'uspech'

pokud scenar uspeje, .... reaguje  , 
	tuto informaci obdrzi od objektu tridy DSScenario, kdyz v automatu scenare dojde do stavu 'selhal'

### Trida Scenare, zakladni metody

- initScenarion
- allocateAgents
- checkEvent
- goalFailed
- goalCompleted


