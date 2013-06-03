package com.zbj.meinvxiezhen;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zbj.meinvxiezhen.utils.UrlInfo;

public class SortlistActivity extends Activity implements OnClickListener {

    private ListView lv;
    private ProgressBar pb;
    private Button btn_previous;
    private Button btn_next;
    private TextView tv_pagenum;

    private String urlstr;
    private String html;
    private String tempstr;
    private int flag;
    private int pagenum = 1;

    private HttpURLConnection huc;

    private List<UrlInfo> list;
    private List<UrlInfo> tmp;

    private MyAdapter adapter;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    lv.setAdapter(adapter);
                    list.clear();
                    for (int i = 0; i < tmp.size(); i++) {
                        list.add(tmp.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    pb.setVisibility(View.GONE);
                    tv_pagenum.setText(String.format(getString(R.string.numpage),pagenum));
                    break;
                case -1:
                    Toast.makeText(SortlistActivity.this, getString(R.string.loadfail),
                            Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    tv_pagenum.setText(String.format(getString(R.string.numpage),--pagenum));
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sortlist);

        adapter = new MyAdapter();


        lv = (ListView) findViewById(R.id.sortlistview);
        pb = (ProgressBar) findViewById(R.id.pb);
        btn_previous = (Button) findViewById(R.id.pre_page);
        btn_next = (Button) findViewById(R.id.next_page);
        tv_pagenum = (TextView) findViewById(R.id.pagenum);


        btn_next.setOnClickListener(this);
        btn_previous.setOnClickListener(this);

        list = new ArrayList<UrlInfo>();
        tmp = new ArrayList<UrlInfo>();

        Intent intent = getIntent();
        urlstr = intent.getStringExtra("url");
        tempstr = urlstr;
        flag = intent.getIntExtra("flag", -1);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(SortlistActivity.this,
                        Meinv2Activity.class);
                intent.putExtra("img", list.get(arg2).getUrl());
                startActivity(intent);
            }
        });

        getList();
    }

    private void getList() {
        // TODO Auto-generated method stub
        pb.setVisibility(View.VISIBLE);
        tmp.clear();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    URL url = new URL(tempstr);
                    huc = (HttpURLConnection) url.openConnection();
                    huc.connect();
                    InputStream is = huc.getInputStream();
                    StringBuffer out = new StringBuffer();
                    InputStreamReader inread = new InputStreamReader(is,
                            "gb2312");

                    char[] b = new char[4096];
                    for (int n; (n = inread.read(b)) != -1; ) {
                        out.append(new String(b, 0, n));
                    }

                    html = out.toString();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = -1;
                    mHandler.sendMessage(msg);
                }
                String useful = null;
                // Log.d("meinv2", html);
                try {
                    useful = html.split("<dl class=\"list-left public-box\">")[1]
                            .split("<div class=\"main-right\">")[0];
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    Message msg = new Message();
                    msg.what = -1;
                    mHandler.sendMessage(msg);
                    return;
                }
                String[] items = useful.split("<dd><a target=\"_blank\"");
                for (int i = 0; i < 20; i++) {
                    try {
                        String img_url = items[i].split("ef=\"")[1]
                                .split("\"><img src")[0];
                        String img_name = items[i].split("alt=\"")[1]
                                .split("\" width=\"120")[0];
                        Log.d("meinv2", img_name);
                        UrlInfo ui = new UrlInfo();
                        ui.setName(img_name);
                        ui.setUrl(img_url);
                        tmp.add(ui);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        continue;
                    }
                }

                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            // TODO Auto-generated method stub
            TextView tv = new TextView(SortlistActivity.this);
            tv.setText(list.get(position).getName());
            tv.setTextSize(20);
            tv.setSingleLine(true);
            tv.setEllipsize(TruncateAt.MARQUEE);
            tv.setPadding(5, 10, 5, 10);
            tv.setTextColor(Color.BLACK);
            // if (position % 2 == 0) {
            // tv.setBackgroundColor(Color.LTGRAY);
            // } else {
            // tv.setBackgroundColor(Color.WHITE);
            // }
            return tv;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (pb.getVisibility() == View.VISIBLE) {
                pb.setVisibility(View.GONE);
                huc.disconnect();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.pre_page:
                if (pagenum <= 1) {
                    return;
                } else {
                    pagenum--;
                    tempstr = urlstr + "list_" + flag + "_" + pagenum + ".html";
                    getList();
                }
                break;
            case R.id.next_page:
                pagenum++;
                tempstr = urlstr + "list_" + flag + "_" + pagenum + ".html";
                getList();
                break;
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
