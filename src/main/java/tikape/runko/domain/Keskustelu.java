package tikape.runko.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Keskustelu {

    private int id_keskustelu;
    private int keskustelualue;
    private String nimi_keskustelu;
    private List<Viesti> viestit;

    public Keskustelu(int id, int keskustelualue, String nimi) {

        this.id_keskustelu = id;
        this.keskustelualue = keskustelualue;
        this.nimi_keskustelu = nimi;
        this.viestit = new ArrayList<>();
    }

    public String getNimi() {
        return nimi_keskustelu;
    }

    public void setNimi(String nimi) {
        this.nimi_keskustelu = nimi;
    }

    public int getId() {
        return id_keskustelu;
    }

    public void setId(int id) {
        this.id_keskustelu = id;
    }

    public int getKeskustelualue() {
        return keskustelualue;
    }

    public void setKeskustelualue(int keskustelualue) {
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

    public int getViestien_lkm() {
        return this.viestit.size();
    }

    public String getViimeisimmanViestinAika() {
        if (!this.viestit.isEmpty()) {
            Timestamp viimeisin = this.viestit.get(0).getViestinaika();

            for (Viesti v : this.viestit) {
                if (v.getViestinaika().after(viimeisin)) {
                    viimeisin = v.getViestinaika();
                }
            }

            return viimeisin.toString();
        } else {
            return "";
        }
    }
}
