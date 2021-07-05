package mvc.model;

import java.util.List;

public interface GridInterface {

    void addGridListener(GridListener l);

    void removeGridListener(GridListener l);

    boolean verifyAdjacency();

    boolean isSelectionEmpty();

    void createCage(int result, MathOperation op);

    void insertNumber(int number, int row, int column);

    void deleteNumber(int row, int column);

    void clear();

    void setSize(int n);

    int getSize();

    boolean[][] getSelectedSquares();

    void selectSquare(int i, int j);

    List<Square> findDuplicates();

    List<Grid.Cage> findIncorrectCages();

    void checkConstraints();

    void findSolutions(int maxSolutions);

    int[][] getCurrentSolution();

    void nextSolution();

    void previousSolution();

    boolean hasNextSolution();

    boolean hasPreviousSolution();

    boolean save(String pathName);

    boolean load(String pathName);
}
