package es.studium.pmdm9;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class BajaRemota {
    OkHttpClient client = new OkHttpClient();

    public boolean darBajaPedido(int idPedido) {
        return ejecutarSolicitud("Pedidos.php", "idPedido", String.valueOf(idPedido));
    }

    public boolean darBajaTienda(int idTienda) {
        return ejecutarSolicitud("Tiendas.php", "idTienda", String.valueOf(idTienda));
    }

    private boolean ejecutarSolicitud(String endpoint, String idParam, String idValue) {
        Request request = new Request.Builder()
                .url("http://" + Constants.SERVER_IP + "/API/" + endpoint + "?" + idParam + "=" + idValue)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            Log.e("BajaRemota", "Error al eliminar: " + e.getMessage());
            return false;
        }
    }
}
