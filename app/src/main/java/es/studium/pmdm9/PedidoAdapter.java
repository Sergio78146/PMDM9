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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {
    private JSONArray pedidos;
    private Context context;

    public PedidoAdapter(JSONArray pedidos, Context context) {
        this.pedidos = pedidos;
        this.context = context;
        ordenarPedidosPorFecha();
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
            try {
                String nombreTienda = pedido.getString("nombreTienda");
                double importePedido = pedido.has("importePedido") ? pedido.getDouble("importePedido") : 0.0;
                String fechaEstimadaPedido = pedido.getString("fechaEstimadaPedido");
                int estadoPedido = pedido.getInt("estadoPedido");
                String descripcionPedido = pedido.getString("descripcionPedido");

                // Log the values for debugging
                Log.d("PedidoAdapter", "Pedido #" + position + ": Tienda: " + nombreTienda +
                        ", Importe: " + importePedido +
                        ", Fecha Estimada: " + fechaEstimadaPedido +
                        ", Estado: " + estadoPedido +
                        ", Descripción: " + descripcionPedido);

                holder.textViewNombreTienda.setText(nombreTienda);
                holder.textViewImporte.setText(String.format("€%.2f", importePedido));
                holder.textViewFechaEstimada.setText(fechaEstimadaPedido);
                holder.textViewEstado.setText(estadoPedido == 1 ? "Recibido" : "Pendiente");
                holder.textViewDescripcion.setText(descripcionPedido);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("PedidoAdapter", "Error al obtener datos del pedido en la posición " + position + ": " + e.getMessage());
                mostrarDialogo("Error", "Error al cargar los datos del pedido.");
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditarPedidoActivity.class);
                try {
                    intent.putExtra("idPedido", pedido.getInt("idPedido"));
                    intent.putExtra("fechaPedido", pedido.getString("fechaPedido"));
                    intent.putExtra("fechaEstimada", pedido.getString("fechaEstimadaPedido"));
                    intent.putExtra("descripcion", pedido.getString("descripcionPedido"));
                    intent.putExtra("importe", pedido.getDouble("importePedido"));
                    intent.putExtra("estado", pedido.getInt("estadoPedido") == 1);
                    intent.putExtra("idTiendaFK", pedido.getInt("idTiendaFK"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("PedidoAdapter", "Error al preparar intent para editar pedido: " + e.getMessage());
                }
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
                                pedidos.remove(position);
                                notifyDataSetChanged();
                            } else {
                                mostrarDialogo("Error", "No se pudo eliminar el pedido");
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            });
        } else {
            Log.e("PedidoAdapter", "Pedido nulo en la posición " + position);
            holder.textViewNombreTienda.setText("Sin datos");
            holder.textViewImporte.setText("Sin datos");
            holder.textViewFechaEstimada.setText("Sin datos");
            holder.textViewEstado.setText("Sin datos");
            holder.textViewDescripcion.setText("Sin datos");
        }
    }

    @Override
    public int getItemCount() {
        return pedidos != null ? pedidos.length() : 0;
    }

    public void actualizarPedidos(JSONArray nuevosPedidos) {
        pedidos = nuevosPedidos;
        ordenarPedidosPorFecha();
        notifyDataSetChanged();
    }

    private void ordenarPedidosPorFecha() {
        List<JSONObject> pedidosList = new ArrayList<>();
        for (int i = 0; i < pedidos.length(); i++) {
            try {
                pedidosList.add(pedidos.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("PedidoAdapter", "Error al agregar pedido a la lista: " + e.getMessage());
            }
        }

        Collections.sort(pedidosList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fecha1 = dateFormat.parse(o1.getString("fechaPedido"));
                    Date fecha2 = dateFormat.parse(o2.getString("fechaPedido"));
                    return fecha1.compareTo(fecha2);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    Log.e("PedidoAdapter", "Error al ordenar pedidos: " + e.getMessage());
                    return 0;
                }
            }
        });

        JSONArray pedidosOrdenados = new JSONArray();
        for (JSONObject pedido : pedidosList) {
            pedidosOrdenados.put(pedido);
        }

        pedidos = pedidosOrdenados;
    }

    private void mostrarDialogo(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, id) -> {});
        builder.create().show();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombreTienda;
        TextView textViewImporte;
        TextView textViewFechaEstimada;
        TextView textViewEstado;
        TextView textViewDescripcion;
        View itemView;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewNombreTienda = itemView.findViewById(R.id.textViewNombreTienda);
            textViewImporte = itemView.findViewById(R.id.textViewImporte);
            textViewFechaEstimada = itemView.findViewById(R.id.textViewFechaEstimada);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
        }
    }
}