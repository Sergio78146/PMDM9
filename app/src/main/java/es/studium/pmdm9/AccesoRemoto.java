package es.studium.pmdm9;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
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
                .url("http://" + Constants.SERVER_IP + "/API/pedidos.php")
                .build();
        return ejecutarSolicitud(request);
    }

    public JSONArray obtenerListadoTiendas() {
        Request request = new Request.Builder()
                .url("http://" + Constants.SERVER_IP + "/API/tiendas.php")
                .build();
        return ejecutarSolicitud(request);
    }

    public static String obtenerTiendaPorId(int idTienda) {
        String nombreTienda = "Tienda Desconocida";
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2/API/Tiendas.php?idTienda=" + idTienda;
        Log.d("AccesoRemoto", "URL: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                String respuesta = response.body().string();
                Log.d("AccesoRemoto", "Respuesta JSON: " + respuesta);
                try {
                    JSONObject tiendaJSON = new JSONObject(respuesta);
                    nombreTienda = tiendaJSON.getString("nombreTienda");
                } catch (JSONException e) {
                    Log.e("AccesoRemoto", "Error al parsear el JSON de la tienda: " + e.getMessage());
                }
            } else {
                Log.e("AccesoRemoto", "Error al obtener el nombre de la tienda: Código " + response.code());
            }
        } catch (IOException e) {
            Log.e("AccesoRemoto", "Error al obtener el nombre de la tienda: " + e.getMessage());
        }
        return nombreTienda;
    }


    private JSONArray ejecutarSolicitud(Request request) {
        JSONArray resultado = new JSONArray();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("AccesoRemoto", "Respuesta recibida: " + responseBody); // Depuración
                if (!responseBody.isEmpty()) {
                    try {
                        resultado = new JSONArray(responseBody);
                    } catch (JSONException e) {
                        Log.e("AccesoRemoto", "Error al parsear JSON: " + e.getMessage());
                        mostrarToast("Error al procesar la respuesta del servidor.");
                    }
                } else {
                    Log.e("AccesoRemoto", "Respuesta vacía");
                    mostrarToast("No se recibieron datos del servidor.");
                }
            } else {
                Log.e("AccesoRemoto", "Error en la respuesta: " + response.code() + " - " + response.message());
                mostrarToast("Error en la respuesta del servidor: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            Log.e("AccesoRemoto", "Excepción: " + e.getMessage(), e);
            mostrarToast("Excepción: " + e.getMessage());
        }
        return resultado;
    }

    private void mostrarToast(String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
    }
}