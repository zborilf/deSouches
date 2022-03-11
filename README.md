# deSouches
Systém pro MAPC 2019 - 2022



Instalace
... podle instrukci V. Uhlire na GITu

[Spusteni serveru]
... spustit server
... massim_2022-main/server/run.bat  (java -jar target\server-2020-2.0-jar-with-dependencies.jar \ --monitor 8000)

Napojeni se na server v java
... Spustit deSouches (projekt na GITu), vyvijime v Ideas
... Nutno nainstalovat [jade](https://medium.com/@jmackie97/setting-up-jade-with-intellij-2406f0495e9) a massim a obě knihovny nalinkovat
...	jade.Boot -nomtp -gui general:dsAgents.DeSouches (jdk 17)
... pokud jede Server (volba scenare, at to jede po iteracich), tak by se mely objevovat vypisy, co ma na senzorech za data
... agenti nic nedelaj, protoze je to stare pro predminuly rocnik, ale zaklad by tam mel byt
... tymy agentu pracuji na spolecne veci, ktera je koordinovana automatem
... pokud agent plni ulohu, plni, dokud neskonci, nebo neselze, v obou pripadech referuje deSouches, jak dopadl, ten pak na zaklade automatu dava dalsi pokyny


[Implementace]

Trida deSouches

vytvari agentni populaci
pokud agent zada praci, nejakou mu najde (prace = prirazeni scenare)
pokud agent dostane ranu, tak narika deSouches (asi by se melo resit v ramci scenaru a neotravovat deSouches)
pokud scenar selze, .... reaguje   (kdo reportuje?, asi komander scenare?)
	tuto informaci obdrzi od objektu tridy DSScenariou, kdyz v automatu scenare dojde do stavu 'uspech'
pokud scenar uspeje, .... reaguje  , 
	tuto informaci obdrzi od objektu tridy DSScenario, kdyz v automatu scenare dojde do stavu 'selhal'

Trida Scenare, zakladni metody

initScenarion
allocateAgents
checkEvent
goalFailed
goalCompleted


[Prace]

1, Upravit BB tak, aby odpovidala soucasnym pravidlum, tedy vymezit, s jakymi vsemi fakty bude agent pracovat
2, Rozsirit strukturu agenta o role a normy, ktere jsou nove. Tedy naplnit prislusne tridy pro nastavovani / editaci, zvazit, jake metody apod
3, Zapracovat na scenarich, ruzne mnoziny strategii a scenaru pro ruzne aktivni normy, zpracovat prechody mezi normami / nastaveni pravidel, 'panic' prechodne obdobi
4, Reset connection -> restart systemu, kde muzou agenti nest jiz nejake bloky apod.


ad 3, Brainstorm pro scenare

Vycisti goal area
Skupinovy ... vyres deadlock s ...

AntiMLFC strategie


Zanerad goal area

Zautoc na nepritele co stoji a drzi !! (ofenzivni)
   role pozorovatel (i skupinky)
	Hlida, zdali stojici a cekajici agent / rikejme mu aukl. Pokud se nehybe, je treba k nemu poslat utocnika, at jej propleskava
	Muze jich hlidat vic, v cyklu kontrolovat, ze jsou na svych mistech
   role utocnik

Seber, toulej se a cekej na prilezitost  (vrstvena reaktivni)

Tymovy ... de Souches skladani bloku

... jinak pro strycka prihodu najit automaticke rozdeleni roli, kolik jich pravdepodobne kdy je potreba
