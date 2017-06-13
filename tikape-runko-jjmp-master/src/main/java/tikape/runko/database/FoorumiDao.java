/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.*;

public class FoorumiDao {

    private Database database;

    public FoorumiDao(Database database) {
        this.database = database;
    }

    
    public Viesti findOneViesti(Integer key) throws SQLException {
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

            Viesti v = new Viesti(id, nimi);

            rs.close();
            stmt.close();
            connection.close();

            return v;
        } catch (Exception e) {
            return null;
        }

    }

    
    public List<Opiskelija> findAll() throws SQLException {

        try (Connection connection = database.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Opiskelija");

            ResultSet rs = stmt.executeQuery();
            List<Opiskelija> opiskelijat = new ArrayList<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String nimi = rs.getString("nimi");

                opiskelijat.add(new Opiskelija(id, nimi));
            }

            rs.close();
            stmt.close();
            connection.close();

            return opiskelijat;
        } catch (Exception e) {
            return null;
        }
    }

    
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }

}
