
package es.studium.pmdm9;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDate;

public class NuevoPedidoActivity extends AppCompatActivity {

    private EditText editTextFechaEstimada;
    private EditText editTextDescripcion;
    private EditText editTextImporte;
    private Spinner spinnerTiendas;
    private CheckBox checkBoxEstado;
    private Button btnGuardarPedido;
    private JSONArray tiendas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        editTextFechaEstimada = findViewById(R.id.editTextFechaEstimada);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextImporte = findViewById(R.id.editTextImporte);
        spinnerTiendas = findViewById(R.id.spinnerTiendas);
        btnGuardarPedido = findViewById(R.id.btnGuardarPedido);
        LocalDate fecha = LocalDate.now();

        // Cargar tiendas
        AccesoRemoto accesoRemoto = new AccesoRemoto(this);
        tiendas = accesoRemoto.obtenerListadoTiendas();
        cargarTiendasEnSpinner();

        btnGuardarPedido.setOnClickListener(v -> {
            String fechaEstimada = editTextFechaEstimada.getText().toString();
            String descripcion = editTextDescripcion.getText().toString();
            String importeTexto = editTextImporte.getText().toString();
            String fechaHoy = String.valueOf(fecha);
            int idTiendaFK = obtenerIdTiendaSeleccionada();


            if (fechaEstimada.isEmpty() || descripcion.isEmpty() || importeTexto.isEmpty() || idTiendaFK == -1) {
                Toast.makeText(this, "Por favor, complete todos los campos correctamente.", Toast.LENGTH_LONG).show();
                return;
            }

            double importe;
            try {
                importe = Double.parseDouble(importeTexto);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor, ingrese un importe válido.", Toast.LENGTH_LONG).show();
                return;
            }

            AltaRemota altaRemota = new AltaRemota(this);
            String error = altaRemota.darAltaPedido(fechaHoy, fechaEstimada, descripcion, importe, idTiendaFK);

            if (error == null) {
                Toast.makeText(this, "Éxito: Pedido creado exitosamente", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarTiendasEnSpinner() {
        String[] nombresTiendas = new String[tiendas.length()];
        for (int i = 0; i < tiendas.length(); i++) {
            try {
                nombresTiendas[i] = tiendas.getJSONObject(i).getString("nombreTienda");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresTiendas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTiendas.setAdapter(adapter);
    }

    private int obtenerIdTiendaSeleccionada() {
        int posicion = spinnerTiendas.getSelectedItemPosition();
        try {
            return tiendas.getJSONObject(posicion).getInt("idTienda");
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
