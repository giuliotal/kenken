package mvc.model;

public class Cella {
    private final int riga;
    private final int colonna;

    public Cella(int riga, int colonna) {
        this.riga = riga;
        this.colonna = colonna;
    }

    public int getRiga() {
        return riga;
    }

    public int getColonna() {
        return colonna;
    }

    @Override
    public String toString() {
        return "<"+riga+","+colonna+">";
    }

    @Override
    public boolean equals(Object o){
        if(o == this) return true;
        if(! (o instanceof Cella)) return false;
        Cella c = (Cella)o;
        return c.riga==this.riga && c.colonna==this.colonna;
    }

    @Override
    public int hashCode() {
        final int M = 17;
        int h = 1;
        h = h * riga + M;
        h = h * colonna + M;
        return h;
    }

}
