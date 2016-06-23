package edu.illinois.ncsa.bwmon;

import android.os.AsyncTask;
import android.support.v4.view.ViewPager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class DownloadFeedsTask extends AsyncTask<String, Void, ArrayList<String>> {

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

    public DownloadFeedsTask() {

    }

    @Override
    protected ArrayList<String> doInBackground(String... urls) {
        try {
            return loadFromNetwork(urls[0]);
        } catch (IOException e) {
            return new ArrayList<String>();
        }
    }

    private void setHeader(String header){
        String[] subHeader = header.split(" ");
        MainActivity.datafeedsList.setList_version(subHeader[subHeader.length-1]);
        String temp = subHeader[0];
        temp = temp.concat(" ");
        for (int i = 1; i < subHeader.length-1; i++)
        {
            if (i < subHeader.length -2){
                temp = temp.concat(subHeader[i]);
                temp = temp.concat(" ");
            }
            else
            {
                temp = temp.concat(subHeader[i]);
            }
        }
        MainActivity.datafeedsList.setList_name(temp);
    }

    private Datafeed setFeed(String info)
    {
        Datafeed temp = new Datafeed();
        String[] temp_info = info.split(",");
        temp.setName(temp_info[0]);
        temp.setUrl(temp_info[1]);
        temp.setType(temp_info[2]);
        temp.setFields(temp_info);
        return temp;
    }

    /**
     * Uses the logging framework to display the output of the fetch
     * operation in the log fragment.
     */
    @Override
    protected void onPostExecute(ArrayList<String> results) {
        //Log.i(TAG, result);
        //fragment.setTitle(result, nPosition);
        setHeader(results.get(0));

        Datafeed[] list = new Datafeed[results.size()-1];
        for (int i = 1; i < results.size(); i++)
        {
            list[i-1] = new Datafeed(setFeed(results.get(i)));
        }

        MainActivity.datafeedsList.setDatafeed(list);
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
                new DownloadFeedDetailsTask(position).execute(MainActivity.datafeedsList.getDatafeedList()[position].getUrl());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        new DownloadFeedDetailsTask(0).execute(MainActivity.datafeedsList.getDatafeedList()[0].getUrl());
    }

}
