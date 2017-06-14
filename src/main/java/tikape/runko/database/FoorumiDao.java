
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import tikape.runko.domain.*;

public class FoorumiDao {

    private Database database;

    public FoorumiDao(Database database) {
        this.database = database;
    }

    // EI VIELÄ TOIMI
    public Viesti findOneViesti(Integer key) throws SQLException {
        try (Connection connection = database.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?");
            stmt.setInt(1, key);

            ResultSet rs = stmt.executeQuery();

            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }
            Integer id = rs.getInt("id_viesti");
// ONGELMIA: tämä keskusteluun ja keskustelualueeseen linkittäminen ei nyt mene oikein (käytetään väärää id:tä).
            Keskustelu keskustelu = new Keskustelu(rs.getInt("id_viesti"), new Keskustelualue(rs.getInt("id_viesti"), rs.getString("nimi")), rs.getString("nimi"));
            String kayttaja = rs.getString("kayttaja");
// Otsikko poistettu turhana.
//            String otsikko = rs.getString("otsikko");
            String runko = rs.getString("runko");
            Timestamp viestinaika = rs.getTimestamp("viestinaika");

            Viesti v = new Viesti(id, keskustelu, kayttaja, runko, viestinaika);

            rs.close();
            stmt.close();
            connection.close();

            return v;
        } catch (Exception e) {
            return null;
        }

    }

    // Tämä metodi palauttaa kaikki parametrina annettuun keskusteluun kuuluvat viestit.
    public List<Viesti> findAllViesti(Keskustelu haettavakeskustelu) throws SQLException {

        try (Connection connection = database.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti, Keskustelu, Keskustelualue WHERE Viesti.keskustelu = ? AND Viesti.keskustelu = Keskustelu.id_keskustelu AND Keskustelu.keskustelualue = Keskustelualue.id_keskustelualue");

            stmt.setInt(1, haettavakeskustelu.getId());
            
            ResultSet rs = stmt.executeQuery();
            List<Viesti> viestit = new ArrayList<>();
            while (rs.next()) {
                Integer id = rs.getInt("Viesti.id_viesti");
                if ()
                Keskustelualue keskustelualue = new Keskustelualue(rs.getInt("Keskustelualue.id_keskustelualue"), rs.getString("Keskustelualue.nimi"))
                Keskustelu keskustelu = new Keskustelu(rs.getInt("Keskustelu.id_keskustelu"), keskustelualue, rs.getString("Keskustelu.nimi"));
                String kayttaja = rs.getString("Viesti.kayttaja");
//                String otsikko = rs.getString("otsikko");
                String runko = rs.getString("Viesti.runko");
                Timestamp viestinaika = rs.getTimestamp("Viesti.viestinaika");

                viestit.add(new Viesti(id, keskustelu, kayttaja, runko, viestinaika));
            }

            rs.close();
            stmt.close();
            connection.close();

            return viestit;
        } catch (Exception e) {
            return null;
        }
    }

    //Viestin lisääminen on kesken, enkä ole ihan täysin varma, miten se tulisi toteuttaa.
    public void lisaaViesti(Integer key) throws SQLException {
        try (Connection connection = database.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viesti VALUES (?, ?, ?, ?)");
            stmt.setInt(1, key);

            ResultSet rs = stmt.executeQuery();

            Integer id = rs.getInt("id");
            Keskustelu keskustelu = new Keskustelu(rs.getInt("id"), new Keskustelualue(rs.getInt("id"), rs.getString("nimi")), rs.getString("nimi"));
            String kayttaja = rs.getString("kayttaja");
            String runko = rs.getString("runko");
            Timestamp viestinaika = rs.getTimestamp("viestinaika");

            Viesti v = new Viesti(id, keskustelu, kayttaja, runko, viestinaika);
            ;

            rs.close();
            stmt.close();
            connection.close();

        } catch (Exception e) {

        }
    }

    public Keskustelu findOneKeskustelu(Integer key) throws SQLException {
        try (Connection connection = database.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?");
            stmt.setInt(1, key);

            ResultSet rs = stmt.executeQuery();

            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }
            Integer id = rs.getInt("id");
            String nimi = rs.getString("kayttaja");
            Keskustelualue keskustelualue = new Keskustelualue(rs.getInt("id"), rs.getString("nimi"));

            Keskustelu k = new Keskustelu(id, keskustelualue, nimi);

            rs.close();
            stmt.close();
            connection.close();

            return k;
        } catch (Exception e) {
            return null;
        }

    }

    public Keskustelualue findOneKeskustelualue(Integer key) throws SQLException {
        try (Connection connection = database.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?");
            stmt.setInt(1, key);

            ResultSet rs = stmt.executeQuery();

            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            Keskustelualue k = new Keskustelualue(id, nimi);

            rs.close();
            stmt.close();
            connection.close();

            return k;
        } catch (Exception e) {
            return null;
        }

    }

    public List<Keskustelu> findAllKeskustelu() throws SQLException {

        try (Connection connection = database.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Opiskelija");

            ResultSet rs = stmt.executeQuery();
            List<Keskustelu> k = new ArrayList<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String nimi = rs.getString("kayttaja");
                Keskustelualue keskustelualue = new Keskustelualue(rs.getInt("id"), rs.getString("nimi"));

                k.add(new Keskustelu(id, keskustelualue, nimi));
            }

            rs.close();
            stmt.close();
            connection.close();

            return k;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Keskustelualue> findAllKeskustelualue() throws SQLException {

        try (Connection connection = database.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Opiskelija");

            ResultSet rs = stmt.executeQuery();
            List<Keskustelualue> keskustelualueet = new ArrayList<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String nimi = rs.getString("nimi");

                keskustelualueet.add(new Keskustelualue(id, nimi));
            }

            rs.close();
            stmt.close();
            connection.close();

            return keskustelualueet;
        } catch (Exception e) {
            return null;
        }
    }

    public void lisaaKeskustelu(Integer key) throws SQLException {
        //tekemättä
    }

    public void lisaaKeskustelualue(Integer key) throws SQLException {
        //tekemättä
    }

}

