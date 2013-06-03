package com.zbj.meinvxiezhen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;


public class CatalogActivity extends Activity implements DialogInterface.OnClickListener {
    private GridView gv;
    private int[] res = new int[]{R.drawable.m1056,
            R.drawable.m1119, R.drawable.m1127, R.drawable.m1134,
            R.drawable.m1136};
    private String[] txts;
    private String[] urls = new String[]{"http://www.mm131.com/qingchun/", "http://www.mm131.com/xiaohua/", "http://www.mm131.com/chemo/",
            "http://www.mm131.com/qipao/", "http://www.mm131.com/mingxing/"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        UmengUpdateAgent.update(this);
        setContentView(R.layout.catalog);

       txts = new String[]{getString(R.string.qingchun), getString(R.string.xiaohua), getString(R.string.chemo),
                getString(R.string.qipao), getString(R.string.mingxing)};

        gv = (GridView) findViewById(R.id.gridview);
        gv.setAdapter(new BaseAdapter() {
            @Override
            public View getView(int position, View v, ViewGroup parent) {
                // TODO Auto-generated method stub
                v = LayoutInflater.from(CatalogActivity.this).inflate(
                        R.layout.catalog_item, null);
                ImageView iv = (ImageView) v.findViewById(R.id.item_iv);
                TextView tv = (TextView) v.findViewById(R.id.item_tv);
                iv.setImageResource(res[position]);
                tv.setText(txts[position]);
                return v;
            }

            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return position;
            }

            @Override
            public Object getItem(int position) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return 5;
            }
        });

        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(CatalogActivity.this, SortlistActivity.class);
                intent.putExtra("url", urls[arg2]);
                intent.putExtra("flag", arg2+1);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(CatalogActivity.this).setTitle(getString(R.string.exit_promt))
                    .setPositiveButton(getString(R.string.no), this)
                    .setNegativeButton(getString(R.string.ok), null).create().show();
        }
        return false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub
        System.exit(0);
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
