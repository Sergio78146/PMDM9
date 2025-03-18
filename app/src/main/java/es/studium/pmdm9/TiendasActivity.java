package es.studium.pmdm9;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

public class TiendasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTiendas;
    private TiendaAdapter tiendaAdapter;
    private JSONArray resultTiendas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiendas);

        // Configurar RecyclerView
        recyclerViewTiendas = findViewById(R.id.recyclerViewTiendas);
        recyclerViewTiendas.setLayoutManager(new LinearLayoutManager(this));

        // Configurar botón para crear una nueva tienda
        Button btnNuevaTienda = findViewById(R.id.btnNuevaTienda);
        btnNuevaTienda.setOnClickListener(v -> {
            Intent intent = new Intent(TiendasActivity.this, NuevaTiendaActivity.class);
            startActivity(intent);
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Cargar datos de tiendas
        AccesoRemoto accesoRemoto = new AccesoRemoto(this);
        resultTiendas = accesoRemoto.obtenerListadoTiendas();
        tiendaAdapter = new TiendaAdapter(resultTiendas, this);
        recyclerViewTiendas.setAdapter(tiendaAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos al volver a la actividad
        AccesoRemoto accesoRemoto = new AccesoRemoto(this);
        resultTiendas = accesoRemoto.obtenerListadoTiendas();
        tiendaAdapter.actualizarTiendas(resultTiendas);
    }
}
