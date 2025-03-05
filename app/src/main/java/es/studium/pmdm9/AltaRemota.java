package es.studium.pmdm9;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AltaRemota {
    OkHttpClient client = new OkHttpClient();
    private Context context;

    public AltaRemota(Context context) {
        this.context = context;
    }

    public String darAltaPedido(String fechaEstimada, String descripcionPedido, double importePedido, int idTienda, int estadoPedido) {
        return ejecutarSolicitud("pedidos.php",
                "descripcionPedido", descripcionPedido,
                "fechaEstimadaPedido", fechaEstimada,
                "importePedido", String.valueOf(importePedido),
                "idTiendaFK", String.valueOf(idTienda),
                "estadoPedido", String.valueOf(estadoPedido));
    }

    public String darAltaTienda(String nombreTienda) {
        return ejecutarSolicitud("tiendas.php", "nombreTienda", nombreTienda);
    }

    private String ejecutarSolicitud(String endpoint, String... params) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (int i = 0; i < params.length; i += 2) {
            formBodyBuilder.add(params[i], params[i + 1]);
        }
        RequestBody formBody = formBodyBuilder.build();

        Request request = new Request.Builder()
                .url("http://" + Constants.SERVER_IP + "/API_PMDM/" + endpoint)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Check if the response body is empty or contains an error message
                String responseBody = response.body().string();
                if (responseBody.isEmpty() || responseBody.contains("error")) {
                    String errorMessage = "Error en la respuesta del servidor: " + response.code() + " - " + responseBody;
                    Log.e("AltaRemota", errorMessage);
                    mostrarDialogoError(errorMessage);
                    return errorMessage;
                }
                return null; // No error
            } else {
                String errorMessage = "Error en la respuesta del servidor: " + response.code() + " - " + response.message();
                Log.e("AltaRemota", errorMessage);
                mostrarDialogoError(errorMessage);
                return errorMessage;
            }
        } catch (IOException e) {
            String errorMessage = "Error de conexión: " + e.getMessage();
            Log.e("AltaRemota", errorMessage);
            mostrarDialogoError(errorMessage);
            return errorMessage;
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            String errorMessage = "Error inesperado: " + e.getMessage();
            Log.e("AltaRemota", errorMessage);
            mostrarDialogoError(errorMessage);
            return errorMessage;
        }
    }

    private void mostrarDialogoError(String mensaje) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }


}