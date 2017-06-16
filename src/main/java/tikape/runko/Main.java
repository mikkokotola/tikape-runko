package tikape.runko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.FoorumiDao;
import tikape.runko.database.KeskusteluDao;
import tikape.runko.database.KeskustelualueDao;
import tikape.runko.database.ViestiDao;
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

        FoorumiDao foorumiDao = new FoorumiDao(db);
        KeskustelualueDao keskustelualueDao = new KeskustelualueDao(db);
        KeskusteluDao keskusteluDao = new KeskusteluDao(db, keskustelualueDao);
        ViestiDao viestiDao = new ViestiDao(db, keskusteluDao);
        
        //testausta
        List<Viesti> vlista = viestiDao.findAll();
        
        for (Viesti x : vlista) {
            System.out.println(x);
        }
        
        // Haetaan kaikki keskustelualueet.
        get(
                "/", (req, res) -> {
                    HashMap map = new HashMap<>();
                    List<Keskustelualue> lista = keskustelualueDao.findAll(); // EI TOIMI jostain syystä. Kadottaa tuon alustuksessa luodun aluelistan. Tämä pitää joka tapauksessa vaihtaa SQL-kyselyyn findAll
                    map.put("alueet", lista);

                    return new ModelAndView(map, "index");
                },
                new ThymeleafTemplateEngine()
        );

        // Haetaan tietyn keskustelualueen keskustelut.
        get(
                "/:id_keskustelualue", (req, res) -> {
                    HashMap map = new HashMap<>();
                    String id_keskustelualue = req.queryParams("id_keskustelualue");
                    map.put("keskustelut", foorumiDao.findAllAlueenKeskustelut(foorumi, id_keskustelualue));
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
                    map.put("viesti", foorumiDao.findOneViesti(Integer.parseInt(req.params("id_viesti")))); // KESKEN, muutettava dao-metodiksi.
                    map.put("alue", id_keskustelualue);
                    map.put("keskustelu", id_keskustelu);
                    return new ModelAndView(map, "viesti");
                },
                new ThymeleafTemplateEngine()
        );

    }
}
