package tikape.runko.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import tikape.runko.domain.Keskustelu;
import java.util.*;
import java.sql.*;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Viesti;

public class KeskusteluDao implements Dao<Keskustelu, Integer> {

    private Database database;
    private KeskustelualueDao keskustelualueDao;

    public KeskusteluDao(Database database, KeskustelualueDao keskustelualueDao) {
        this.database = database;
        this.keskustelualueDao = keskustelualueDao;
    }

    @Override
    public List<Keskustelu> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu");

        ResultSet rs = stmt.executeQuery();

        Map<Integer, List<Keskustelu>> keskustelunkeskustelualue = new HashMap<>();

        List<Keskustelu> keskustelut = new ArrayList<>();

        while (rs.next()) {
            Integer id = rs.getInt("id_keskustelu");
            int keskustelualue = rs.getInt("keskustelualue");
            String nimi = rs.getString("nimi_keskustelu");

            Keskustelu k = new Keskustelu(id, keskustelualue, nimi);

            keskustelut.add(k);

            
            if (!keskustelunkeskustelualue.containsKey(keskustelualue)) {
                keskustelunkeskustelualue.put(keskustelualue, new ArrayList<>());
            }
            keskustelunkeskustelualue.get(keskustelualue).add(k);

        }
        rs.close();
        stmt.close();
        connection.close();
        for (Keskustelualue kealue : this.keskustelualueDao.findAllIn(keskustelunkeskustelualue.keySet())) {
            for (Keskustelu keskustelu : keskustelunkeskustelualue.get(kealue.getId())) {
                keskustelu.setKeskustelualue(kealue.getId());
            }

        }
        return keskustelut;
    }

    @Override
    public List<Keskustelu> findAllIn(Collection<Integer> keys) throws SQLException {
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            muuttujat.append(", ?");
        }

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE id_keskustelu IN (" + muuttujat + ")");
        int laskuri = 1;
        for (Integer key : keys) {
            stmt.setObject(laskuri, key);
            laskuri++;
        }

        ResultSet rs = stmt.executeQuery();
        Map<Integer, List<Keskustelu>> keskustelunkeskustelualue = new HashMap<>();
        List<Keskustelu> keskustelut = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id_keskustelu");
            int keskustelualue = rs.getInt("keskustelualue");
            String nimi = rs.getString("nimi_keskustelu");
            Keskustelu k = new Keskustelu(id, keskustelualue, nimi);

            keskustelut.add(k);
            
            if (!keskustelunkeskustelualue.containsKey(keskustelualue)) {
                keskustelunkeskustelualue.put(keskustelualue, new ArrayList<>());
            }
            keskustelunkeskustelualue.get(keskustelualue).add(k);

        }
        rs.close();
        stmt.close();
        connection.close();
        for (Keskustelualue kealue : this.keskustelualueDao.findAllIn(keskustelunkeskustelualue.keySet())) {
            for (Keskustelu keskustelu : keskustelunkeskustelualue.get(kealue.getId())) {
                keskustelu.setKeskustelualue(kealue.getId());
            }

        }

        return keskustelut;
    }

    @Override
    public Keskustelu findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE id_keskustelu = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        Integer id = rs.getInt("id_keskustelu");
        Integer alue = rs.getInt("keskustelualue");
        String nimi = rs.getString("nimi_keskustelu");

        Keskustelu k = new Keskustelu(id, alue, nimi);

        

        rs.close();
        stmt.close();
        connection.close();
        

        return k;

    }

    
    public void addKeskustelu(int alue, String nimi) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelu (keskustelualue, nimi_keskustelu) VALUES (?, ?)");
        stmt.setInt(1, alue);
        stmt.setString(2, nimi);
        stmt.execute();
        stmt.close();
        connection.close();

    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
