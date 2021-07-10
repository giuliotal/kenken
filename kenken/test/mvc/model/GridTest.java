package mvc.model;

import mvc.model.exceptions.SolutionsNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class GridTest {

    private static final int TIMEOUT = 2000;
    private Grid grid;

    @Before
    public void beforeTest() {
        grid = new Grid();
    }

    @Test(timeout = TIMEOUT)
    public void getDuplicateSquaresEmptyGrid() {
        assertNull("Empty grid, duplicate numbers: ", grid.getDuplicateSquares());
    }

    @Test(timeout = TIMEOUT)
    public void getInvalidTargetResultCagesEmptyGrid() {
        assertNull("Constraint check on empty grid must return null", grid.getInvalidTargetResultCages());
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void setIllegalSize() {
        int size = -1;
        grid.setSize(size);
    }

    @Test(timeout = TIMEOUT)
    public void getSize() {
        int size = 6;
        grid.setSize(size);
        assertEquals("Grid size",size,grid.getSize());
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void insertIllegalNumber() {
        grid.setSize(3);
        grid.insertNumber(5,-1,0);
    }

    @Test(timeout = TIMEOUT)
    public void deleteNumber() {
        grid.setSize(9);
        grid.insertNumber(2,0,0);
        grid.deleteNumber(0,0);
        assertEquals("Deleting number",0,grid.getGrid()[0][0].getValue());
    }

    @Test(timeout = TIMEOUT)
    public void clear() {
        grid.setSize(3);
        defineGrid();
        grid.clear();
        assertEquals("Grid cleared: ",0,grid.getGrid()[0][0].getValue());
    }

    @Test(timeout = TIMEOUT)
    public void checkConstraints() {
        grid.setSize(3);
        // creo un blocco vi inserisco dei valori che non rispettano i vincoli del giooc
        defineGrid();
        assertFalse("Invalid grid",grid.checkConstraints());

    }

    @Test(timeout = TIMEOUT)
    public void findSingleSolution() throws SolutionsNotFoundException {
        // carico una griglia di gioco 6x6 di difficoltà media che possiede un'unica soluzione
        grid.load("test/resources/game6x6.kenken");
        grid.findSolutions(100);
        assertEquals(1,grid.getTotalSolutions());
    }

    @Test(timeout = TIMEOUT, expected = SolutionsNotFoundException.class)
    public void findSolutionForUnsolvableGame() throws SolutionsNotFoundException {
        // carico una griglia di gioco che non ha soluzioni
        grid.load("test/resources/unsolvableGame.kenken");
        grid.findSolutions(100);
    }

    @Test(timeout = TIMEOUT)
    public void saveAndLoadGrid() {
        // inizializzo e popolo una griglia di gioco
        grid.setSize(3);
        Square s1 = new Square(0,0);
        Square s2 = new Square(0,1);
        Square s3 = new Square(0,2);
        Square[] squares = {s1,s2,s3};
        grid.createCage(squares,100,MathOperation.SUM);
        grid.insertNumber(1,0,0);
        grid.insertNumber(1,0,1);
        grid.insertNumber(1,0,2);
        // salvo la griglia di gioco
        grid.save("/tmp/saveTest.kenken");
        // ripulisco la griglia di gioco
        grid.clear();
        // ripristino la griglia di gioco precedentemente salvata
        grid.load("/tmp/saveTest.kenken");
        // verifico la presenza del blocco precedentemente inserito
        assertEquals("Loaded cage size",squares.length,grid.getCageSchema().get(0).getSize());
    }

    @Test(timeout = TIMEOUT)
    public void save() {
        assertFalse("Saving to an illegal file format", grid.load("/home/file.txt"));
    }

    @Test(timeout = TIMEOUT)
    public void load() {
        assertFalse("Loading an illegal file format", grid.load("/home/file.txt"));
    }

    @Test(timeout = TIMEOUT)
    public void createCageAliasing() {
        grid.setSize(3);
        Square s1 = new Square(0,0);
        Square s2 = new Square(0,1);
        Square s3 = new Square(0,2);
        Square[] squares = {s1,s2,s3};
        int result = 3;
        MathOperation operation = MathOperation.SUM;
        grid.createCage(squares,result,operation);
        Square[][] currentGrid = grid.getGrid();
        // verifico l'aliasing tra le celle della griglia di gioco e le celle del blocco
        for(Cage c : grid.getCageSchema()){
            for(Square s : c.getSquares()) {
                assertSame("Square in Cage references square in Grid",currentGrid[s.getRow()][s.getColumn()], s);
            }
        }
    }

    private void defineGrid() {
        Square s1 = new Square(0,0);
        Square s2 = new Square(0,1);
        Square s3 = new Square(0,2);
        Square[] squares = {s1,s2,s3};
        grid.createCage(squares,100,MathOperation.SUM);
        grid.insertNumber(1,0,0);
        grid.insertNumber(1,0,1);
        grid.insertNumber(1,0,2);
    }
}