package edu.illinois.ncsa.bwmon;

/**
 * Created by ylin9 on 2016/6/21.
 */
public class DataFeedsList {
    String List_name;
    String List_version;
    Datafeed[] list;

    public DataFeedsList(){
        this.List_version = "0.0";
        this.List_name = "";
        this.list = null;
    }

    public void setList_name(String List_name){
        this.List_name = List_name;
    }

    public String getList_name(){
        return this.List_name;
    }

    public void setList_version(String List_version){
        this.List_version = List_version;
    }

    public String getList_version(){return this.List_version;}

    public Datafeed[] getDatafeedList(){return this.list;}

    public void setDatafeed(Datafeed[] datafeed){
        this.list = new Datafeed[datafeed.length];
        for (int i = 0; i < this.list.length; i++)
        {
            this.list[i] = new Datafeed(datafeed[i]);
        }
    }

    public String[] getNameList(){
        String[] name_list = new String[list.length];
        for (int i = 0; i < list.length; i++)
        {
            name_list[i] = list[i].getName();
        }
        return name_list;
    }

}
