package tikape.runko;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
//import tikape.runko.database.FoorumiDao;
import tikape.runko.database.KeskusteluDao;
import tikape.runko.database.KeskustelualueDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Keskustelu;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Viesti;

public class Main {

    // Siirretty foorumin "pääkoodi" erilliseen luokkaan Foorumi.
    public static void main(String[] args) throws Exception {

        Foorumi foorumi = new Foorumi();

// Portin määritys Herokua varten, lisätty TiKaPe-materiaalin ohjeen mukaan.
        if (System.getenv(
                "PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }

        // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:foorumi.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön

        if (System.getenv(
                "DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        }

        Database db = new Database(jdbcOsoite);

        db.init();

        //FoorumiDao foorumiDao = new FoorumiDao(db);
        //testausta
        //List<Integer> lista = new ArrayList<>();
        //lista.add(1);
        //lista.add(2);
        //System.out.println(keskustelualueDao.findAllIn(lista));
        //List<Keskustelualue> aluelista = keskustelualueDao.findAll();
//        for (Keskustelualue x : aluelista) {
//            System.out.println(x);
//           
//        }
//        List<Viesti> list = viestiDao.findAll(); 
//        for (Viesti x : list) {
//            System.out.println(x);
//        }
        // Haetaan kaikki keskustelualueet.
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            List<Keskustelualue> keskustelualuelista = luoKeskustelut(db);

            map.put("alueet", keskustelualuelista);

            return new ModelAndView(map, "index");
        },
                new ThymeleafTemplateEngine()
        );

        // Haetaan tietyn keskustelualueen keskustelut.
        get(
                "/:id_keskustelualue", (req, res) -> {
                    HashMap map = new HashMap<>();
                    String id_keskustelualue = req.queryParams("id_keskustelualue");

                    List<Keskustelualue> keskustelualuelista = luoKeskustelut(db);
                    int valittavaAlueIndex = 0;

                    for (int i = 0; i < keskustelualuelista.size(); i++) {
                        if (keskustelualuelista.get(i).getId() == Integer.parseInt(id_keskustelualue)) {
                            valittavaAlueIndex = i;
                        }
                    }

                    List<Keskustelu> alueenKeskustelut = keskustelualuelista.get(valittavaAlueIndex).getKeskustelut();

                    map.put("keskustelut", alueenKeskustelut);
                    map.put("alue", id_keskustelualue);

                    return new ModelAndView(map, "keskustelualue");
                },
                new ThymeleafTemplateEngine()
        );

        // KESKEN, haetaan tietyn keskustelun viestit.
        get(
                "/:id_keskustelualue/:id_keskustelu", (req, res) -> {
                    HashMap map = new HashMap<>();
                    String id_keskustelualue = req.queryParams("id_keskustelualue");
                    String id_keskustelu = req.queryParams("id_keskustelu");
                    map.put("viestit", foorumi.getAluelista().get(0).getKeskustelut().get(0).getViestit()); // KESKEN, muutettava dao-metodiksi.
                    map.put("alue", id_keskustelualue);
                    map.put("keskustelu", id_keskustelu);

                    return new ModelAndView(map, "keskustelu");
                },
                new ThymeleafTemplateEngine()
        );

        // KESKEN, haetaan tietty viesti.
        get(
                "/:id_keskustelualue/:id_keskustelu/:id_viesti", (req, res) -> {
                    HashMap map = new HashMap<>();
                    // 
                    String id_keskustelualue = req.queryParams("id_keskustelualue");
                    String id_keskustelu = req.queryParams("id_keskustelu");
                    map.put("viesti", "TESTIVIESTI"); // KESKEN.
                    map.put("alue", id_keskustelualue);
                    map.put("keskustelu", id_keskustelu);
                    return new ModelAndView(map, "viesti");
                },
                new ThymeleafTemplateEngine()
        );

    }

    public static List<Keskustelualue> luoKeskustelut(Database db) throws SQLException {

        KeskustelualueDao keskustelualueDao = new KeskustelualueDao(db);
        KeskusteluDao keskusteluDao = new KeskusteluDao(db, keskustelualueDao);
        ViestiDao viestiDao = new ViestiDao(db, keskusteluDao);

        // Haetaan keskustelualueet
        List<Keskustelualue> keskustelualuelista = keskustelualueDao.findAll();
        // Lisätään alueille niille kuuluvat keskustelut
        List<Keskustelu> kaikkiKeskustelut = keskusteluDao.findAll();
        List<Viesti> kaikkiViestit = viestiDao.findAll();
        for (Keskustelualue ka : keskustelualuelista) {
            for (Keskustelu k : kaikkiKeskustelut) {
                if (k.getKeskustelualue().getId() == ka.getId()) {
                    ka.addKeskustelu(k);
                }
                // Lisätän keskusteluihin niihin kuuluvat viestit
                for (Viesti v : kaikkiViestit) {
                    if (v.getKeskustelu().getId() == k.getId()) {
                        k.addViesti(v);
                    }

                }
            }
        }
        return keskustelualuelista;
    }

}
