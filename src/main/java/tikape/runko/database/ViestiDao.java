package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tikape.runko.domain.Keskustelu;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Viesti;

public class ViestiDao implements Dao<Viesti, Integer> {

    private Database database;
    private KeskusteluDao keskusteluDao;

    public ViestiDao(Database database, KeskusteluDao keskusteluDao) {
        this.database = database;
        this.keskusteluDao = keskusteluDao;
    }

    @Override
    public Viesti findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        Integer id = rs.getInt("id_viesti");
// ONGELMIA: tämä keskusteluun ja keskustelualueeseen linkittäminen ei nyt mene oikein (käytetään väärää id:tä).
        Keskustelu keskustelu = new Keskustelu(rs.getInt("id_viesti"), rs.getString("nimi"));
        String kayttaja = rs.getString("kayttaja");
        String runko = rs.getString("runko");
        String viestinaika = "" + rs.getTimestamp("Viesti.viestinaika");

        Viesti v = new Viesti(id, kayttaja, runko, viestinaika);

        rs.close();
        stmt.close();
        connection.close();

        return v;

    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti");
        Map<String, List<Keskustelu>> keskustelunViestit = new HashMap<>();

        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();

        while (rs.next()) {

            Integer id = rs.getInt("id_viesti");

            // KESKEN, miten käsitellään jo olemassa olevat keskustelualueet (samasta hakuerästä)
            Keskustelualue keskustelualue = new Keskustelualue(rs.getInt("id_keskustelualue"), rs.getString("nimi_keskustelualue"));
            Keskustelu keskustelu = new Keskustelu(rs.getInt("id_keskustelu"), rs.getString("nimi_keskustelu"));
            String kayttaja = rs.getString("kayttaja");
            String runko = rs.getString("runko");
            String viestinaika = "" + rs.getTimestamp("viestinaika");

            viestit.add(new Viesti(id, kayttaja, runko, viestinaika));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestit;

    }

    @Override
    public List<Viesti> findAllIn(Collection<Integer> keys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viesti VALUES (?, ?, ?, ?)");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();

        Integer id = rs.getInt("id");
        Keskustelu keskustelu = new Keskustelu(rs.getInt("id"), rs.getString("nimi"));
        String kayttaja = rs.getString("kayttaja");
        String runko = rs.getString("runko");
        String viestinaika = "" + rs.getTimestamp("Viesti.viestinaika");

        Viesti v = new Viesti(id, kayttaja, runko, viestinaika);
        ;

        rs.close();
        stmt.close();
        connection.close();

    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
