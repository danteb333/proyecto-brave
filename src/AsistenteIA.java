import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AsistenteIA {
    private static final String API_KEY = "";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    public static String callGeminiAPI(String prompt) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        /*connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);
         */

        String requestBody = String.format(
                "{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}",
                escapeJson(prompt)
        );

        //envio de peticion
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        //respuesta
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return extractTextFromResponse(response.toString());
            }
        } else {
            throw new RuntimeException("Error en la API: " + responseCode);
        }
    }

    //limpia JSON
    private static String escapeJson(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Extrae el texto de la respuesta JSON de Gemini
    private static String extractTextFromResponse(String jsonResponse) {
        String marker = "\"text\": \"";
        int start = jsonResponse.indexOf(marker);
        if (start == -1) return "No se pudo extraer la respuesta.";
        start += marker.length();

        StringBuilder result = new StringBuilder();
        int i = start;
        while (i < jsonResponse.length()) {
            char c = jsonResponse.charAt(i);
            if (c == '\\' && i + 1 < jsonResponse.length()) {
                char next = jsonResponse.charAt(i + 1);
                switch (next) {
                    case '"'  -> { result.append('"');  i += 2; }
                    case 'n'  -> { result.append('\n'); i += 2; }
                    case 'r'  -> { result.append('\r'); i += 2; }
                    case 't'  -> { result.append('\t'); i += 2; }
                    case '\\' -> { result.append('\\'); i += 2; }
                    default   -> { result.append(c); i++; }
                }
            } else if (c == '"') {
                break;
            } else {
                result.append(c);
                i++;
            }
        }
        return result.toString();
    }
}