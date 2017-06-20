package tikape.runko.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Keskustelualue {

    private int id_keskustelualue;
    private String nimi_keskustelualue;
    private List<Keskustelu> keskustelut;
//    private int viestien_lkm;
//    private String viimeisimmanViestinAika;

    public Keskustelualue(int id, String nimi) {

        this.id_keskustelualue = id;
        this.nimi_keskustelualue = nimi;
        this.keskustelut = new ArrayList<>();

//        this.viestien_lkm = 0;
//        this.viimeisimmanViestinAika = "";
    }

    public int getId() {
        return id_keskustelualue;
    }

    public void setId(int id) {
        this.id_keskustelualue = id;
    }

    public String getNimi() {
        return nimi_keskustelualue;
    }

    public void setNimi(String nimi) {
        this.nimi_keskustelualue = nimi;
    }

    public List<Keskustelu> getKeskustelut() {
        return keskustelut;
    }

    public void setKeskustelut(List<Keskustelu> keskustelut) {
        this.keskustelut = keskustelut;
    }

    // Metodi lisää keskustelun listalle vain jos keskustelu ei ole jo listalla. Palauttaa true jos keskustelu lisättiin ja false jos keskustelu oli jo listalla ja sitä ei lisätty.
    public boolean addKeskustelu(Keskustelu lisattavaKeskustelu) {

        // Tarkastetaan onko lisättävä keskustelu jo listalla.
        boolean keskusteluOnJoListassa = false;
        for (Keskustelu keskustelu : this.keskustelut) {
            if (keskustelu.getId() == lisattavaKeskustelu.getId()) {
                keskusteluOnJoListassa = true;
            }
        }

        // Jos keskustelu ei ollut jo listalla, lisätään se. Jos keskustelu oli jo listalla, ei lisätä.
        if (!keskusteluOnJoListassa) {
            this.keskustelut.add(lisattavaKeskustelu);
            return true;
        } else {
            return false;
        }
    }

    public int viestienLkm() {

        int alueenViestienLkm = 0;
        for (Keskustelu k : this.keskustelut) {
            alueenViestienLkm += k.getViestit().size();
        }

        return alueenViestienLkm;
    }

    public String viimeisimmanViestinAika() {
        if (!this.keskustelut.isEmpty()) {
            if (!this.keskustelut.get(0).getViestit().isEmpty()) {
                Timestamp viimeisin = this.keskustelut.get(0).getViestit().get(0).getViestinaika();
                for (Keskustelu k : this.keskustelut) {
                    for (Viesti v : k.getViestit()) {
                        if (v.getViestinaika().after(viimeisin)) {
                            viimeisin = v.getViestinaika();
                        }
                    }
                }

                return viimeisin.toString();
            } else {
                return "";
            }
        } else {
            return "";
        }

    }

    public int getId_keskustelualue() {
        return id_keskustelualue;
    }

    public void setId_keskustelualue(int id_keskustelualue) {
        this.id_keskustelualue = id_keskustelualue;
    }

    public String getNimi_keskustelualue() {
        return nimi_keskustelualue;
    }

    public void setNimi_keskustelualue(String nimi_keskustelualue) {
        this.nimi_keskustelualue = nimi_keskustelualue;
    }

    public int getViestien_lkm() {
        return viestienLkm();
    }

//    public void setViestien_lkm(int viestien_lkm) {
//        this.viestien_lkm = viestien_lkm;
//    }
    public String getViimeisimmanViestinAika() {
        return viimeisimmanViestinAika();
    }

//    public void setViimeisimmanViestinAika(String viimeisimmanViestinAika) {
//        this.viimeisimmanViestinAika = viimeisimmanViestinAika;
//    }
}
