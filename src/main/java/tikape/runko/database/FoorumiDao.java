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
import tikape.runko.Foorumi;

public class FoorumiDao {

    private Database database;

    public FoorumiDao(Database database) {
        this.database = database;
    }

    // EI VIELÄ TOIMI
    public Viesti findOneViesti(Integer key) throws SQLException {
        return null;

    }

    // Tämä metodi palauttaa kaikki parametrina annettuun keskusteluun kuuluvat viestit.
    public List<Viesti> findAllViesti(int haettavakeskustelu) throws SQLException {

        return null;

    }

    public void lisaaViesti(Integer key) throws SQLException {

    }

    public Keskustelu findOneKeskustelu(Integer key) throws SQLException {
        return null;
    }

    public Keskustelualue findOneKeskustelualue(Integer keskustelualue) throws SQLException {
        return null;

    }

    public List<Keskustelu> findAllAlueenKeskustelut(Foorumi foorumi, String alueenId) throws SQLException {

        try (Connection connection = database.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu, Keskustelualue WHERE Keskustelu.keskustelualue = ? AND Keskustelu.keskustelualue = Keskustelualue.id_keskustelualue");

            ResultSet rs = stmt.executeQuery();
            List<Keskustelu> k = new ArrayList<>();

            while (rs.next()) {
                Integer id = rs.getInt("id_keskustelu");
                String nimi = rs.getString("nimi_keskustelu");
                Keskustelualue keskustelualue = new Keskustelualue(rs.getInt("Keskustelualue.id_keskustelualue"), rs.getString("Keskustelualue.nimi_keskustelualue"));

                // Jos alue oli jo aluelistalla, haetaan oikean alueen viite
                boolean alueLisattiinListalle = foorumi.addAlue(keskustelualue);
                if (!alueLisattiinListalle) {
                    List<Keskustelualue> lista = foorumi.getAluelista();
                    int listanIndex = 0;
                    for (int i = 0; i < lista.size(); i++) {
                        if (lista.get(i).getId() == Integer.parseInt(alueenId)) {
                            listanIndex = i;
                        }
                    }
                    keskustelualue = lista.get(listanIndex);

                }
                Keskustelu lisattavaKeskustelu = new Keskustelu(id, nimi);
                k.add(lisattavaKeskustelu);
                keskustelualue.addKeskustelu(lisattavaKeskustelu);
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
        return null;
    }

    public void lisaaKeskustelu(Integer key) throws SQLException {
        //tekemättä
    }

    public void lisaaKeskustelualue(Integer key) throws SQLException {
        //tekemättä
    }

}
