package es.studium.pmdm9;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditarTiendaActivity extends AppCompatActivity {

    private EditText editTextNombreTienda;
    private Button btnGuardarCambios;
    private int idTienda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_tienda);

        editTextNombreTienda = findViewById(R.id.editTextNombreTienda);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        // Obtener los datos de la tienda a editar
        idTienda = getIntent().getIntExtra("idTienda", -1);
        String nombreTienda = getIntent().getStringExtra("nombreTienda");

        editTextNombreTienda.setText(nombreTienda);

        btnGuardarCambios.setOnClickListener(v -> {
            String nuevoNombre = editTextNombreTienda.getText().toString();

            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese el nombre de la tienda.", Toast.LENGTH_LONG).show();
                return;
            }

            ModificacionRemota modificacionRemota = new ModificacionRemota(this);
            boolean exito = modificacionRemota.modificarTienda(idTienda, nuevoNombre);

            if (exito) {

                Toast.makeText(this, "Éxito: Tienda modificada exitosamente", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
