package edu.illinois.ncsa.bwmon.DataModel;

/**
 * Created by ylin9 on 2016/6/21.
 */
public class Datafeed {
    String name;
    String url;
    String type;
    String[] fields;
    int select = 0;

    public Datafeed()
    {
        this.name = "";
        this.url = "";
        this.type = "";
        String[] fields = null;
    }

    public Datafeed(Datafeed df){
        this.name = df.getName();
        this.url = df.getUrl();
        this.type = df.getType();
        this.fields = new String[df.fields.length];
        for (int i = 0; i < this.fields.length; i++){
            this.fields[i] = df.getFields()[i];
        }
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getType(){
        return this.type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String[] getFields(){
        return this.fields;
    }


    public void setFields(String[] fields){
        this.fields = new String[fields.length];
        for (int i = 3; i < this.fields.length; i++){
            this.fields[i-3] = fields[i];
        }
    }


}
