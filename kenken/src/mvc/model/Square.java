package mvc.model;

import java.io.Serializable;

public class Square implements Serializable {

    private static final long serialVersionUID = -1403355865754859882L;

    private final int row;
    private final int column;
    private int value;

    public Square(int row, int column, int value) {
        this.row = row;
        this.column = column;
        this.value = value;
    }

    public Square(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Square(Square square) {
        this.row = square.row;
        this.column = square.column;
        this.value = square.value;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getValue() { return value; }

    public void setValue(int value) { this.value = value; }

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
