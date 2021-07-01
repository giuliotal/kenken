package mvc.model;

public class GridEvent {

    private final Grid source;

    public GridEvent(Grid source) {
        this.source = source;
    }

    public Grid getSource() {return source;}
}
