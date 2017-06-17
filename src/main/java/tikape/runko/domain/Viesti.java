
package tikape.runko.domain;

import java.sql.Timestamp;

public class Viesti {
    
    private int id_viesti;
    private Keskustelu keskustelu;
    private String kayttaja;
    private String runko;
    private Timestamp viestinaika;
    

    public Viesti(int id, String kayttaja, String runko, Timestamp viestinaika) {
        
        this.id_viesti = id;
        this.kayttaja=kayttaja;
        this.runko=runko;
        this.viestinaika=viestinaika;
        
    }

    public int getId() {
        return id_viesti;
    }

    public void setId(int id) {
        this.id_viesti = id;
    }

    public Keskustelu getKeskustelu() {
        return keskustelu;
    }

    public void setKeskustelu(Keskustelu keskustelu) {
        this.keskustelu = keskustelu;
    }

    public String getKayttaja() {
        return kayttaja;
    }

    public void setKayttaja(String kayttaja) {
        this.kayttaja = kayttaja;
    }

//    public String getOtsikko() {
//        return otsikko;
//    }
//
//    public void setOtsikko(String otsikko) {
//        this.otsikko = otsikko;
//    }

    public String getRunko() {
        return runko;
    }

    public void setRunko(String runko) {
        this.runko = runko;
    }

    public Timestamp getViestinaika() {
        return viestinaika;
    }

    public void setViestinaika(Timestamp viestinaika) {
        this.viestinaika = viestinaika;
    }

    
}
