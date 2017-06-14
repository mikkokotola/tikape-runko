package tikape.runko;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.database.Database;
import tikape.runko.database.FoorumiDao;
import tikape.runko.domain.Keskustelu;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Viesti;

public class Foorumi {

    private List<Keskustelualue> aluelista;

    public Foorumi() {
        this.aluelista = new ArrayList<>();

        List<Keskustelualue> aluelista = new ArrayList<>();
        // TESTIDATAA
        //Keskustelualue alueJaakiekko = new Keskustelualue(1, "Jääkiekko");
        //Keskustelualue alueJääpallo = new Keskustelualue(2, "Jääpallo");
        //Keskustelualue alueJalkapallo = new Keskustelualue(3, "Jalkapallo"); 

        //aluelista.add(alueJaakiekko);
        //aluelista.add(alueJääpallo);
        //aluelista.add(alueJalkapallo);

        //Keskustelu jaakiekkoOnParas = new Keskustelu(1, alueJaakiekko, "Jääkiekko on paras.");
        //Keskustelu jaakiekkoOnHuonoin = new Keskustelu (2, alueJääkiekko, "Jääkiekko on huonoin.");

        //alueJaakiekko.addKeskustelu(jaakiekkoOnParas);
        //alueJääkiekko.addKeskustelu(jaakiekkoOnHuonoin);

        //Viesti latkaHyva1 = new Viesti(1, jaakiekkoOnParas, "Ville", "Jääkiekko todellakin on paras.", new Timestamp(2017, 06, 14, 23, 45, 00, 00));
        //Viesti latkaHyva2 = new Viesti(2, jaakiekkoOnParas, "Repe", "Jääkiekko on NIIN paras.", new Timestamp(2017, 06, 14, 23, 46, 00, 00));

        //jaakiekkoOnParas.addViesti(latkaHyva1);
        //jaakiekkoOnParas.addViesti(latkaHyva2);
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
}
