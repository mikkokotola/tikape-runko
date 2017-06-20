package tikape.runko;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
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

            // Järjestetään lista aakkosjärjestykseen.
            Collections.sort(keskustelualuelista, (o1, o2) -> o1.getNimi().compareTo(o2.getNimi()));

            map.put("alueet", keskustelualuelista);

            return new ModelAndView(map, "index");
        },
                new ThymeleafTemplateEngine()
        );

        // Haetaan tietyn keskustelualueen keskustelut, näytetään vain kymmenen viimeksi aktiivista.
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

                    // Järjestetään käänteiseen aikajärjestykseen (uusimmasta vanhimpaan) keskustelun uusimman viestin aikaleiman perusteella.
                    Collections.sort(alueenKeskustelut, (o2, o1) -> o1.getViimeisimmanViestinAika().compareTo(o2.getViimeisimmanViestinAika()));

                    // Poimitaan näytettäväksi kymmenen viimeisintä.
                    List<Keskustelu> alueenKeskustelutViimeisimmat10 = new ArrayList<>();
                    for (int i = 0; i < alueenKeskustelut.size(); i++) {
                        if (i < 10) {
                            alueenKeskustelutViimeisimmat10.add(alueenKeskustelut.get(i));
                        }
                    }

                    map.put("keskustelut", alueenKeskustelutViimeisimmat10);
                    map.put("alue", keskustelualuelista.get(valittavaAlueIndex));

                    return new ModelAndView(map, "keskustelualue");
                },
                new ThymeleafTemplateEngine()
        );

        // Haetaan tietyn keskustelualueen keskustelut, näytetään kaikki
        get(
                "/alue/:id_keskustelualue/all", (req, res) -> {
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

                    // Järjestetään käänteiseen aikajärjestykseen (uusimmasta vanhimpaan) keskustelun uusimman viestin aikaleiman perusteella.
                    Collections.sort(alueenKeskustelut, (o2, o1) -> o1.getViimeisimmanViestinAika().compareTo(o2.getViimeisimmanViestinAika()));

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

                    // Tarkastetaan onko näytettäviä rajoittavaa sivua tarkoittavaa parametria sivu.
                    int sivu = -1;
                    if (req.queryParams().contains("sivu")) {
                        sivu = Integer.parseInt(req.queryParams("sivu"));

                    }

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
                    List<Viesti> naytettavatViestit = new ArrayList<>();
                    int aloitusnro = 1;

                    if (sivu != -1) {
                        int sivujenLkm = keskustelunViestit.size() / 20;
                        if (keskustelunViestit.size() % 20 > 0) {
                            sivujenLkm++;
                        }

                        for (int i = (sivu - 1) * 20 + 1; i <= Math.min((sivu * 20), keskustelunViestit.size()); i++) {
                            naytettavatViestit.add(keskustelunViestit.get(i - 1));
                        }

                        aloitusnro = ((sivu - 1) * 20) + 1;
                    } else {
                        naytettavatViestit = keskustelunViestit;
                    }

                    int sivujenLkm = ((keskustelunViestit.size() - 1) / 20) + 1;
                    List<Integer> sivunumerot = new ArrayList<>();
                    for (int i = 1; i <= sivujenLkm; i++) {
                        sivunumerot.add(i);

                    }

                    map.put("viestit", naytettavatViestit);
                    map.put("alue", keskustelualuelista.get(valittavaAlueIndex));
                    map.put("keskustelu", alueenKeskustelut.get(valittavaKeskusteluIndex));
                    map.put("aloitusnro", aloitusnro);
                    map.put("sivunumerot", sivunumerot);

                    return new ModelAndView(map, "keskustelu");
                },
                new ThymeleafTemplateEngine()
        );

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

        // Uuden keskustelun lisääminen postilla.
        post("/lisaakeskustelu", (req, res) -> {
            int alue = Integer.parseInt(req.queryParams("alue"));
            String kayttaja = req.queryParams("kayttaja");
            String otsikko = req.queryParams("otsikko");
            String runko = req.queryParams("runko");

            KeskustelualueDao keskustelualueDao = new KeskustelualueDao(db);
            KeskusteluDao keskusteluDao = new KeskusteluDao(db, keskustelualueDao);
            ViestiDao viestiDao = new ViestiDao(db, keskusteluDao);

            keskusteluDao.addKeskustelu(alue, otsikko);
            List<Keskustelu> keskustelulista = keskusteluDao.findAll();

            // Etsitään juuri lisätyn keskustelun id.
            int keskustelunId = 999;
            for (int i = 0; i < keskustelulista.size(); i++) {
                if (keskustelulista.get(i).getNimi().equals(otsikko)) {
                    keskustelunId = keskustelulista.get(i).getId();
                }
            }

            // Lisätään viesti juuri lisättyyn keskusteluun.
            viestiDao.addViesti(keskustelunId, kayttaja, runko);

            res.redirect("/alue/" + alue + "/keskustelu/" + keskustelunId + "?sivu=1");
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
