package com.fiap.am_jardineiro.ui.track_live;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.fiap.am_jardineiro.R;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrackLiveFragment extends Fragment {

    private TrackLiveViewModel trackLiveViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trackLiveViewModel =
                ViewModelProviders.of(this).get(TrackLiveViewModel.class);
        View root = inflater.inflate(R.layout.fragment_track_live, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        final TextView latestPercent = root.findViewById(R.id.latest_percent);
        final TextView plantNow = root.findViewById(R.id.plant_now);
        final TextView plantState = root.findViewById(R.id.plant_state);
        final TextView btnRefresh = root.findViewById(R.id.refresh_track);
        trackLiveViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                String string = getString(R.string.track_loading);
                getStatus(latestPercent,plantNow,plantState,textView);
                textView.setText(string);
                btnRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getStatus(latestPercent,plantNow,plantState,textView);

                    }
                });
            }
        });
        return root;
    }
    public void getStatus(final TextView latestPercent,final TextView plantNow,final TextView plantState,final TextView textView){
        OkHttpClient client = new OkHttpClient();
        String url = "https://jardineiro.mybluemix.net/plantas/ultima";
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
                            System.out.println(myResponse);
                            latestPercent.setText(getString(R.string.latest_percent));
                            try {
                                JSONArray dataJson= new JSONArray(myResponse.toString());

                                JSONObject jsonData = dataJson.getJSONObject(0);
                                String umidade = jsonData.get("umidade").toString();
                                plantNow.setText(umidade+"%");
                                Float floatUmidade = new Float(umidade);
                                if (floatUmidade<=20){
                                    plantState.setText("Sua planta esta Hidratada :)");
                                }else if(floatUmidade>20 || floatUmidade<50){
                                    plantState.setText("Sua planta está começando a ficar desidratada :/");
                                }else{
                                    plantState.setText("Não me abandona");
                                }
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                            //if(umidade<18) -> print boa
                            textView.setText("");

                        }
                    });
                }
            }
        });
    }
}