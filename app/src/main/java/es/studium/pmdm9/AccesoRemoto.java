package es.studium.pmdm9;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccesoRemoto {
    OkHttpClient client = new OkHttpClient();
    private Context context;

    public AccesoRemoto(Context context) {
        this.context = context;
    }

    public JSONArray obtenerListadoPedidos() {
        Request request = new Request.Builder()
                .url("http://" + Constants.SERVER_IP + "/API_PMDM/pedidos.php")
                .build();
        return ejecutarSolicitud(request);
    }

    public JSONArray obtenerListadoTiendas() {
        Request request = new Request.Builder()
                .url("http://" + Constants.SERVER_IP + "/API_PMDM/tiendas.php")
                .build();
        return ejecutarSolicitud(request);
    }

    private JSONArray ejecutarSolicitud(Request request) {
        JSONArray resultado = new JSONArray();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("AccesoRemoto", "Respuesta recibida: " + responseBody);
                resultado = new JSONArray(responseBody);
            } else {
                Log.e("AccesoRemoto", "Error en la respuesta: " + response.code() + " - " + response.message());
                mostrarToast("Error en la respuesta del servidor: " + response.code() + " - " + response.message());
            }
        } catch (IOException | JSONException e) {
            Log.e("AccesoRemoto", "Excepción: " + e.getMessage(), e);
            mostrarToast("Excepción: " + e.getMessage());
        }
        return resultado;
    }

    private void mostrarToast(String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
    }
}
