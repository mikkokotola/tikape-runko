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
            String nimi = rs.getString("nimi_keskustelu");

            Keskustelu k = new Keskustelu(id, nimi);

            keskustelut.add(k);

            int keskustelualue = rs.getInt("keskustelualue");
            if (!keskustelunkeskustelualue.containsKey(keskustelualue)) {
                keskustelunkeskustelualue.put(keskustelualue, new ArrayList<>());
            }
            keskustelunkeskustelualue.get(keskustelualue).add(k);

        }
        rs.close();
        stmt.close();
        connection.close();
        for (Keskustelualue kealue : this.keskustelualueDao.findAll()) {
            if (!keskustelunkeskustelualue.containsKey(kealue.getId())) {
                continue;
            }

            for (Keskustelu keskustelu : keskustelunkeskustelualue.get(kealue.getId())) {
                keskustelu.setKeskustelualue(kealue);
            }
        }
        return keskustelut;
    }

    @Override
    public List findAllIn(Collection keys) throws SQLException {
        return null;
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
        String nimi = rs.getString("nimi_keskustelu");
        
        Keskustelu k = new Keskustelu(id, nimi);
        
        Integer alue = rs.getInt("Keskustelualue");

        rs.close();
        stmt.close();
        connection.close();
        k.setKeskustelualue(this.keskustelualueDao.findOne(alue));

        return k;

    }

    @Override
    public void add(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelu VALUES (?, ?, ?)");
        stmt.setInt(1, key);
        stmt.setObject(2, "keskustelualue");
        stmt.setString(3, "nimi_keskustelualue");

        stmt.close();
        connection.close();
        

    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
