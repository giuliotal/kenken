package support;

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

    public String toString() {
        return "<"+riga+","+colonna+">";
    }
}
