package com.fiap.am_jardineiro.ui.home;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.fiap.am_jardineiro.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private static Activity context = null;
    TextView feedback;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        feedback = root.findViewById(R.id.feedback);
        final TextView textView = root.findViewById(R.id.text_home);
        final ImageView imageView = root.findViewById(R.id.imageView);
        final TextView status = root.findViewById(R.id.status);
        final Button sendFeedback = root.findViewById(R.id.send_feedback);
        sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarFeedback(feedback.getText().toString(),getView());

            }
        });
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                trazerUmidade(textView,status,imageView);
                //textView.setText(s);
            }
        });
        return root;
    }
    public void enviarFeedback(String text,final View view){
        OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "https://jardineiro.mybluemix.net/feedback";
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("feedback", text);
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
                            feedback.setText("");
                            Snackbar.make(view, "FeedBack enviado, Obrigado!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                                    .hideSoftInputFromWindow(feedback.getWindowToken(), 0);

                        }
                    });
                }
            }
        });
    }


    public void trazerUmidade(final TextView textView,final TextView status, final ImageView image){
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
                            try {
                                JSONArray dataJson= new JSONArray(myResponse.toString());

                                JSONObject jsonData = dataJson.getJSONObject(0);
                                String umidade = jsonData.get("umidade").toString();
                                Float floatUmidade = new Float(umidade);
                                textView.setText("");
                                if (floatUmidade<=40){
                                    status.setText("Sua plantinha esta feliz hoje");

                                }else {
                                    status.setText("Sua plantinha esta com cede");

                                }
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }

                        }
                    });
                }
            }
        });
    }
}