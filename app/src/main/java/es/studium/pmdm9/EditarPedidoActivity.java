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
import java.util.Locale;


public class EditarPedidoActivity extends AppCompatActivity {

    private EditText editTextFechaPedido;
    private EditText editTextFechaEstimada;
    private EditText editTextDescripcion;
    private EditText editTextImporte;
    private Spinner spinnerTiendas;
    private CheckBox checkBoxEstado;
    private Button btnGuardarCambios;
    private int idPedido;
    private JSONArray tiendas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_pedido);

        editTextFechaPedido = findViewById(R.id.editTextFechaPedido);
        editTextFechaEstimada = findViewById(R.id.editTextFechaEstimada);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextImporte = findViewById(R.id.editTextImporte);
        spinnerTiendas = findViewById(R.id.spinnerTiendas);
        checkBoxEstado = findViewById(R.id.checkBoxEstado);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        // Obtain order data to edit
        idPedido = getIntent().getIntExtra("idPedido", -1);
        String fechaPedido = getIntent().getStringExtra("fechaPedido");
        String fechaEstimada = getIntent().getStringExtra("fechaEstimada");
        String descripcion = getIntent().getStringExtra("descripcion");
        double importe = getIntent().getDoubleExtra("importe", 0.0); // Obtener el importe como double
        boolean estado = getIntent().getBooleanExtra("estado", false);

        // Display data in the input fields
        editTextFechaPedido.setText(fechaPedido);
        editTextFechaEstimada.setText(fechaEstimada);
        editTextDescripcion.setText(descripcion);
        editTextImporte.setText(String.format(Locale.US, "%.2f", importe)); // Usar Locale.US para forzar el punto decimal
        checkBoxEstado.setChecked(estado);

        // Load stores
        AccesoRemoto accesoRemoto = new AccesoRemoto(this);
        tiendas = accesoRemoto.obtenerListadoTiendas();
        cargarTiendasEnSpinner();

        btnGuardarCambios.setOnClickListener(v -> {
            String nuevaFechaPedido = editTextFechaPedido.getText().toString();
            String nuevaFechaEstimada = editTextFechaEstimada.getText().toString();
            String nuevaDescripcion = editTextDescripcion.getText().toString();
            String importeTexto = editTextImporte.getText().toString();
            int idTiendaFK = obtenerIdTiendaSeleccionada();
            int nuevoEstado = checkBoxEstado.isChecked() ? 1 : 0;

            // Validate input
            if (nuevaFechaPedido.isEmpty() || nuevaFechaEstimada.isEmpty() || nuevaDescripcion.isEmpty() || importeTexto.isEmpty() || idTiendaFK == -1) {
                Toast.makeText(this, "Por favor, complete todos los campos correctamente.", Toast.LENGTH_LONG).show();
                return;
            }

            double nuevoImporte;
            try {
                nuevoImporte = Double.parseDouble(importeTexto);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor, ingrese un importe válido.", Toast.LENGTH_LONG).show();
                return;
            }

            ModificacionRemota modificacionRemota = new ModificacionRemota(this);
            boolean exito = modificacionRemota.modificarPedido(idPedido, nuevaFechaPedido, nuevaFechaEstimada, nuevaDescripcion, nuevoImporte, nuevoEstado, idTiendaFK);

            if (exito) {
                Toast.makeText(this, "Éxito: Pedido modificado exitosamente", Toast.LENGTH_LONG).show();
                finish();
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

        // Optionally, set the spinner selection to the current store if known
        int idTiendaSeleccionada = getIntent().getIntExtra("idTiendaFK", -1);
        if (idTiendaSeleccionada != -1) {
            for (int i = 0; i < tiendas.length(); i++) {
                try {
                    if (tiendas.getJSONObject(i).getInt("idTienda") == idTiendaSeleccionada) {
                        spinnerTiendas.setSelection(i);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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