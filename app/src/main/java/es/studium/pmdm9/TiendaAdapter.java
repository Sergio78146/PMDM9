package es.studium.pmdm9;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

public class TiendaAdapter extends RecyclerView.Adapter<TiendaAdapter.TiendaViewHolder> {
    private JSONArray tiendas;
    private Context context;

    public TiendaAdapter(JSONArray tiendas, Context context) {
        this.tiendas = tiendas;
        this.context = context;
    }

    @NonNull
    @Override
    public TiendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tienda, parent, false);
        return new TiendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TiendaViewHolder holder, int position) {
        JSONObject tienda = tiendas.optJSONObject(position);
        if (tienda != null) {
            holder.textViewNombreTienda.setText(tienda.optString("nombreTienda", "Sin nombre"));

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditarTiendaActivity.class);
                intent.putExtra("idTienda", tienda.optInt("idTienda"));
                intent.putExtra("nombreTienda", tienda.optString("nombreTienda"));
                context.startActivity(intent);
            });

            holder.itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Confirmar Borrado")
                        .setMessage("¿Seguro que deseas borrar esta tienda?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            BajaRemota bajaRemota = new BajaRemota();
                            boolean exito = bajaRemota.darBajaTienda(tienda.optInt("idTienda"));
                            if (exito) {
                                tiendas.remove(position);
                                notifyDataSetChanged();
                            } else {
                                mostrarDialogo("Error", "No se pudo eliminar la tienda");
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            });
        } else {
            holder.textViewNombreTienda.setText("Sin datos");
        }
    }

    @Override
    public int getItemCount() {
        return tiendas != null ? tiendas.length() : 0;
    }

    public void actualizarTiendas(JSONArray nuevasTiendas) {
        tiendas = nuevasTiendas;
        notifyDataSetChanged();
    }

    private void mostrarDialogo(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, id) -> {});
        builder.create().show();
    }

    public static class TiendaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombreTienda;

        public TiendaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreTienda = itemView.findViewById(R.id.textViewNombreTienda);
        }
    }
}
