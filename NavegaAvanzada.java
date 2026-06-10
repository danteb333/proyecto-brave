import java.util.Stack;
import javax.swing.JButton;

public class NavegaAvanzada {
    private final Stack<String> pilaAtras;
    private final Stack<String> pilaAdelante;
    private String urlActual;

    // Referencias a los botones visuales para encenderlos/apagarlos
    private final JButton btnAtras;
    private final JButton btnAdelante;

    public NavegaAvanzada(JButton btnAtras, JButton btnAdelante) {
        this.pilaAtras = new Stack<>();
        this.pilaAdelante = new Stack<>();
        this.btnAtras = btnAtras;
        this.btnAdelante = btnAdelante;

        // Cumple la regla: Al iniciar pestaña los botones deben estar deshabilitados
        actualizarBotones();
    }

    // Se llama cada vez que el usuario escribe una URL o hace clic en un enlace normal
    public void registrarVisitaNueva(String nuevaUrl) {
        // Evita duplicados si hace clic en un enlace que lleva a la misma página
        if (urlActual != null && !urlActual.equals(nuevaUrl)) {
            pilaAtras.push(urlActual);
            pilaAdelante.clear(); // Al tomar una nueva ruta, el "futuro" anterior se borra
        }
        urlActual = nuevaUrl;
        actualizarBotones();
    }

    public String irAtras() {
        if (!pilaAtras.isEmpty()) {
            pilaAdelante.push(urlActual); // Guardamos la actual en el futuro
            urlActual = pilaAtras.pop();  // Sacamos la anterior del pasado
            actualizarBotones();
            return urlActual;
        }
        return null;
    }

    public String irAdelante() {
        if (!pilaAdelante.isEmpty()) {
            pilaAtras.push(urlActual);    // Guardamos la actual en el pasado
            urlActual = pilaAdelante.pop();// Sacamos la siguiente del futuro
            actualizarBotones();
            return urlActual;
        }
        return null;
    }

    private void actualizarBotones() {
        btnAtras.setEnabled(!pilaAtras.isEmpty());
        btnAdelante.setEnabled(!pilaAdelante.isEmpty());
    }
}