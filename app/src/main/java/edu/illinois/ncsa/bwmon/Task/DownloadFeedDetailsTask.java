package edu.illinois.ncsa.bwmon.Task;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import edu.illinois.ncsa.bwmon.Adapter.SectionsPagerAdapter;
import edu.illinois.ncsa.bwmon.Chart.LineChart;
import edu.illinois.ncsa.bwmon.Chart.PieChart;
import edu.illinois.ncsa.bwmon.DataModel.Datafeed;
import edu.illinois.ncsa.bwmon.MainActivity;
import edu.illinois.ncsa.bwmon.R;

public class DownloadFeedDetailsTask extends AsyncTask<String, Void, ArrayList<String>> {
    private int nPosition;
    private Datafeed df;
    private TextView tv1;
    public static View v1;
    public static SwipeRefreshLayout swipeContainer;
    private InputStream downloadUrl(String urlString) throws IOException {
        // BEGIN_INCLUDE(get_inputstream)
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
        // END_INCLUDE(get_inputstream)
    }

    private ArrayList<String> loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        BufferedReader reader = null;
        String line = "";
        ArrayList<String> lines = new ArrayList<String>();
        try {
            stream = downloadUrl(urlString);
            reader = new BufferedReader(new InputStreamReader(stream));
            while((line = reader.readLine()) != null) {
                lines.add(line.toString());
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return lines;
    }

    public DownloadFeedDetailsTask(int nPos) {
        nPosition = nPos;
    }

    @Override
    protected ArrayList<String> doInBackground(String... urls) {
        try {
            return loadFromNetwork(urls[0]);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Uses the logging framework to display the output of the fetch
     * operation in the log fragment.
     */
    @Override
    protected void onPostExecute(ArrayList<String> result) {
        //Log.i(TAG, result);
        //fragment.setTitle(result, nPosition);
        //View v1 = MainActivity.mViewPager.getChildAt(nPosition);
        v1 = SectionsPagerAdapter.fragmentList.get(nPosition).v;
        tv1 = (TextView) v1.findViewById(R.id.section_label);
        tv1.clearComposingText();
        swipeContainer = (SwipeRefreshLayout) v1.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                new DownloadFeedDetailsTask(nPosition).execute(MainActivity.datafeedsList.getDatafeedList()[nPosition].getUrl());
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setRefreshing(false);
        if (result.size() > 0){
            df = MainActivity.datafeedsList.getDatafeedList()[nPosition];
            switch (df.getType()){
                case "text":
                    displayText(result);
                    break;
                case "piechart":
                    displayPieChart(result);
                    break;
                case "linechart":
                    displayLineChart(result);
                    break;
            }
        }
        else
            tv1.setText("Empty feeds");
    }

    private void displayText(ArrayList<String> result){
        String temp = "";
        for (int i = 0; i < result.size(); i++){
            temp = temp.concat(result.get(i));
            temp = temp.concat("\n");
        }
        tv1.setText(temp);
    }

    private void displayLineChart(ArrayList<String> result){
        tv1.setText("");
        LineChart.setLineChartData(result);
        LineChart.drawLine();
    }

    private void displayPieChart(ArrayList<String> result){
        tv1.setText("");
        PieChart.setPieChartData(result);
        PieChart.drawPie();
    }

}
