package edu.illinois.ncsa.bwmon.Task;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import edu.illinois.ncsa.bwmon.Adapter.SectionsPagerAdapter;
import edu.illinois.ncsa.bwmon.DataModel.Datafeed;
import edu.illinois.ncsa.bwmon.MainActivity;
import edu.illinois.ncsa.bwmon.R;

public class DownloadFeedDetailsTask extends AsyncTask<String, Void, ArrayList<String>> {
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
        tv1.clearComposingText();
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

    private void setLineChartData(ArrayList<String> result){
        String[] first_line = result.get(0).split(",");
        String x_name = first_line[2];
        title = first_line[0].concat(" ").concat(first_line[1]);
        int num_y = first_line.length - 3;
        if (num_y < 0)
        {
            Toast.makeText(MainActivity.mainContext,"need more data to generate line chart",Toast.LENGTH_LONG).show();
        }
        else
        {
            XYSeries[] xyseries = new XYSeries[num_y];
            for (int i = 0; i < xyseries.length; i++){
                String name = x_name.concat(" vs ").concat(first_line[i+3]);
                xyseries[i] = new XYSeries(name);
            }
            for (int i = 1; i < result.size(); i++)
            {
                String[] line = result.get(i).split(",");
                double x = Double.parseDouble(line[0]);
                for (int j = 1; j < line.length; j++)
                {
                    double y = Double.parseDouble(line[j]);
                    xyseries[j-1].add(x,y);
                }
            }

            MainActivity.mRenderer = new XYMultipleSeriesRenderer();
            MainActivity.dataset = new XYMultipleSeriesDataset();
            for (int i = 0; i < xyseries.length; i++){
                MainActivity.dataset.addSeries(xyseries[i]);
                XYSeriesRenderer renderer = new XYSeriesRenderer();
                renderer.setLineWidth(2);
                renderer.setColor(colors[i]);
                renderer.setDisplayBoundingPoints(true);
                renderer.setPointStyle(PointStyle.CIRCLE);
                renderer.setPointStrokeWidth(3);
                MainActivity.mRenderer.addSeriesRenderer(renderer);
            }
            MainActivity.mRenderer.setXTitle(x_name);
            MainActivity.mRenderer.setChartTitle(title);
        }
    }

    private void drawLine(){
        ViewGroup chartContainer = (ViewGroup)v1.findViewById(R.id.chart);
        if (MainActivity.mChartView != null){
            chartContainer.removeView(MainActivity.mChartView);
        }
        MainActivity.mChartView = ChartFactory.getLineChartView(MainActivity.mainContext, MainActivity.dataset, MainActivity.mRenderer);
        MainActivity.mRenderer.setPanEnabled(false, false);
        MainActivity.mRenderer.setLabelsTextSize(40);
        MainActivity.mRenderer.setLegendTextSize(40);
        MainActivity.mRenderer.setChartTitleTextSize(60);
        MainActivity.mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        chartContainer.addView(MainActivity.mChartView,0);
    }

    private void displayLineChart(ArrayList<String> result){
        String temp = "";
        setLineChartData(result);
        drawLine();
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
        if (MainActivity.mChartView != null){
            chartContainer.removeView(MainActivity.mChartView);
        }
        MainActivity.mChartView = ChartFactory.getPieChartView(MainActivity.mainContext, MainActivity.distributionSeries, MainActivity.defaultRenderer);
        // Adding the pie chart to the custom layout
        chartContainer.addView(MainActivity.mChartView);
    }

    private void displayPieChart(ArrayList<String> result){
        tv1.setText("");
        setPieChartData(result);
        drawPie();
        MainActivity.drawPie=1;

    }

}
