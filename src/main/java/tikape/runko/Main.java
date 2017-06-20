package tikape.runko;

import java.sql.SQLException;
import java.sql.Timestamp;
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

    public static void main(String[] args) throws Exception {

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
                "/alue/:id_keskustelualue", (req, res) -> {
                    HashMap map = new HashMap<>();
                    String id_keskustelualue = req.params("id_keskustelualue");

                    List<Keskustelualue> keskustelualuelista = luoKeskustelut(db);
                    int valittavaAlueIndex = 0;

                    for (int i = 0; i < keskustelualuelista.size(); i++) {
                        if (keskustelualuelista.get(i).getId() == Integer.parseInt(id_keskustelualue)) {
                            valittavaAlueIndex = i;
                        }
                    }

                    List<Keskustelu> alueenKeskustelut = keskustelualuelista.get(valittavaAlueIndex).getKeskustelut();

                    map.put("keskustelut", alueenKeskustelut);
                    map.put("alue", keskustelualuelista.get(valittavaAlueIndex));

                    return new ModelAndView(map, "keskustelualue");
                },
                new ThymeleafTemplateEngine()
        );

        // Haetaan tietyn keskustelun viestit.
        get(
                "/alue/:id_keskustelualue/keskustelu/:id_keskustelu", (req, res) -> {
                    HashMap map = new HashMap<>();
                    String id_keskustelualue = req.params("id_keskustelualue");
                    String id_keskustelu = req.params("id_keskustelu");

                    List<Keskustelualue> keskustelualuelista = luoKeskustelut(db);
                    int valittavaAlueIndex = 0;

                    for (int i = 0; i < keskustelualuelista.size(); i++) {
                        if (keskustelualuelista.get(i).getId() == Integer.parseInt(id_keskustelualue)) {
                            valittavaAlueIndex = i;
                        }
                    }

                    List<Keskustelu> alueenKeskustelut = keskustelualuelista.get(valittavaAlueIndex).getKeskustelut();

                    int valittavaKeskusteluIndex = 0;

                    for (int i = 0; i < alueenKeskustelut.size(); i++) {
                        if (alueenKeskustelut.get(i).getId() == Integer.parseInt(id_keskustelu)) {
                            valittavaKeskusteluIndex = i;
                        }
                    }

                    List<Viesti> keskustelunViestit = alueenKeskustelut.get(valittavaKeskusteluIndex).getViestit();

                    map.put("viestit", keskustelunViestit);
                    map.put("alue", keskustelualuelista.get(valittavaAlueIndex));
                    map.put("keskustelu", alueenKeskustelut.get(valittavaKeskusteluIndex));

                    return new ModelAndView(map, "keskustelu");
                },
                new ThymeleafTemplateEngine()
        );

        // Tämä on vaihtoehtoinen uuden viestin lisäävän postin toteutus, ei käytössä tällä hetkellä.
        post("/alue/:id_keskustelualue/keskustelu/:id_keskustelu/lisaaviesti", (req, res) -> {
            String alue = req.params("id_keskustelualue");
//             keskustelu = req.params(Integer.parseInt("keskustelu"));
            String kayttaja = req.queryParams("kayttaja");
            String runko = req.queryParams("viestinrunko");

            KeskustelualueDao keskustelualueDao = new KeskustelualueDao(db);
            KeskusteluDao keskusteluDao = new KeskusteluDao(db, keskustelualueDao);
            ViestiDao viestiDao = new ViestiDao(db, keskusteluDao);

//            viestiDao.addViesti(keskustelu, kayttaja, runko);
//            res.redirect("/alue/" + alue + "/keskustelu/" + keskustelu);
            return "";
        });

        
        // Uuden alueen lisääminen postilla.
        post("/lisaaalue", (req, res) -> {
            String alue = req.queryParams("alueennimi");

            KeskustelualueDao keskustelualueDao = new KeskustelualueDao(db);
            //KeskusteluDao keskusteluDao = new KeskusteluDao(db, keskustelualueDao);
            //ViestiDao viestiDao = new ViestiDao(db, keskusteluDao);

            keskustelualueDao.addKeskustelualue(alue);
            
            res.redirect("/");
            return "";
        }
        );
        
        // KESKEN
        // Uuden keskustelun lisääminen postilla.
        post("/lisaakeskustelu", (req, res) -> {
            int alue = Integer.parseInt(req.queryParams("alue"));
            String kayttaja = req.queryParams("kayttaja");
            String otsikko = req.queryParams("otsikko");
            String runko = req.queryParams("runko");

            KeskustelualueDao keskustelualueDao = new KeskustelualueDao(db);
            KeskusteluDao keskusteluDao = new KeskusteluDao(db, keskustelualueDao);
        
            keskusteluDao.addKeskustelu(alue, otsikko);
            
            // Vielä pitäisi saada se viesti lisättyä tuolle kyseiselle alueelle. Eli tarvitaan juuri kyseisen alueen id - miten haetaan?
            res.redirect("/alue/" + alue);
            return "";
        }
        );
        
        // Uuden viestin lisääminen postilla.
        post("/lisaaviesti", (req, res) -> {
            int alue = Integer.parseInt(req.queryParams("alue"));
            int keskustelu = Integer.parseInt(req.queryParams("keskustelu"));
            String kayttaja = req.queryParams("kayttaja");
            String runko = req.queryParams("runko");

            KeskustelualueDao keskustelualueDao = new KeskustelualueDao(db);
            KeskusteluDao keskusteluDao = new KeskusteluDao(db, keskustelualueDao);
            ViestiDao viestiDao = new ViestiDao(db, keskusteluDao);

            viestiDao.addViesti(keskustelu, kayttaja, runko);

            res.redirect("/alue/" + alue + "/keskustelu/" + keskustelu);
            return "";
        }
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
                if (k.getKeskustelualue() == ka.getId()) {
                    ka.addKeskustelu(k);
                }
                // Lisätän keskusteluihin niihin kuuluvat viestit
                for (Viesti v : kaikkiViestit) {
                    if (v.getKeskustelu() == k.getId()) {
                        k.addViesti(v);
                    }

                }
            }
        }
        return keskustelualuelista;
    }

}
