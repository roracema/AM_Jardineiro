package com.fiap.am_jardineiro.ui.input;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fiap.am_jardineiro.CustomAdapter;
import com.fiap.am_jardineiro.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InputFragment extends Fragment {

    private InputViewModel inputViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private static Activity context = null;
    TextView seedUmidade;
    TextView seedName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        inputViewModel =
                ViewModelProviders.of(this).get(InputViewModel.class);
        View root = inflater.inflate(R.layout.fragment_input, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        context = getActivity();
        inputViewModel.getText().observe(this, new Observer<String>() {

            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("Cadastrar planta e a umidade ideal");
                seedUmidade = getView().findViewById(R.id.umidade_ideal);
                seedName = getView().findViewById(R.id.nome_planta);

                Button btnSend = (Button) getView().findViewById(R.id.send_planta);
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cadastrar(seedName,seedUmidade,getView());

                    }
                });
                populateView();
            }
        });
        return root;
    }
    public static void cadastrar(final TextView seedName, final TextView seedUmidade,final View view){
        OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "https://jardineiro.mybluemix.net/plantasDesc";
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("codigo", 1);
            postdata.put("nome", seedName.getText());
            postdata.put("umidadeIdeal", seedUmidade.getText());
        } catch(JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(myResponse);
                            seedName.setText("");
                            seedUmidade.setText("");
                            Snackbar.make(view, "Cadastrado com sucesso", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            populateView();
                            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                                    .hideSoftInputFromWindow(seedUmidade.getWindowToken(), 0);

                        }
                    });
                }
            }
        });
    }
    public static void populateView(){
        OkHttpClient client = new OkHttpClient();
        String url = "https://jardineiro.mybluemix.net/plantasdesc";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // get the reference of RecyclerView
                            RecyclerView recyclerView = (RecyclerView) context.findViewById(R.id.recyclerView);
                            // set a LinearLayoutManager with default vertical orientation
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            ArrayList<String> codigos = new ArrayList<>();
                            ArrayList<String> nomes = new ArrayList<>();
                            ArrayList<String> umidades = new ArrayList<>();

                            try {
                                // fetch JSONArray named users
                                JSONArray dataJson= new JSONArray(myResponse.toString());
                                // implement for loop for getting users list data
                                for (int i = 0; i < dataJson.length(); i++) {
                                    // create a JSONObject for fetching single user data
                                    JSONObject jsonData = dataJson.getJSONObject(i);
                                    // fetch email and name and store it in arraylist
                                    codigos.add(jsonData.getString("id"));
                                    nomes.add(jsonData.getString("nome"));
                                    umidades.add(jsonData.getString("umidadeIdeal"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            CustomAdapter customAdapter = new CustomAdapter(context, nomes, codigos, umidades);
                            recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView

                        }
                    });
                }
            }
        });
    }
    public static void delete(String codigo){
        System.out.println("codigo:"+codigo);
        OkHttpClient client = new OkHttpClient();
        String url = "https://jardineiro.mybluemix.net/plantasdesc?codigo="+codigo;
        Request request = new Request.Builder().url(url).delete().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            populateView();
                            System.out.println(myResponse);
                        }
                    });
                }
            }
        });

    };

    public static void update(String codigo,String seedName, String seedUmidade){
        OkHttpClient client = new OkHttpClient();
        String url = "https://jardineiro.mybluemix.net/plantasdesc?codigo="+codigo+"&nome="+seedName+"&umidadeIdeal="+seedUmidade;
        MediaType MEDIA_TYPE = MediaType.parse("application/json");

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("nome", seedName);
            postdata.put("umidadeIdeal", seedUmidade);
        } catch(JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        Request request = new Request.Builder().url(url).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            populateView();
                            System.out.println(myResponse);
                        }
                    });
                }
            }
        });

    };
}