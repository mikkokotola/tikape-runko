package tikape.runko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.FoorumiDao;
import tikape.runko.domain.Keskustelualue;

public class Main {

    // Mikon testausta koodin muokkaamisesta ja Githubin käytöstä.
    public static void main(String[] args) throws Exception {
        // Portin määritys Herokua varten, lisätty TiKaPe-materiaalin ohjeen mukaan.
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:foorumi.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        } 

        Database db = new Database(jdbcOsoite);

        db.init();

        FoorumiDao foorumiDao = new FoorumiDao(db);
        List<Keskustelualue> aluelista = new ArrayList<>();

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/viestit", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viestit", foorumiDao.findAllViesti());

            return new ModelAndView(map, "opiskelijat");
        }, new ThymeleafTemplateEngine());

        get("/viestit/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", foorumiDao.findOneViesti(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "viesti");
        }, new ThymeleafTemplateEngine());
    }
}
