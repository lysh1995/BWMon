package edu.illinois.ncsa.bwmon.Chart;

import android.graphics.Color;
import android.view.ViewGroup;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;

import edu.illinois.ncsa.bwmon.MainActivity;
import edu.illinois.ncsa.bwmon.R;
import edu.illinois.ncsa.bwmon.Task.DownloadFeedDetailsTask;

/**
 * Created by ylin9 on 2016/7/6.
 */
public class PieChart {
    private static String title;
    private static final int[] colors = { Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED, Color.YELLOW };
    private static double[] distribution;

    public static void setPieChartData(ArrayList<String> result){
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

    public static void drawPie(){
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

        ViewGroup chartContainer = (ViewGroup) DownloadFeedDetailsTask.v1.findViewById(R.id.chart);
        if (MainActivity.mChartView != null){
            chartContainer.removeView(MainActivity.mChartView);
        }
        MainActivity.mChartView = ChartFactory.getPieChartView(MainActivity.mainContext, MainActivity.distributionSeries, MainActivity.defaultRenderer);
        // Adding the pie chart to the custom layout
        chartContainer.addView(MainActivity.mChartView);
    }

}
