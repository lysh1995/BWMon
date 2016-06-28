package edu.illinois.ncsa.bwmon;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import edu.illinois.ncsa.bwmon.DataModel.DataFeedsList;
import edu.illinois.ncsa.bwmon.DataModel.Datafeed;
import edu.illinois.ncsa.bwmon.Task.DownloadFeedsTask;

public class FeedsSelectActivity extends AppCompatActivity {
    public static DataFeedsList datafeedsList = new DataFeedsList();
    public static String[] nameList;
    public static Datafeed[] selectedList;
    public static LinearLayout checkList;
    public static Context feedsSelectContext;
    public static CheckBox[] checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_select);
        checkList = (LinearLayout) findViewById(R.id.checkList);
        checkList.removeAllViews();
        feedsSelectContext = this;
        new DownloadFeedsTask().execute("http://isce.ncsa.illinois.edu/bwmon/datafeeds.html");
    }
}
