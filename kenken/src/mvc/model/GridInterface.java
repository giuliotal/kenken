package mvc.model;

public interface GridInterface {

    void addGridListener(GridListener l);

    void removeGridListener(GridListener l);

    boolean verifyAdjacency(boolean[][] selectedSquares);

    //TODO da sistemare: un metodo nell'interfaccia non può dipendere dalla classe concreta!
    boolean createCage(boolean[][] squares, int result, Grid.MathOperation op);

    void insertNumber(int number, int row, int column);

    void deleteNumber(int row, int column);

    void clear();

    void setSize(int n);

    int getSize();

    boolean checkConstraints();

    void findSolutions(int maxSolutions);

    int[][] getCurrentSolution();

    void nextSolution();

    void previousSolution();

    boolean hasNextSolution();

    boolean hasPreviousSolution();

    boolean save(String pathName);

    boolean load(String pathName);

    Memento getMemento();

    void setMemento(Memento m);
}
