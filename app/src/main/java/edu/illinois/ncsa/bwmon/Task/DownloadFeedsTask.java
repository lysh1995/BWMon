package edu.illinois.ncsa.bwmon.Task;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import edu.illinois.ncsa.bwmon.DataModel.Datafeed;
import edu.illinois.ncsa.bwmon.FeedsListUpdateReceiver;
import edu.illinois.ncsa.bwmon.FeedsSelectActivity;
import edu.illinois.ncsa.bwmon.MainActivity;

public class DownloadFeedsTask extends AsyncTask<String, Void, ArrayList<String>> {
    private String curr_version = "";

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
        curr_version = subHeader[subHeader.length-1];
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

    private void create_display_button()
    {
        Button display = new Button(FeedsSelectActivity.feedsSelectContext);
        display.setText("Display");
        FeedsSelectActivity.checkList.addView(display);
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Datafeed> selected = new ArrayList<Datafeed>();
                int length = FeedsSelectActivity.nameList.length;
                for (int i = 0; i < length; i++){
                    if (FeedsSelectActivity.checkBoxes[i].isChecked())
                    {
                        selected.add(FeedsSelectActivity.datafeedsList.getDatafeedList()[i]);
                    }
                }
                FeedsSelectActivity.selectedList = new Datafeed[selected.size()];
                for (int i = 0; i < selected.size(); i++)
                {
                    FeedsSelectActivity.selectedList[i] = selected.get(i);
                }
                Intent intent = new Intent(FeedsSelectActivity.feedsSelectContext, MainActivity.class);
                FeedsSelectActivity.feedsSelectContext.startActivity(intent);
            }
        });
    }

    private void sendNotification(){
        Intent intent = new Intent(FeedsSelectActivity.feedsSelectContext, FeedsListUpdateReceiver.class);
        FeedsSelectActivity.feedsSelectContext.sendBroadcast(intent);
    }

    /**
     * Uses the logging framework to display the output of the fetch
     * operation in the log fragment.
     */
    @Override
    protected void onPostExecute(ArrayList<String> results) {
        //Log.i(TAG, result);
        //fragment.setTitle(result, nPosition);
        FeedsSelectActivity.swipeContainer.setRefreshing(false);
        setHeader(results.get(0));
        if (curr_version.equals(FeedsSelectActivity.version))
            return;
        FeedsSelectActivity.version = curr_version;
        Datafeed[] list = new Datafeed[results.size()-1];
        for (int i = 1; i < results.size(); i++)
        {
            list[i-1] = new Datafeed(setFeed(results.get(i)));
        }

        FeedsSelectActivity.datafeedsList.setDatafeed(list);
        FeedsSelectActivity.nameList = FeedsSelectActivity.datafeedsList.getNameList();
        String[] nameList = FeedsSelectActivity.nameList;
        int length = FeedsSelectActivity.nameList.length;
        FeedsSelectActivity.checkBoxes = new CheckBox[length];
        for (int i = 0; i < length; i++){
            CheckBox checkBox = new CheckBox(FeedsSelectActivity.feedsSelectContext);
            checkBox.setText(nameList[i]);
            FeedsSelectActivity.checkList.addView(checkBox);
            FeedsSelectActivity.checkBoxes[i] = checkBox;
        }
        create_display_button();
        sendNotification();
    }

}
