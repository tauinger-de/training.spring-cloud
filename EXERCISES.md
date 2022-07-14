# Übungen 

## A) ConfigServer

### A1) Setup
1. Erstellen Sie ein neues Maven-Modul mit Namen "my-configserver"
2. Fügen Sie die `spring-cloud-config-server` Dependency der pom.xml hinzu
3. Erstellen Sie die Applikationsklasse
4. Konfigurieren Sie mittels einer neuen `src/main/resources/application.properties` Datei
im Projekt folgende Werte:
    ````properties
   server.port: 8888
   spring.profiles.active=native
   spring.cloud.config.server.native.searchLocations=classpath:/configurations
    ````
7. Erzeugen Sie im Internet ein hübsche ASCII Art Ausgabe für den Text "Config Server" und
   speichern dies in einer neuen Datei `src/main/resources/banner.txt` ab.

### A2) Konfigurationen hinterlegen
1. Legen Sie im Projekt ein leeres Verzeichnis `src/main/resources/configurations`
6. Erstellen Sie dort Dateien folgende Dateien, jeweils mit mind. dem Property `greeting.message`:
   1. `someApp.yml`
   2. `testApp-default.yml`
   3. `testApp-testProfile.yml`

### A3) Konfigurationen abfragen
1. Starten Sie die Anwendung
8. Rufen Sie folgende URLs auf -- was fällt Ihnen auf?
   - http://localhost:8888/someApp/default
   - http://localhost:8888/someApp/testProfile
   - http://localhost:8888/someApp/xyz
   - http://localhost:8888/testApp/default
   - http://localhost:8888/testApp/testProfile
   - http://localhost:8888/testApp/xyz

## B) OrderService

### B1) Setup

1. Erstellen Sie ein neues Maven-Modul mit Namen "my-orderservice"
2. Erstellen Sie die Applikationsklasse
3. Definieren Sie den Applikationsnamen und die Quelle der zu importierenden Properties fest (`application.properties`):
    ````properties
    spring.application.name: someApp
    spring.config.import=configserver:http://localhost:8888
    ````

### B2) Erster Test der Konfiguration

1. Erstellen Sie eine Klasse "OrderRestApi", die mit `@RestController` annotiert ist
2. Injecten Sie das Property `greeting.message` mittels `@Value`
3. Implementieren Sie einen `GET /greeting` Endpoint, der diese Message ausgibt
4. Starten Sie die Anwendung und rufen Sie http://localhost:8080/greeting auf
5. Ändern Sie den Anwendungsnamen auf "testApp"
6. Starten Sie die Anwendung und rufen Sie http://localhost:8080/greeting auf
7. Starten Sie die Anwendung mit dem aktiven Profil "testProfile" und rufen Sie http://localhost:8080/greeting auf

### B3) Spezifische konfiguration
1. Ändern Sie den Anwendungsnamen auf "orderservice"
2. Legen Sie eine neue `orderservice-default.yml` Datei im "configserver" Projekt an 
und starten Sie diesen neu
3. Starten Sie die Orderservice-Anwendung erneut und rufen Sie http://localhost:8080/greeting auf


## C) RegistryServer

### C1) Setup

1. Erstellen Sie ein neues Maven-Modul mit Namen "my-registryserver"
2. Fügen Sie folgende Dependency der pom.xml hinzu:
    ````xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
    ````
3. Erstellen Sie die Applikationsklasse, die folgende Annotation tragen muss: `@EnableEurekaServer`
4. Hinterlegen Sie folgende Konfiguration  in der `application.properties`, womit der Server auf
   einem alternativen Port im Standalone-Modus gestartet wird:
    ````properties
    server.port: 8761
    eureka.instance.hostname=localhost
    eureka.client.register-with-eureka=false
    eureka.client.fetch-registry=false
    eureka.client.service-url.default-zone=http://${eureka.instance.hostname}:${server.port}/eureka/
    ````

### C2) Start

1. Starten Sie den Registry-Server
2. Rufen Sie folgende URL auf, um das Admin Dashboard anzuzeigen: http://localhost:8761/


## D) ProductService

### D1) Setup

1. Erstellen Sie ein neues Maven-Modul mit Namen "my-productservice"
3. Erstellen Sie die Applikationsklasse
4. Definieren Sie den Applikationsnamen und die Quelle der zu importierenden Properties fest (`application.properties`):
    ````properties
    spring.application.name: productservice
    spring.config.import=configserver:http://localhost:8888
    ````
### D2) Geschäftslogik

1. Fügen Sie folgende Dependencies der pom.xml hinzu:
    ````xml
   <dependencies>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>   
        <dependency>
           <groupId>org.projectlombok</groupId>
           <artifactId>lombok</artifactId>
       </dependency>
   </dependencies>
    ````
2. Erstellen Sie eine Klasse "ProductService", der eine Methode `Product getProduct(String productId)` enthält.
Diese soll ein Dummy-Produkt mit Namen und Id ausliefern.
3. Machen Sie die Ausführung der Methode absichtlich langsam, indem Sie ein `Thread.sleep(1000)` einbauen.

### D3) REST API

1. Erstellen Sie eine Klasse "ProductRestApi", die mit `@RestController` annotiert ist
2. Injecten Sie hierein den `ProductService`
3. Implementieren Sie einen `GET /products/{productId}` Endpoint, der ein Produkt mittels dieses Service ausliefert

### D4) Service Registrierung

1. Fügen Sie der pom.xml folgende Dependency hinzu:
    ````xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    ````
2. Starten Sie die `ProductServiceApplication` Klasse (läuft der Registry-Server noch/schon?)
3. Rufen Sie folgende URL auf: http://localhost:8761/ -- Dort ist nun der PRODUCTSERVICE als 
registrierte Anwendung zu sehen.
4. Ein Click auf den Link "localhost:productservice" zeigt welches Ergebnis? Warum wohl?

### D5) Konfiguration

1. Erstellen Sie im **Config-Server** Projekt eine Konfigurationsdatei namens
`productservice-default.yml` mit folgendem Inhalt:
````yaml
management:
  endpoints:
    web:
      exposure:
        include: info, health

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
````
2. Nun den Config-Server und dann den Product-Service neu starten. Geht es nun?

### D6) Stress-Testing
1. Laden Sie den Apache HTTPD Server 2.4 oder 2.5 als ZIP von https://www.apachelounge.com/download/ herunter
2. Kopieren Sie aus dem "bin" Verzeichnis das Tool `ab.exe` in das Projektverzeichnis
3. Führen Sie folgenden Befehl aus: `ab.exe -n 10 -c 1 http://localhost:8080/products/123`
4. Und dann `ab.exe -n 100 -c 10 http://localhost:8080/products/123` -- was können Sie aus den Ergebnissen ablesen?