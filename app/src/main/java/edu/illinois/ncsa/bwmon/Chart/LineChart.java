package edu.illinois.ncsa.bwmon.Chart;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

import edu.illinois.ncsa.bwmon.MainActivity;
import edu.illinois.ncsa.bwmon.R;
import edu.illinois.ncsa.bwmon.Task.DownloadFeedDetailsTask;

/**
 * Created by ylin9 on 2016/7/6.
 */
public class LineChart {
    private static String title;

    public static void setLineChartData(ArrayList<String> result){
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
            MainActivity.datafeedsList.getDatafeedList()[MainActivity.current_position].name_list = new ArrayList<String>();
            for (int i = 0; i < xyseries.length; i++){
                MainActivity.datafeedsList.getDatafeedList()[MainActivity.current_position].name_list.add(first_line[i+3]);
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
                renderer.setColor(MainActivity.color_list[MainActivity.current_position][i]);
                renderer.setDisplayBoundingPoints(true);
                renderer.setPointStyle(PointStyle.CIRCLE);
                renderer.setPointStrokeWidth(3);
                MainActivity.mRenderer.addSeriesRenderer(renderer);
            }
            MainActivity.mRenderer.setXTitle(x_name);
            MainActivity.mRenderer.setChartTitle(title);
        }
    }

    public static void drawLine(){
        ViewGroup chartContainer = (ViewGroup) DownloadFeedDetailsTask.v1.findViewById(R.id.chart);
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
}
