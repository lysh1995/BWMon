package edu.illinois.ncsa.bwmon;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import edu.illinois.ncsa.bwmon.DataModel.DataFeedsList;
import edu.illinois.ncsa.bwmon.DataModel.Datafeed;
import edu.illinois.ncsa.bwmon.Task.DownloadFeedsTask;

public class FeedsSelectActivity extends AppCompatActivity {
    public static SwipeRefreshLayout swipeContainer;
    public static DataFeedsList datafeedsList = new DataFeedsList();
    public static String[] nameList;
    public static Datafeed[] selectedList;
    public static LinearLayout checkList;
    public static Context feedsSelectContext;
    public static CheckBox[] checkBoxes;
    private Handler handler;
    private HandlerThread hThread;
    public long timer = 10 * 60 * 1000;
    public static String version = "";

    private void setUpdate(){
        hThread = new HandlerThread("HandlerThread");
        hThread.start();
        handler = new Handler(hThread.getLooper());
        Runnable eachMinute = new Runnable() {
            @Override
            public void run() {
                System.out.println("ten minute");
                new DownloadFeedsTask().execute("http://isce.ncsa.illinois.edu/bwmon/datafeeds.html");
                handler.postDelayed(this, timer);
            }
        };
        handler.postDelayed(eachMinute, timer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_select);
        checkList = (LinearLayout) findViewById(R.id.checkList);
        checkList.removeAllViews();
        feedsSelectContext = this;
        new DownloadFeedsTask().execute("http://isce.ncsa.illinois.edu/bwmon/datafeeds.html");
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                FeedsSelectActivity.checkList.removeAllViews();
                FeedsSelectActivity.version = "";
                new DownloadFeedsTask().execute("http://isce.ncsa.illinois.edu/bwmon/datafeeds.html");
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        setUpdate();

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        hThread.quit();
    }
}
