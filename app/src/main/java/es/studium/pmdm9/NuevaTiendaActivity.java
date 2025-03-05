package es.studium.pmdm9;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NuevaTiendaActivity extends AppCompatActivity {

    private EditText editTextNombreTienda;
    private Button btnGuardarTienda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_tienda);

        editTextNombreTienda = findViewById(R.id.editTextNombreTienda);
        btnGuardarTienda = findViewById(R.id.btnGuardarTienda);

        btnGuardarTienda.setOnClickListener(v -> {
            String nombreTienda = editTextNombreTienda.getText().toString();

            if (nombreTienda.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese el nombre de la tienda.", Toast.LENGTH_LONG).show();
                return;
            }

            AltaRemota altaRemota = new AltaRemota(this);
            String error = altaRemota.darAltaTienda(nombreTienda);

            if (error == null) {
                Toast.makeText(this, "Éxito: Tienda creada exitosamente", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
