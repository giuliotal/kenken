package mvc.model;

import java.util.List;

public interface GridInterface {

    void addGridListener(GridListener l);

    void removeGridListener(GridListener l);

    void createCage(Square[] s, int result, MathOperation op);

    void insertNumber(int number, int row, int column);

    void deleteNumber(int row, int column);

    void setSize(int n);

    int getSize();

    List<Square> findDuplicates();

    List<Grid.Cage> findIncorrectCages();
}
