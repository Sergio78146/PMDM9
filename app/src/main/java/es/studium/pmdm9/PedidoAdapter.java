package es.studium.pmdm9;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {
    private JSONArray pedidos;
    private Context context;

    public PedidoAdapter(JSONArray pedidos, Context context) {
        this.pedidos = pedidos;
        this.context = context;
        ordenarPedidosPorFecha(); // Ordenar los pedidos por fecha
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        JSONObject pedido = pedidos.optJSONObject(position);
        if (pedido != null) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditarPedidoActivity.class);
                intent.putExtra("idPedido", pedido.optInt("idPedido"));
                intent.putExtra("fechaPedido", pedido.optString("fechaPedido"));
                intent.putExtra("fechaEstimada", pedido.optString("fechaEstimadaPedido")); // Corregido
                intent.putExtra("descripcion", pedido.optString("descripcionPedido"));
                intent.putExtra("importe", pedido.optDouble("importePedido", 0.0)); // Corregido

                context.startActivity(intent);
            });

            holder.itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Confirmar Borrado")
                        .setMessage("¿Seguro que deseas borrar este pedido?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            BajaRemota bajaRemota = new BajaRemota();
                            boolean exito = bajaRemota.darBajaPedido(pedido.optInt("idPedido"));
                            if (exito) {
                                actualizarPedidosEliminados(position);
                            } else {
                                mostrarDialogo("Error", "No se pudo eliminar el pedido");
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            });

            try {
                int idTiendaFK = pedido.optInt("idTiendaFK", -1);
                String nombreTienda = AccesoRemoto.obtenerTiendaPorId(idTiendaFK);
                double importePedido = pedido.optDouble("importePedido", 0.0);
                String fechaEstimadaPedido = pedido.optString("fechaEstimadaPedido", "Sin fecha");
                //int estadoPedido = pedido.optInt("estadoPedido", 0);
                String descripcionPedido = pedido.optString("descripcionPedido", "Sin descripción");

                holder.textViewNombreTiendaValor.setText(nombreTienda);
                holder.textViewImporteValor.setText(String.format("€%.2f", importePedido));
                holder.textViewFechaEstimadaValor.setText(fechaEstimadaPedido);
               // holder.textViewEstadoValor.setText(estadoPedido == 0 ? "Recibido" : "Pendiente");
                holder.textViewDescripcionValor.setText(descripcionPedido);
            } catch (Exception e) {
                Log.e("PedidoAdapter", "Error al obtener datos del pedido: " + e.getMessage());
            }
        } else {
            Log.e("PedidoAdapter", "Pedido nulo en la posición " + position);
        }
    }

    @Override
    public int getItemCount() {
        return pedidos != null ? pedidos.length() : 0;
    }

    public void actualizarPedidos(JSONArray nuevosPedidos) {
        this.pedidos = nuevosPedidos;
        ordenarPedidosPorFecha();
        notifyDataSetChanged();
    }

    private void actualizarPedidosEliminados(int position) {
        List<JSONObject> pedidosList = new ArrayList<>();
        for (int i = 0; i < pedidos.length(); i++) {
            if (i != position) {
                try {
                    pedidosList.add(pedidos.getJSONObject(i));
                } catch (JSONException e) {
                    Log.e("PedidoAdapter", "Error al eliminar pedido: " + e.getMessage());
                }
            }
        }
        pedidos = new JSONArray(pedidosList);
        notifyDataSetChanged();
    }

    private void ordenarPedidosPorFecha() {
        List<JSONObject> pedidosList = new ArrayList<>();
        for (int i = 0; i < pedidos.length(); i++) {
            try {
                pedidosList.add(pedidos.getJSONObject(i));
            } catch (JSONException e) {
                Log.e("PedidoAdapter", "Error al agregar pedido a la lista: " + e.getMessage());
            }
        }

        pedidosList.sort((o1, o2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date fecha1 = dateFormat.parse(o1.optString("fechaPedido", "1970-01-01"));
                Date fecha2 = dateFormat.parse(o2.optString("fechaPedido", "1970-01-01"));
                return fecha1.compareTo(fecha2);
            } catch (ParseException e) {
                Log.e("PedidoAdapter", "Error al ordenar pedidos: " + e.getMessage());
                return 0;
            }
        });

        this.pedidos = new JSONArray(pedidosList);
    }

    private void mostrarDialogo(String titulo, String mensaje) {
        new AlertDialog.Builder(context)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombreTiendaValor;
        TextView textViewImporteValor;
        TextView textViewFechaEstimadaValor;
        TextView textViewDescripcionValor;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreTiendaValor = itemView.findViewById(R.id.textViewNombreTiendaValor);
            textViewImporteValor = itemView.findViewById(R.id.textViewImporteValor);
            textViewFechaEstimadaValor = itemView.findViewById(R.id.textViewFechaEstimadaValor);
            textViewDescripcionValor = itemView.findViewById(R.id.textViewDescripcionValor);
        }
    }
}
