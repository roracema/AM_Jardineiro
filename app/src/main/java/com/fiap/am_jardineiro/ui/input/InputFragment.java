package com.fiap.am_jardineiro.ui.input;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        inputViewModel =
                ViewModelProviders.of(this).get(InputViewModel.class);
        View root = inflater.inflate(R.layout.fragment_input, container, false);

        final TextView textView = root.findViewById(R.id.text_slideshow);

        inputViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("Cadastrar planta e a umidade ideal");
                final TextView seedName = getView().findViewById(R.id.nome_planta);

                final TextView seedUmidade = getView().findViewById(R.id.umidade_ideal);

                Button btnSend = (Button) getView().findViewById(R.id.send_planta);
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.out.println(myResponse);
                                            Snackbar.make(getView(), "Cadastrado com sucesso", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                                                    .hideSoftInputFromWindow(seedUmidade.getWindowToken(), 0);

                                        }
                                    });
                                }
                            }
                        });
                    }
                });
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // get the reference of RecyclerView
                                    RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
                                    // set a LinearLayoutManager with default vertical orientation
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
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
                                            JSONObject userDetail = dataJson.getJSONObject(i);
                                            // fetch email and name and store it in arraylist
                                            codigos.add(userDetail.getString("codigoSeed"));
                                            nomes.add(userDetail.getString("nome"));
                                            umidades.add(userDetail.getString("umidadeIdeal"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    CustomAdapter customAdapter = new CustomAdapter(getActivity(), nomes, codigos, umidades);
                                    recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView

                                }
                            });
                        }
                    }
                });



            }
        });
        return root;
    }

}