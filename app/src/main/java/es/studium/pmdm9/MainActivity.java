package es.studium.pmdm9;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPedidos;
    private PedidoAdapter pedidoAdapter;
    private JSONArray resultPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar RecyclerView
        recyclerViewPedidos = findViewById(R.id.recyclerViewPedidos);
        recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(this));

        // Configurar botones
        Button btnNuevoPedido = findViewById(R.id.btnNuevoPedido);
        Button btnTiendas = findViewById(R.id.btnTiendas);

        btnNuevoPedido.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NuevoPedidoActivity.class);
            startActivity(intent);
        });

        btnTiendas.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TiendasActivity.class);
            startActivity(intent);
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Cargar datos de pedidos
        cargarDatosPedidos();
    }

    private void cargarDatosPedidos() {
        AccesoRemoto accesoRemoto = new AccesoRemoto(this);
        resultPedidos = accesoRemoto.obtenerListadoPedidos();
        pedidoAdapter = new PedidoAdapter(resultPedidos, this);
        recyclerViewPedidos.setAdapter(pedidoAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos al volver a la actividad
        cargarDatosPedidos();
    }
}
