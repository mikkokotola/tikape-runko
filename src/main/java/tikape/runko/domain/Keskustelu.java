
package tikape.runko.domain;

public class Keskustelu {
    
    private int id;
    private Keskustelualue keskustelualue;
    private String nimi;
    
    public Keskustelu(int id, Keskustelualue k, String nimi){
        
        this.id=id;
        this.keskustelualue=k;
        this.nimi = nimi;
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
    
}
