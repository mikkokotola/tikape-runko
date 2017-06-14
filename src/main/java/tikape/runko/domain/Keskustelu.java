package tikape.runko.domain;

import java.util.ArrayList;
import java.util.List;

public class Keskustelu {

    private int id;
    private Keskustelualue keskustelualue;
    private String nimi;
    private List<Viesti> viestit;

    public Keskustelu(int id, Keskustelualue k, String nimi) {

        this.id = id;
        this.keskustelualue = k;
        this.nimi = nimi;
        this.viestit = new ArrayList<>();
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Keskustelualue getKeskustelualue() {
        return keskustelualue;
    }

    public void setKeskustelualue(Keskustelualue keskustelualue) {
        this.keskustelualue = keskustelualue;
    }

    public List<Viesti> getViestit() {
        return viestit;
    }

    public void setViestit(List<Viesti> viestit) {
        this.viestit = viestit;
    }

    // Metodi lisää viestin listalle vain jos viesti ei ole jo listalla. Palauttaa true jos viesti lisättiin ja false jos viesti oli jo listalla ja sitä ei lisätty.
    public boolean addViesti(Viesti lisattavaViesti) {

        // Tarkastetaan onko lisättävä keskustelu jo listalla.
        boolean viestiOnJoListassa = false;
        for (Viesti viesti : this.viestit) {
            if (viesti.getId() == lisattavaViesti.getId()) {
                viestiOnJoListassa = true;
            }
        }

        // Jos viesti ei ollut jo listalla, lisätään se. Jos viesti oli jo listalla, ei lisätä. 
        if (!viestiOnJoListassa) {
            this.viestit.add(lisattavaViesti);
            return true;
        } else {
            return false;
        }
    }
}
