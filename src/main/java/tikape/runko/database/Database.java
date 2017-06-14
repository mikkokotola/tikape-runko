package tikape.runko.database;

import java.sql.*;
import java.util.*;
import java.net.*;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;

        init();
    }

    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }

        return DriverManager.getConnection(databaseAddress);
    }

    public void init() {
        List<String> lauseet = null;

        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }

    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("DROP TABLE Keskustelualue;");
        lista.add("DROP TABLE Keskustelu;");
        lista.add("DROP TABLE Viesti;");
        // heroku käyttää SERIAL-avainsanaa uuden tunnuksen automaattiseen luomiseen
        lista.add("CREATE TABLE Keskustelualue (id_keskustelualue SERIAL PRIMARY KEY, nimi_keskustelualue varchar(200));");
        lista.add("CREATE TABLE Keskustelu (id_keskustelu SERIAL PRIMARY KEY, keskustelualue SERIAL REFERENCES Keskustelualue(id_keskustelualue), nimi_keskustelu);");
        lista.add("CREATE TABLE Viesti (id_viesti SERIAL PRIMARY KEY, keskustelu SERIAL REFERENCES Keskustelu(id_keskustelu), kayttaja varchar(200), runko varchar(1000), viestinaika timestamp);");
        lista.add("INSERT INTO Keskustelualue (nimi_keskustelualue) VALUES ('Tietojenkasittely');");
        lista.add("INSERT INTO Keskustelualue (nimi_keskustelualue) VALUES ('Biologia');");
        lista.add("INSERT INTO Keskustelualue (nimi_keskustelualue) VALUES ('Sosiologia');");
        lista.add("INSERT INTO Keskustelualue (nimi_keskustelualue) VALUES ('Matematiikka');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Tietojenkasittely'), 'Kapistely on kivaa');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Tietojenkasittely'), 'Tikape');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Tietojenkasittely'),'Ohpe');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Tietojenkasittely'),'Ohja');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Tietojenkasittely'),'JTKT');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Biologia'),'Darwin');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Biologia'),'Suomen linnut');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Sosiologia'),'Luennot');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Matematiikka'),'Matriisit');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Matematiikka'),'Inregraalit');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Kapistely on kivaa'),'Juha', 'Koodaaminen on hauskaa', '2017-01-01-00-00-00-00');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Kapistely on kivaa'),'Pekka', 'Nojaa', '2017-01-02-00-00-00-00');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Kapistely on kivaa'),'Juha', 'Miten niin ei?', '2017-01-02-09-00-00-00');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Kapistely on kivaa'),'Pekka', 'Liian vaikeaa', '2017-01-03-00-00-00-00');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Tikape'),'Juha', 'Nää sql-kyselyt saa pään pyörälle', '2017-01-01-00-00-00-00');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Tikape'),'Juha', 'Mutta tekemällä oppii', '2017-01-02-00-00-00-00');");
        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("CREATE TABLE Keskustelualue (id_keskustelualue integer PRIMARY KEY, nimi_keskustelualue varchar(200));");
        lista.add("CREATE TABLE Keskustelu (id_keskustelu integer PRIMARY KEY, keskustelualue integer, nimi_keskustelu varchar(200), FOREIGN KEY(keskustelualue) REFERENCES Keskustelualue(id_keskustelualue));");
        lista.add("CREATE TABLE Viesti (id_viesti integer PRIMARY KEY, keskustelu integer, FOREIGN KEY(keskustelu) REFERENCES Keskustelu(id_keskustelu), kayttaja varchar(200), otsikko varchar (200), runko varchar(1000), viestinaika timestamp);");
        lista.add("INSERT INTO Keskustelualue (nimi_keskustelualue) VALUES ('Tietojenkasittely');");
        lista.add("INSERT INTO Keskustelualue (nimi_keskustelualue) VALUES ('Biologia');");
        lista.add("INSERT INTO Keskustelualue (nimi_keskustelualue) VALUES ('Sosiologia');");
        lista.add("INSERT INTO Keskustelualue (nimi_keskustelualue) VALUES ('Matematiikka');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Tietojenkasittely'), 'Kapistely on kivaa');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Tietojenkasittely'), 'Tikape');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Tietojenkasittely'),'Ohpe');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Tietojenkasittely'),'Ohja');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Tietojenkasittely'),'JTKT');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Biologia'),'Darwin');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Biologia'),'Suomen linnut');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Sosiologia'),'Luennot');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Matematiikka'),'Matriisit');");
        lista.add("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES ((SELECT id_keskustelualue FROM Keskustelualue WHERE nimi = 'Matematiikka'),'Inregraalit');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Kapistely on kivaa'),'Juha', 'Koodaaminen on hauskaa', '2017-01-01-00-00-00-00');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Kapistely on kivaa'),'Pekka', 'Nojaa', '2017-01-02-00-00-00-00');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Kapistely on kivaa'),'Juha', 'Miten niin ei?', '2017-01-02-09-00-00-00');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Kapistely on kivaa'),'Pekka', 'Liian vaikeaa', '2017-01-03-00-00-00-00');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Tikape'),'Juha', 'Nää sql-kyselyt saa pään pyörälle', '2017-01-01-00-00-00-00');");
        lista.add("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES ((SELECT id_keskustelu FROM Keskustelu WHERE nimi = 'Tikape'),'Juha', 'Mutta tekemällä oppii', '2017-01-02-00-00-00-00');");
        return lista;
    }
}
