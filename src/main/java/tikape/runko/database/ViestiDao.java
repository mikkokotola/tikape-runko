package tikape.runko.database;

import static java.lang.String.valueOf;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import static java.sql.Timestamp.valueOf;
import static java.sql.Timestamp.valueOf;
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
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id_viesti = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        Integer id = rs.getInt("id_viesti");
        int keskustelu = rs.getInt("keskustelu");
        String kayttaja = rs.getString("kayttaja");
        String runko = rs.getString("runko");
        Timestamp viestinaika = rs.getTimestamp("viestinaika");

        Viesti v = new Viesti(id, keskustelu, kayttaja, runko, viestinaika);

        

        rs.close();
        stmt.close();
        connection.close();
        

        return v;

    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti");
        ResultSet rs = stmt.executeQuery();

        Map<Integer, List<Viesti>> keskustelunViestit = new HashMap<>();

        List<Viesti> viestit = new ArrayList<>();

        while (rs.next()) {

            Integer id = rs.getInt("id_viesti");
            int keskustelu = rs.getInt("keskustelu");
            String kayttaja = rs.getString("kayttaja");
            String runko = rs.getString("runko");
            Timestamp viestinaika = Timestamp.valueOf(rs.getString("viestinaika"));
            Viesti v = new Viesti(id, keskustelu, kayttaja, runko, viestinaika);
            viestit.add(v);
            
            if (!keskustelunViestit.containsKey(keskustelu)) {
                keskustelunViestit.put(keskustelu, new ArrayList<>());
            }
            keskustelunViestit.get(keskustelu).add(v);
        }

        rs.close();
        stmt.close();
        connection.close();
        for (Keskustelu ke : this.keskusteluDao.findAllIn(keskustelunViestit.keySet())) {
            for (Viesti viesti : keskustelunViestit.get(ke.getId())) {
                viesti.setKeskustelu(ke.getId());
            }

        }

        return viestit;

    }

    @Override
    public List<Viesti> findAllIn(Collection<Integer> keys) throws SQLException {
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            muuttujat.append(", ?");
        }

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id_viesti IN (" + muuttujat + ")");
        int laskuri = 1;
        for (Integer key : keys) {
            stmt.setObject(laskuri, key);
            laskuri++;
        }

        ResultSet rs = stmt.executeQuery();
        Map<Integer, List<Viesti>> viestinkeskustelu = new HashMap<>();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id_viesti");
            String kayttaja = rs.getString("kayttaja");
            String runko = rs.getString("runko");
            int keskustelu = rs.getInt("keskustelu");
            Timestamp viestinaika = rs.getTimestamp("viestinaika");
            Viesti v = new Viesti(id, keskustelu, kayttaja, runko, viestinaika);
            viestit.add(v);

            if (!viestinkeskustelu.containsKey(keskustelu)) {
                viestinkeskustelu.put(keskustelu, new ArrayList<>());
            }
            viestinkeskustelu.get(keskustelu).add(v);

        }
        rs.close();
        stmt.close();
        connection.close();
        for (Keskustelu ke : this.keskusteluDao.findAllIn(viestinkeskustelu.keySet())) {
            for (Viesti viesti : viestinkeskustelu.get(ke.getId())) {
                viesti.setKeskustelu(ke.getId());
            }

        }

        return viestit;
    }


    
    public void addViesti(int keskustelu, String kayttaja, String runko) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viesti (keskustelu, kayttaja, runko, viestinaika) VALUES (?, ?, ?, datetime())");
        stmt.setInt(1, keskustelu );
        stmt.setString(2, kayttaja);
        stmt.setString(3, runko);
//        stmt.setObject(4, "");

        stmt.execute();
        
        stmt.close();
        connection.close();

    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
