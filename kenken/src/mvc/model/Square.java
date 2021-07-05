package mvc.model;

import java.io.Serializable;

public class Square implements Serializable {
    private final int row;
    private final int column;

    public Square(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "<"+ row +","+ column +">";
    }

    @Override
    public boolean equals(Object o){
        if(o == this) return true;
        if(! (o instanceof Square)) return false;
        Square c = (Square)o;
        return c.row ==this.row && c.column ==this.column;
    }

    @Override
    public int hashCode() {
        final int M = 17;
        int h = 1;
        h = h * row + M;
        h = h * column + M;
        return h;
    }

}
