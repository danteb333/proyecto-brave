import java.util.HashSet;
import java.util.LinkedList;

public class Historial {

    private final LinkedList<String> entradas;

    public Historial(){
        this.entradas = new LinkedList<>();
    }

    public void agregarVisita(String url, String titulo){
        //1. NO guarda URLS duplicadas consecutivas
        if(!entradas.isEmpty() && entradas.getFirst().equals(titulo+" - "+url)){
            return;
        }

        //2. Agregamos la nueva visita al inicio de la lista
        entradas.addFirst(titulo+" - "+url);

        //3. Limita el historial a las ultimas 10 entradas
        if(entradas.size() > 10){
            entradas.removeLast();
        }
    }
    public LinkedList<String> getRegistros(){
        return entradas;
    }
    public void limpiarHistorial(){
        entradas.clear();
    }

    //favoritos
    private final HashSet<String> favoritos = new HashSet<>();

    public boolean esFavorito(String url) {
        return favoritos.contains(url);
    }

    public void alternarFavorito(String url) {
        if (favoritos.contains(url)) {
            favoritos.remove(url);
        } else {
            favoritos.add(url);
        }
    }

    public LinkedList<String> getEntradas() {
        return entradas;
    }
}

