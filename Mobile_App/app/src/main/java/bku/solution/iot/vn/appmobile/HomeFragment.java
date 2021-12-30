package bku.solution.iot.vn.appmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PHITRUONG on 11/19/2021.
 */

public class HomeFragment extends Fragment {
    LineChart graph;
    TextView txtNewValue, txtLastValue, txtTime, txtAverage;
    String lastValue = "None";
    float sum_value = 0;
    int num_value = 0;
    int idLast = -1;
    final String url = "https://api.thingspeak.com/channels/1575517/feeds.json?api_key=4FCLL1TRBMBT9XZP&results=1";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        graph = view.findViewById(R.id.graphMini);
        txtNewValue = view.findViewById(R.id.txtLightIntensity);
        txtLastValue = view.findViewById(R.id.txtLastValue);
        txtTime = view.findViewById(R.id.txtTime);
        txtAverage = view.findViewById(R.id.txtAverageValue);
        graph.getDescription().setEnabled(true);
        graph.getDescription().setText("Biểu đồ cường độ ánh sáng");
        graph.setNoDataText("No data for the moment");

        graph.setHighlightPerDragEnabled(true);
        graph.setTouchEnabled(true);

        graph.setDragEnabled(true);
        graph.setScaleEnabled(true);
        graph.setDrawGridBackground(false);

        graph.setPinchZoom(true);
        graph.setBackgroundColor(Color.argb(180, 233, 233, 231));

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        graph.setData(data);

        Legend l = graph.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = graph.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);


        YAxis leftAxis = graph.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(1500f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.BLACK);

        YAxis rightAxis = graph.getAxisRight();
        rightAxis.setEnabled(false);
        UpdateGraph();
        return view;
    }
    private void addEntrytoGraph(float value) {

        LineData data = graph.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), value), 0);
            data.notifyDataChanged();

            // let the graph know it's data has changed
            graph.notifyDataSetChanged();

            // limit the number of visible entries
            graph.setVisibleXRangeMaximum(5);

            // move to the latest entry
            graph.moveViewToX(data.getEntryCount());
        }
    }
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Light intensity waves");
        set.setDrawCircles(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.RED);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(3.5f);
        set.setCircleRadius(4.0f);
        set.setFillAlpha(65);
        set.setFillColor(Color.GREEN);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(13f);
        set.setDrawValues(true);
        return set;
    }
    public void readJson(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray feeds = response.getJSONArray("feeds");
                            for(int i=0; i<feeds.length();i++){
                                JSONObject object = feeds.getJSONObject(i);
                                int idNew = object.getInt("entry_id");
                                String newvalue  = object.getString("field1");
                                if(idLast != idNew){
                                    num_value++;
                                    txtNewValue.setText(newvalue);
                                    txtLastValue.setText(lastValue);
                                    sum_value += Float.parseFloat(newvalue);
                                    txtTime.setText(getTime());
                                    txtAverage.setText(Float.toString((float)(sum_value/num_value)));
                                    idLast = idNew;
                                    lastValue = newvalue;
                                    addEntrytoGraph(Float.parseFloat(newvalue));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(objectRequest);
    }
    private void UpdateGraph(){
        Timer aTimer = new Timer();
        TimerTask aTask = new TimerTask() {
            @Override
            public void run() {
                readJson(url);
            }
        };
        aTimer.schedule(aTask, 0, 500);
    }
    private String getTime() {
        String time;
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millis = c.get(Calendar.MILLISECOND);
        time = Integer.toString(month + 1) + "/" + Integer.toString(day) + "/" + Integer.toString(year) + "  "
                + hour + ":" + minute + ":" + second + ":" + millis;
        return time;
    }
}
