package mvc.model;

import mvc.model.exceptions.SolutionsNotFoundException;

import java.util.Collections;
import java.util.List;

public interface GridInterface {

    void addGridListener(GridListener l);

    void removeGridListener(GridListener l);

    boolean createCage(Square[] squares, int result, MathOperation op);

    void insertNumber(int number, int row, int column);

    void deleteNumber(int row, int column);

    void clear();

    int getSize();

    void setSize(int n);

    List<Square> getDuplicateSquares();

    List<Cage> getInvalidTargetResultCages();

    boolean checkConstraints();

    void findSolutions(int maxSolutions) throws SolutionsNotFoundException;

    int getTotalSolutions();

    int[][] getCurrentSolution() throws SolutionsNotFoundException;

    void nextSolution();

    void previousSolution();

    boolean hasNextSolution();

    boolean hasPreviousSolution();

    boolean save(String pathName);

    boolean load(String pathName);

    Memento getMemento();

    void setMemento(Memento m);
}
