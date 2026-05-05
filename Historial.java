import java.util.LinkedList;

public class Historial {

    // Clase interna para guardar (url,titulo)

    private LinkedList<String> entradas;
    private int limite_historial = 10;

    public Historial(){
        this.entradas = new LinkedList<>();
    }

    public void agregarVisita(String url, String titulo){
        //1. NO guarda URLS duplicadas consecutivas
        if(!entradas.isEmpty() && entradas.getFirst().equals(url+" - "+url)){
            return;
        }

        //2. Agregamos la nueva visita al inicio de la lista
        entradas.addFirst(titulo+" - "+url);

        //3. Limita el historial a las ultimas 10 entradas
        if(entradas.size() > limite_historial){
            entradas.removeLast();
        }
    }
    public LinkedList<String> getEntradas(){
        return entradas;
    }
    public void limpiarHistorial(){
        entradas.clear();
    }
}

