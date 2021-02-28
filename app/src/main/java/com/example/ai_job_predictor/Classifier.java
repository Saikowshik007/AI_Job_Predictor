package com.example.ai_job_predictor;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.AsyncListUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Classifier {
    private Context context;
    private String filename;
    private static DataCallback callback;
    private int maxlen;
    private HashMap<String, Integer> vocabData;

    public Classifier(Context context, String Jfilename) {
        this.context = context;
        this.filename = Jfilename;
    }

    public void loadData() {
        LoadVocabularyTask loadVocabulary = new LoadVocabularyTask(callback);
        loadVocabulary.execute(loadJSONFromAsset(filename));
    }

    private String loadJSONFromAsset(String filename) {
        String json;
        try {
            InputStream inputStream = context.getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public void setCallback(DataCallback callback) {
        this.callback = callback;
    }

    ArrayList<Integer> tokenize(String message) {
        String[] parts = message.split(" ");
        ArrayList<Integer> tokenizedMessage = new ArrayList();
        int index = 0;
        for (String part : parts) {
            if (part.trim() != "") {
                if (vocabData.get(part) == null) {
                    index = 0;
                } else {
                  index = vocabData.get(part);
                }
            }
            tokenizedMessage.add(index);


        }
        //Toast.makeText(context,"",Toast.LENGTH_LONG).show();
        return tokenizedMessage;
    }
    public ArrayList<Integer> padSequence(List<Integer>sequence){
        sequence.removeAll(Collections.singleton(0));
        if (sequence.size()>maxlen){
            ArrayList<Integer> array=new ArrayList<>(sequence.subList(0,maxlen));
            //Toast.makeText(context,"if"+array.size(),Toast.LENGTH_LONG).show();
            return array;

        }
        else if (sequence.size()<maxlen){
            for (int i=sequence.size();i<maxlen;i++){
                sequence.add(0);
            }
           // Toast.makeText(context,"else"+sequence.size(),Toast.LENGTH_LONG).show();
            return (ArrayList<Integer>) sequence;
        }
        else{return (ArrayList<Integer>) sequence;}


    }
    public void setVocab(HashMap<String,Integer>data){
        this.vocabData=data;
    }
    public void setMaxlength(int maxlen){this.maxlen=maxlen;}


    interface DataCallback{
        void onDataProcessed(HashMap<String, Integer> result); }
    private class LoadVocabularyTask extends AsyncTask<String, Void, HashMap<String, Integer>> {
        private DataCallback callback;
        public LoadVocabularyTask(DataCallback callback) {
            this.callback= (DataCallback) callback;
        }

        @Override
        protected HashMap<String, Integer> doInBackground(String... strings) {
            JSONObject jsonObject= null;
            try {
                jsonObject = new JSONObject(strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Iterator<String> iterator=jsonObject.keys();
            HashMap<String,Integer>data=new HashMap<>();
            while(iterator.hasNext()){
                String key = iterator.next();
                try {
                    data.put(key,(Integer)jsonObject.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return data;
        }
        @Override
        protected void onPostExecute(HashMap<String,Integer>result){
            super.onPostExecute(result);
            callback.onDataProcessed(result);
        }
    }

}
