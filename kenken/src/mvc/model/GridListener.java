package mvc.model;

//Interfaccia Observer
public interface GridListener {
    // TODO attenzione: potrebbe non essere necessario il GridEvent in quanto non ci sono informazioni da estrapolare dalla griglia
    // (vedi differenza tra GraphicObjectPanel e GraphicObjectLogger)
    void gridChanged(GridEvent e);
}
