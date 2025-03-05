package es.studium.pmdm9;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModificacionRemota {
    private OkHttpClient client = new OkHttpClient();
    private Context context;

    public ModificacionRemota(Context context) {
        this.context = context;
    }

    public boolean modificarPedido(int idPedido, String fechaPedido, String fechaEstimada, String descripcionPedido, double importePedido, int estadoPedido, int idTienda) {
        String responseBody = ejecutarSolicitud("pedidos.php",
                "idPedido", String.valueOf(idPedido),
                "fechaPedido", fechaPedido,
                "fechaEstimadaPedido", fechaEstimada,
                "descripcionPedido", descripcionPedido,
                "importePedido", String.valueOf(importePedido),
                "estadoPedido", String.valueOf(estadoPedido),
                "idTiendaFK", String.valueOf(idTienda));

        if (responseBody != null) {
            mostrarLogsServidor("Respuesta del Servidor (Pedido)", responseBody);
            return true;
        } else {
            return false;
        }
    }

    public boolean modificarTienda(int idTienda, String nombreTienda) {
        String responseBody = ejecutarSolicitud("tiendas.php",
                "idTienda", String.valueOf(idTienda),
                "nombreTienda", nombreTienda);

        if (responseBody != null) {
            mostrarLogsServidor("Respuesta del Servidor (Tienda)", responseBody);
            return true;
        } else {
            return false;
        }
    }

    private String ejecutarSolicitud(String endpoint, String... params) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (int i = 0; i < params.length; i += 2) {
            formBodyBuilder.add(params[i], params[i + 1]);
        }
        RequestBody formBody = formBodyBuilder.build();

        Request request = new Request.Builder()
                .url("http://" + Constants.SERVER_IP + "/API_PMDM/" + endpoint)
                .put(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                if (responseBody.isEmpty() || responseBody.contains("error")) {
                    String errorMessage = "Error en la respuesta del servidor: " + response.code() + " - " + responseBody;
                    Log.e("ModificacionRemota", errorMessage);
                    mostrarDialogoError(errorMessage);
                    return null;
                }
                Log.d("ModificacionRemota", "Respuesta del servidor: " + responseBody);
                return responseBody;
            } else {
                String errorMessage = "Error en la respuesta del servidor: " + response.code() + " - " + response.message();
                Log.e("ModificacionRemota", errorMessage);
                mostrarDialogoError(errorMessage);
                return null;
            }
        } catch (IOException e) {
            String errorMessage = "Error de conexión: " + e.getMessage();
            Log.e("ModificacionRemota", errorMessage);
            mostrarDialogoError(errorMessage);
            return null;
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            String errorMessage = "Error inesperado: " + e.getMessage();
            Log.e("ModificacionRemota", errorMessage);
            mostrarDialogoError(errorMessage);
            return null;
        }
    }

    private void mostrarDialogoError(String mensaje) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void mostrarLogsServidor(String titulo, String logs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo);

        // Create a TextView to display the logs
        TextView textView = new TextView(context);
        textView.setText(logs);
        textView.setTextSize(14);
        textView.setPadding(20, 20, 20, 20);

        // Use a ScrollView to allow scrolling if the logs are long
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(textView);

        builder.setView(scrollView);

        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
