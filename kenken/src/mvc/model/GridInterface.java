package mvc.model;

import java.util.List;

public interface GridInterface {

    void addGridListener(GridListener l);

    void removeGridListener(GridListener l);

    void creaBlocco(Cella[] celle, int risultato, MathOperation operazione);

    void inserisciNumero(int numero, int riga, int colonna);

    void eliminaNumero(int riga, int colonna);

    List<Cella> verificaDuplicati();

    List<GridObject.Blocco> verificaBlocchi();
}
