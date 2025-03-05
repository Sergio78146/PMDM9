package es.studium.pmdm9;

import android.util.Log;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BajaRemota {
    OkHttpClient client = new OkHttpClient();

    public boolean darBajaPedido(int idPedido) {
        return ejecutarSolicitud("pedidos.php", "idPedido", String.valueOf(idPedido));
    }

    public boolean darBajaTienda(int idTienda) {
        return ejecutarSolicitud("tiendas.php", "idTienda", String.valueOf(idTienda));
    }

    private boolean ejecutarSolicitud(String endpoint, String idParam, String idValue) {
        Request request = new Request.Builder()
                .url("http://" + Constants.SERVER_IP + "/API_PMDM/" + endpoint + "?" + idParam + "=" + idValue)
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
