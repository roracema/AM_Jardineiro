package com.fiap.am_jardineiro.ui.history;

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

import com.anychart.AnyChart;
import com.anychart.AnyChartView;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.charts.Radar;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.radar.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.fiap.am_jardineiro.MainActivity;
import com.fiap.am_jardineiro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        final AnyChartView anychart = root.findViewById(R.id.any_chart);

        final TextView textView = root.findViewById(R.id.text_tools);
        historyViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                createChart();
                textView.setText(s);
            }
        });



        return root;
    }

    public void createChart(){
        OkHttpClient client = new OkHttpClient();
        String url = "https://jardineiro.mybluemix.net/plantas/history";
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
                                //MONTA O GRAFICO
                                AnyChartView anyChartView = (AnyChartView) getView().findViewById(R.id.any_chart);
                                anyChartView.setProgressBar(getView().findViewById(R.id.progress_bar));

                                // fetch JSONArray named users
                                JSONArray dataJson= new JSONArray(myResponse.toString());
                                Cartesian cartesian = AnyChart.column();
                                List<DataEntry> data = new ArrayList<>();
                                for (int i = 0; i < dataJson.length(); i++) {
                                    JSONObject response = dataJson.getJSONObject(i);
                                    Float f= Float.parseFloat(response.getString("umidade"));
                                    data.add(new ValueDataEntry(response.getString("data"),f));
                                }


                                Column column = cartesian.column(data);

                                column.tooltip()
                                        .titleFormat("{%X}")
                                        .position(Position.CENTER_BOTTOM)
                                        .anchor(Anchor.CENTER_BOTTOM)
                                        .offsetX(0d)
                                        .offsetY(5d)
                                        .format("{%Value}{groupsSeparator: }%");

                                cartesian.animation(true);
                                String title = getString(R.string.history_title);
                                String date = getString(R.string.history_date);
                                String humidity = getString(R.string.history_percent);

                                cartesian.title(title);

                                cartesian.yScale().minimum(0d);

                                cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }%");

                                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                                cartesian.interactivity().hoverMode(HoverMode.BY_X);

                                cartesian.xAxis(0).title(date);
                                cartesian.yAxis(0).title(humidity);

                                anyChartView.setChart(cartesian);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });

    }
}