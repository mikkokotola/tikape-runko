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
    public List findAll() throws SQLException {
        return null;
    }

    @Override
    public List findAllIn(Collection keys) throws SQLException {
        return null;
    }

    @Override
    public Keskustelu findOne(Integer key) throws SQLException {
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

    @Override
    public void add(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
