package edu.illinois.ncsa.bwmon;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class DownloadFeedDetailsTask extends AsyncTask<String, Void, ArrayList<String>> {
    private int nPosition;
    private Datafeed df;
    private TextView tv1;
    private View v1;
    private final int[] colors = { Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED, Color.YELLOW };
    private double[] distribution;
    private String title;
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
        if (result.size() > 0){
            df = MainActivity.datafeedsList.getDatafeedList()[nPosition];
            switch (df.getType()){
                case "text":
                    displayText(result);
                    break;
                case "piechart":
                    displayPieChart(result);
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

    private void setPieChartData(ArrayList<String> result){
        String[] first_line = result.get(0).split(",");
        String[] second_line = result.get(1).split(",");
        title = first_line[0].concat(" ").concat(first_line[1]);
        distribution = new double[second_line.length];
        for (int i = 0; i < distribution.length; i++)
        {
            distribution[i] = Double.parseDouble(second_line[i]);
        }
        MainActivity.distributionSeries = new CategorySeries(title);
        for(int i=0 ;i < distribution.length;i++){
            // Adding a slice with its values and name to the Pie Chart
            MainActivity.distributionSeries.add(first_line[i+2], distribution[i]);
        }
    }

    private void drawPie(){
        MainActivity.defaultRenderer  = new DefaultRenderer();
        for(int i = 0 ;i<distribution.length;i++){
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(colors[i]);
            seriesRenderer.setDisplayBoundingPoints(true);
            // Adding a renderer for a slice
            MainActivity.defaultRenderer.addSeriesRenderer(seriesRenderer);
        }

        MainActivity.defaultRenderer.setChartTitle(title);
        MainActivity.defaultRenderer.setChartTitleTextSize(60);
        MainActivity.defaultRenderer.setZoomButtonsVisible(true);
        MainActivity.defaultRenderer.setLegendTextSize(40);
        MainActivity.defaultRenderer.setLabelsTextSize(40);

        ViewGroup chartContainer = (ViewGroup)v1.findViewById(R.id.chart);

        MainActivity.mChartView = ChartFactory.getPieChartView(MainActivity.mainContext, MainActivity.distributionSeries, MainActivity.defaultRenderer);
        // Adding the pie chart to the custom layout
        chartContainer.addView(MainActivity.mChartView);
    }

    private void displayPieChart(ArrayList<String> result){
        setPieChartData(result);
        if(MainActivity.drawPie==0)
        {
            drawPie();
            MainActivity.drawPie=1;

        }
        else {
            if (MainActivity.mChartView != null) {
                MainActivity.distributionSeries.clear();
                setPieChartData(result);
                MainActivity.defaultRenderer.setChartTitle(title);
                MainActivity.mChartView.repaint();
            }
        }


    }

}
