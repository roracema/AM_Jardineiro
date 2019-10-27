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

    public CustomAdapter(Context context, ArrayList<String> personNames, ArrayList<String> emailIds, ArrayList<String> mobileNumbers) {
        this.context = context;
        this.codigos = personNames;
        this.nomes = emailIds;
        this.umidades = mobileNumbers;
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
        holder.name.setText(codigos.get(position));
        holder.codigo.setText(nomes.get(position));
        holder.umidade.setText(umidades.get(position));
        Button btn = holder.itemView.findViewById(R.id.delete_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OkHttpClient client = new OkHttpClient();
//                String url = "https://jardineiro.mybluemix.net/plantas/ultima";
//                Request request = new Request.Builder().url(url).build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if(response.isSuccessful()){
//                            final String myResponse = response.body().string();
//                            .runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    System.out.println(myResponse);
//                                }
//                            });
//                        }
//                    }
//                });
            }

        });
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast.makeText(context, nomes.get(position), Toast.LENGTH_SHORT).show();

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
