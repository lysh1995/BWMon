package edu.illinois.ncsa.bwmon;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import edu.illinois.ncsa.bwmon.Adapter.SectionsPagerAdapter;
import edu.illinois.ncsa.bwmon.DataModel.DataFeedsList;
import edu.illinois.ncsa.bwmon.Task.DownloadFeedDetailsTask;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    public static SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public static ViewPager mViewPager;
    public static TabLayout tabLayout;
    public static Toolbar toolbar;
    public static Context mainContext;
    public static int current_position;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public static DataFeedsList datafeedsList = new DataFeedsList();
    public static CategorySeries distributionSeries;
    public static int drawPie = 0;
    public static GraphicalView mChartView;
    public static DefaultRenderer defaultRenderer;

    private  void setView()
    {
        MainActivity.datafeedsList.setDatafeed(FeedsSelectActivity.selectedList);
        MainActivity.mSectionsPagerAdapter.setPageTitle(MainActivity.datafeedsList.getNameList());
        MainActivity.mSectionsPagerAdapter.setCount(MainActivity.datafeedsList.getNameList().length);
        // Set up the ViewPager with the sections adapter.
        MainActivity.mViewPager.setAdapter(MainActivity.mSectionsPagerAdapter);
        MainActivity.tabLayout.setupWithViewPager(MainActivity.mViewPager);
        MainActivity.mViewPager.setOffscreenPageLimit(3);
        MainActivity.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MainActivity.current_position = position;
                new DownloadFeedDetailsTask(position).execute(MainActivity.datafeedsList.getDatafeedList()[position].getUrl());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (FeedsSelectActivity.selectedList.length > 0)
            new DownloadFeedDetailsTask(0).execute(MainActivity.datafeedsList.getDatafeedList()[0].getUrl());
        else
        {
            Toast.makeText(MainActivity.mainContext,"Select at least one feed to display details", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawPie = 0;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mainContext = this;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        setView();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.illinois.ncsa.bwmon/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.illinois.ncsa.bwmon/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_action_update) {
            new DownloadFeedDetailsTask(current_position).execute(MainActivity.datafeedsList.getDatafeedList()[current_position].getUrl());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}