package es.studium.pmdm9;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class ModificacionRemota {
    private OkHttpClient client = new OkHttpClient();
    private Context context;

    public ModificacionRemota(Context context) {
        this.context = context;
    }

    public boolean modificarPedido(int idPedido, String fechaPedido, String fechaEstimada, String descripcionPedido, double importePedido, int estadoPedido, int idTienda) {
        String responseBody = ejecutarSolicitud("Pedidos.php",
                "idPedido", String.valueOf(idPedido),
                "fechaPedido", fechaPedido,
                "fechaEstimadaPedido", fechaEstimada,
                "descripcionPedido", descripcionPedido,
                "importePedido", String.valueOf(importePedido),
                "estadoPedido", String.valueOf(estadoPedido),
                "idTiendaFK", String.valueOf(idTienda));

        if (responseBody != null) {

            if (responseBody.contains("error")) {
                mostrarToast("Error al modificar el pedido. Verifique los datos.");
                return false;
            } else {
                mostrarToast("Pedido modificado correctamente.");
                return true;
            }
        } else {
            mostrarToast("No se recibió respuesta del servidor.");
            return false;
        }
    }

    public boolean modificarTienda(int idTienda, String nombreTienda) {
        String responseBody = ejecutarSolicitud("Tiendas.php",
                "idTienda", String.valueOf(idTienda),
                "nombreTienda", nombreTienda);

        if (responseBody != null) {
            if (responseBody.contains("error")) {
                mostrarToast("Error al modificar la tienda. Verifique los datos.");
                return false;
            } else {
                mostrarToast("Tienda modificada correctamente.");
                return true;
            }
        } else {
            mostrarToast("No se recibió respuesta del servidor.");
            return false;
        }
    }

    private String ejecutarSolicitud(String endpoint, String... params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + Constants.SERVER_IP + "/API/" + endpoint).newBuilder();
        for (int i = 0; i < params.length; i += 2) {
            urlBuilder.addQueryParameter(params[i], params[i + 1]);
        }
        String url = urlBuilder.build().toString();

        Log.d("ModificacionRemota", "URL construida: " + url);

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(null, new byte[0]))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("ModificacionRemota", "Respuesta del servidor: " + responseBody);
                return responseBody;
            } else {
                mostrarToast("Error en la respuesta del servidor: " + response.code() + " - " + response.message());
                return null;
            }
        } catch (IOException e) {
            mostrarToast("Error de conexión: " + e.getMessage());
            return null;
        }
    }

    private void mostrarToast(String mensaje) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).runOnUiThread(() ->
                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show());
        }
    }

    private void mostrarLogsServidor(String titulo, String logs) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(titulo);

                TextView textView = new TextView(context);
                textView.setText(logs);
                textView.setTextSize(14);
                textView.setPadding(20, 20, 20, 20);

                ScrollView scrollView = new ScrollView(context);
                scrollView.addView(textView);

                builder.setView(scrollView);
                builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
    }
}
