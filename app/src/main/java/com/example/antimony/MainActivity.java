package com.example.antimony;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    String baseurl = "http://79.127.126.110/Serial/";
    Document doc;
    int choice;
    ListView list;
    SwipeRefreshLayout pulltoRefresh;

    class JLoad extends AsyncTask<String, Void, ArrayList> {
        int size;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pulltoRefresh.setRefreshing(true);
        }

        @Override
        protected Elements doInBackground(String... strings) {
            try {
                doc = Jsoup.connect(baseurl).get();
                Elements atags = doc.getElementsByTag("a");
                for (Element a : atags) {
                    listItems.add(a.text().replace("/", ""));
                }
                return atags;

            } catch (IOException e) {
                e.printStackTrace();
                doc = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList s) {
            super.onPostExecute(s);

            adapter.notifyDataSetChanged();
            this.cancel(true);
            pulltoRefresh.setRefreshing(false);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Find Views
        list = findViewById(R.id.list);
        pulltoRefresh = findViewById(R.id.pullToRefresh);
        listItems = new ArrayList<>();
        final JLoad jLoad = new JLoad();


        jLoad.execute("");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        list.setAdapter(adapter);
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choice = i;
                String item = listItems.get(i);
                if (item.endsWith(".mkv") || item.endsWith(".mp4")) {
                    intent.setDataAndType(Uri.parse(baseurl + "/" + item), "video/*");
                    startActivity(Intent.createChooser(intent, "Complete action using"));
                } else
                    updateitems(i);
            }
        });
        pulltoRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                baseurl = "http://79.127.126.110/Serial/";
                listItems.clear();
                JLoad jLoad = new JLoad();
                jLoad.execute("");
                adapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, listItems);
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                pulltoRefresh.setRefreshing(false);
            }
        });


    }


    void updateitems(int i) {
        String changeitem = listItems.get(i);
        changeitem = changeitem.replace(" ", "%20");
        baseurl += "/" + changeitem;
        JLoad loadagain = new JLoad();
        listItems.clear();
        loadagain.execute("");
    }

    @Override
    public void onBackPressed() {
        updateitems(0);

    }
}

