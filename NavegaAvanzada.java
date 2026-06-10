import java.net.URL;
import java.util.Stack;

import javax.swing.JButton;


public class NavegaAvanzada {


    private Historial historial;

    private final Stack<String> pilaAtras;
    private final Stack<String> pilaAdelante;
    private String urlActual;

    //botones

    private final JButton btnAtras;
    private final JButton btnAdelante;

    public NavegaAvanzada(JButton btnAtras, JButton btnAdelante) {
        this.btnAtras = btnAtras;
        this.btnAdelante = btnAdelante;
        this.pilaAtras = new Stack<>();
        this.pilaAdelante = new Stack<>();

        actualizarBotones();
    }

    public void registrarVisita(String nuevaUrl){

        for (String comparar: historial.getEntradas()){
                //se evitan duplicados si se va a un link con la misma pagina
            if (urlActual != null && !urlActual.equals(nuevaUrl) && !urlActual.equals(comparar)) {
                pilaAtras.push(nuevaUrl);
                pilaAdelante.clear();
            }
        }
        urlActual = nuevaUrl;
        actualizarBotones();
    }

    public String irAtras(){

        if (!pilaAtras.isEmpty()) {
            pilaAdelante.push(urlActual);  //guardamos la actual en el futuro
            urlActual = pilaAtras.pop(); // sacamos la anterior del pasado
            actualizarBotones();
            return urlActual;
        }
        return null; // No hay página atras
    }

    public String irAdelante(){
        if (!pilaAdelante.isEmpty()) {
            pilaAtras.push(urlActual); //guardamos al actual en el pasado
            urlActual = pilaAdelante.pop(); // sacamos la siguiente del futuro
            return urlActual;
        }
        return null;
    }

    private void actualizarBotones() {
        btnAtras.setEnabled(!pilaAtras.isEmpty());
        btnAdelante.setEnabled(!pilaAdelante.isEmpty());
    }

}
