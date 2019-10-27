package com.fiap.am_jardineiro;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.fiap.am_jardineiro.ui.input.InputFragment;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    ArrayList<String> codigos;
    ArrayList<String> nomes;
    ArrayList<String> umidades;
    Context context;

    public CustomAdapter(Context context, ArrayList<String> nomes, ArrayList<String> codigos, ArrayList<String> umidades) {
        this.context = context;
        this.codigos = codigos;
        this.nomes = nomes;
        this.umidades = umidades;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items
        holder.name.setText(nomes.get(position));
        holder.codigo.setText(codigos.get(position));
        holder.umidade.setText(umidades.get(position));
        Button btn = holder.itemView.findViewById(R.id.delete_button);
        Button btnUpdate = holder.itemView.findViewById(R.id.update_button);
        final TextView seedName = holder.itemView.findViewById(R.id.name);
        final TextView seedUmidade = holder.itemView.findViewById(R.id.umidade);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputFragment.delete(codigos.get(position));
                Toast.makeText(context, nomes.get(position)+" removido", Toast.LENGTH_SHORT).show();
                InputFragment.populateView();
            }

        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputFragment.update(codigos.get(position),seedName.getText().toString(),seedUmidade.getText().toString());
                Toast.makeText(context, nomes.get(position)+" Atualizado", Toast.LENGTH_SHORT).show();
                InputFragment.populateView();
            }

        });
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast.makeText(context, nomes.get(position) + " Seed", Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public int getItemCount() {
        return nomes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, codigo, umidade;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            codigo = (TextView) itemView.findViewById(R.id.codigo);
            umidade = (TextView) itemView.findViewById(R.id.umidade);

        }
    }
}
