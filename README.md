# deSouches
Systém pro MAPC 2019 - 2022



## Instalace

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

tymy agentu pracuji na spolecne veci, ktera je koordinovana automatem

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


### Prace

- [ ] Upravit BB tak, aby odpovidala soucasnym pravidlum, tedy vymezit, s jakymi vsemi fakty bude agent pracovat
- [ ] Rozsirit strukturu agenta o role a normy, ktere jsou nove. Tedy naplnit prislusne tridy pro nastavovani / editaci, zvazit, jake metody apod
- [ ] Zapracovat na scenarich, ruzne mnoziny strategii a scenaru pro ruzne aktivni normy, zpracovat prechody mezi normami / nastaveni pravidel, 'panic' prechodne obdobi + Brainstorm pro scenare
- [ ] Reset connection -> restart systemu, kde muzou agenti nest jiz nejake bloky apod.
- [ ] Vycisti goal area
- [ ] Skupinovy ... vyres deadlock s ...


- [ ] Seber, toulej se a cekej na prilezitost  (vrstvena reaktivni)

- [ ] Tymovy ... de Souches skladani bloku

- [ ] jinak pro strycka prihodu najit automaticke rozdeleni roli, kolik jich pravdepodobne kdy je potreba

#### AntiMLFC strategie:

- [ ] Zanerad goal area

- [ ] Zautoc na nepritele co stoji a drzi !! (ofenzivni)
  role pozorovatel (i skupinky)
  Hlida, zdali stojici a cekajici agent / rikejme mu aukl. Pokud se nehybe, je treba k nemu poslat utocnika, at jej propleskava
  Muze jich hlidat vic, v cyklu kontrolovat, ze jsou na svych mistech
  role utocnik
