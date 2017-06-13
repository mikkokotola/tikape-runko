
package tikape.runko.domain;

public class Keskustelu {
    
    private int id;
    private Keskustelualue keskustelualue;
    
    public Keskustelu(int id, Keskustelualue k){
        
        this.id=id;
        this.keskustelualue=k;
        
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
