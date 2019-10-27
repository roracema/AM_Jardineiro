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

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;
import com.fiap.am_jardineiro.MainActivity;
import com.fiap.am_jardineiro.R;

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
                //MONTA O GRAFICO
                Pie pie = AnyChart.pie();
                List<DataEntry> data = new ArrayList<>();
                data.add(new ValueDataEntry("John", 10000));
                data.add(new ValueDataEntry("Jake", 12000));
                data.add(new ValueDataEntry("Peter", 18000));
                pie.setData(data);
                AnyChartView anyChartView = (AnyChartView) getView().findViewById(R.id.any_chart);
                anychart.setChart(pie);

                textView.setText(s);
            }
        });



        return root;
    }

}