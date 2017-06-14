package tikape.runko;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.get;
import static spark.Spark.port;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.FoorumiDao;
import tikape.runko.domain.Keskustelu;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Viesti;

public class Foorumi {

    private List<Keskustelualue> aluelista;

    public Foorumi() {
        this.aluelista = new ArrayList<>();
    }

    public List<Keskustelualue> getAluelista() {
        return aluelista;
    }

    public void setAluelista(List<Keskustelualue> aluelista) {
        this.aluelista = aluelista;
    }

    // Metodi lisää alueen listalle vain jos alue ei ole jo listalla. Palauttaa true jos alue lisättiin ja false jos alue oli jo listalla ja sitä ei lisätty.
    public boolean addAlue(Keskustelualue lisattavaAlue) {

        // Tarkastetaan onko lisättävä alue jo listalla.
        boolean alueOnJoListassa = false;
        for (Keskustelualue alue : this.aluelista) {
            if (alue.getId() == lisattavaAlue.getId()) {
                alueOnJoListassa = true;
            }
        }

        // Jos alue ei ollut jo listalla, lisätään se. Jos alue oli jo listalla, ei lisätä.
        if (!alueOnJoListassa) {
            this.aluelista.add(lisattavaAlue);
            return true;
        } else {
            return false;
        }
    }

    public void run() throws ClassNotFoundException {
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
        List<Keskustelualue> aluelista = new ArrayList<>();
        // TESTIDATAA
        Keskustelualue alueJääkiekko = new Keskustelualue(1, "Jääkiekko");
        //Keskustelualue alueJääpallo = new Keskustelualue(2, "Jääpallo");
        //Keskustelualue alueJalkapallo = new Keskustelualue(3, "Jalkapallo"); 

        aluelista.add(alueJääkiekko);
        //aluelista.add(alueJääpallo);
        //aluelista.add(alueJalkapallo);
        
        Keskustelu jaakiekkoOnParas = new Keskustelu (1, alueJääkiekko, "Jääkiekko on paras.");
        //Keskustelu jaakiekkoOnHuonoin = new Keskustelu (2, alueJääkiekko, "Jääkiekko on huonoin.");
                
        alueJääkiekko.addKeskustelu(jaakiekkoOnParas);
        //alueJääkiekko.addKeskustelu(jaakiekkoOnHuonoin);

        Viesti latkaHyva1 = new Viesti (1, jaakiekkoOnParas, "Ville", "Jääkiekko todellakin on paras.", new Timestamp(2017,06,14,23,45,00,00));
        Viesti latkaHyva2 = new Viesti (2, jaakiekkoOnParas, "Repe", "Jääkiekko on NIIN paras.", new Timestamp(2017,06,14,23,46,00,00));
        
        jaakiekkoOnParas.addViesti(latkaHyva1);
        jaakiekkoOnParas.addViesti(latkaHyva2);
        
        // KESKEN, haetaan kaikki keskustelualueet.
        get(
                "/", (req, res) -> {
                    HashMap map = new HashMap<>();
                    map.put("viesti", this.aluelista); // KESKEN, muutettava dao-metodiksi. Vai tarvitseeko noita tallentaa ollenkaan?

                    return new ModelAndView(map, "index");
                },
                new ThymeleafTemplateEngine()
        );

        // KESKEN, haetaan tietyn keskustelualueen keskustelut.
        get(
                "/:id_keskustelualue", (req, res) -> {
                    HashMap map = new HashMap<>();
                    String id_keskustelualue = req.queryParams("id_keskustelualue");
                    map.put("keskustelut", this.aluelista.get(0).getKeskustelut()); // KESKEN, muutettava dao-metodiksi.

                    return new ModelAndView(map, "keskustelualue");
                },
                new ThymeleafTemplateEngine()
        );

        // KESKEN, haetaan tietyn keskustelun viestit.
        get(
                "/:id_keskustelualue/:id_keskustelu", (req, res) -> {
                    HashMap map = new HashMap<>();
                    map.put("viestit", this.aluelista.get(0).getKeskustelut().get(0).getViestit()); // KESKEN, muutettava dao-metodiksi.

                    return new ModelAndView(map, "keskustelu");
                },
                new ThymeleafTemplateEngine()
        );

        // KESKEN, haetaan tietty viesti.
        get(
                "/:id_keskustelualue/:id_keskustelu/:id_viesti", (req, res) -> {
                    HashMap map = new HashMap<>();
                    // 
                    map.put("viesti", foorumiDao.findOneViesti(Integer.parseInt(req.params("id")))); // KESKEN, muutettava dao-metodiksi.

                    return new ModelAndView(map, "viesti");
                },
                new ThymeleafTemplateEngine()
        );
    }
}
