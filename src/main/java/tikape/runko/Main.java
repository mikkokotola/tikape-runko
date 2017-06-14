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

    // Siirretty foorumin "pääkoodi" erilliseen luokkaan Foorumi.
    public static void main(String[] args) throws Exception {
    
        Foorumi foorumi = new Foorumi();
        foorumi.run();
    }
}
