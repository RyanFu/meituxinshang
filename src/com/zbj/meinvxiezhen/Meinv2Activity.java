package com.zbj.meinvxiezhen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.newxp.common.ExchangeConstants;
import com.umeng.newxp.controller.ExchangeDataService;
import com.umeng.newxp.view.ExchangeViewManager;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class Meinv2Activity extends Activity implements OnClickListener {


    private ImageButton ib_back;
    private ImageButton ib_share;
    private ImageButton ib_crop;
    private ImageButton ib_save;
    private ImageButton ib_forward;
    private ImageView iv;
    private ProgressBar pb;
    private Bitmap bm;
    private SharedPreferences sp;
    private String url;
    private int num = 1;
    private int title;
    private String animnum;
    private String img_url;
    private String savedir;
    private HttpURLConnection uc;
    private PhotoViewAttacher attacher;
    private Handler mHandler = new Handler() {
        Animation ivAnimation_in = null;

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    pb.setVisibility(View.GONE);
                    Animation ivAnimation = null;

                    switch (Integer.parseInt(animnum)) {
                        case 1:
                            ivAnimation = AnimationUtils.loadAnimation(
                                    Meinv2Activity.this, R.anim.anim_iv);
                            ivAnimation_in = AnimationUtils.loadAnimation(
                                    Meinv2Activity.this, R.anim.anim_iv_in);
                            break;
                        case 2:
                            ivAnimation = AnimationUtils.loadAnimation(
                                    Meinv2Activity.this, R.anim.anim_iv2);
                            ivAnimation_in = AnimationUtils.loadAnimation(
                                    Meinv2Activity.this, R.anim.anim_iv2_in);
                            break;
                        case 3:
                            ivAnimation = AnimationUtils.loadAnimation(
                                    Meinv2Activity.this, R.anim.anim_iv1);
                            ivAnimation_in = AnimationUtils.loadAnimation(
                                    Meinv2Activity.this, R.anim.anim_iv1_in);
                            break;
                    }
                    iv.startAnimation(ivAnimation);
                    ivAnimation.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // TODO Auto-generated method stub
                            if (bm == null)
                                return;
                            iv.setImageBitmap(bm);
                            iv.startAnimation(ivAnimation_in);
                            attacher.update();
                        }
                    });
                    break;
                case -1:
                    Toast.makeText(Meinv2Activity.this, getString(R.string.loadfail), Toast.LENGTH_SHORT)
                            .show();
                    pb.setVisibility(View.GONE);
                    break;
            }
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add(0, 1, 1, getString(R.string.prefs)).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, 2, 2, getString(R.string.ads));
        menu.add(0, 3, 3, getString(R.string.send)).setIcon(android.R.drawable.ic_menu_send);
        menu.add(0, 4, 4, getString(R.string.exit)).setIcon(
                android.R.drawable.ic_menu_close_clear_cancel);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(Meinv2Activity.this,
                        preferenceActivity.class);
                startActivity(intent);
                break;
            case 2:
                ExchangeDataService dataService = new ExchangeDataService();
                new ExchangeViewManager(this, dataService).addView(ExchangeConstants.type_list_curtain, null);
                break;
            case 3:
                FeedbackAgent agent = new FeedbackAgent(this);
                agent.startFeedbackActivity();
                break;
            case 4:
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meinv2);

        ib_back = (ImageButton) findViewById(R.id.back);
        ib_share = (ImageButton) findViewById(R.id.share);
        ib_crop = (ImageButton) findViewById(R.id.crop);
        ib_save = (ImageButton) findViewById(R.id.save);
        ib_forward = (ImageButton) findViewById(R.id.forward);
        iv = (ImageView) findViewById(R.id.meinv2img);
        pb = (ProgressBar) findViewById(R.id.pb2);

        ib_back.setOnClickListener(this);
        ib_share.setOnClickListener(this);
        ib_crop.setOnClickListener(this);
        ib_save.setOnClickListener(this);
        ib_forward.setOnClickListener(this);

        Intent intent = getIntent();
        url = intent.getStringExtra("img");
        getImage();

        attacher = new PhotoViewAttacher(iv);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        MobclickAgent.onResume(this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        savedir = sp.getString("folder", "MeinvXiezhen");
        animnum = sp.getString("anim", "1");


        File file = new File("/sdcard/" + savedir);
        if (!file.exists()) {
            file.mkdirs();
        }
        super.onResume();
    }

    private void getImage() {
        // TODO Auto-generated method stub
        title = Integer.parseInt(url.split("/")[4].split(".html")[0]);
        img_url = "http://img1.mm131.com/pic/" + title + "/" + num + ".jpg";
        pb.setVisibility(View.VISIBLE);
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    URL img = new URL(img_url);
                    uc = (HttpURLConnection) img.openConnection();
                    uc.setRequestProperty("Referer", url);
                    uc.connect();
                    InputStream is = uc.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                    is.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Message msg = new Message();
                    msg.what = -1;
                    mHandler.sendMessage(msg);
                }

                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        };
        th.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (pb.getVisibility() == View.VISIBLE) {
            pb.setVisibility(View.GONE);
            uc.disconnect();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:
                if (num <= 1) {
                    return;
                } else {
                    num--;
                    getImage();
                }
                break;
            case R.id.share:
                if (bm != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri
                            .parse(MediaStore.Images.Media.insertImage(
                                    getContentResolver(), bm, null, null)));
                    startActivity(Intent.createChooser(intent, getString(R.string.share)));
                }
                break;
            case R.id.crop:
                if (bm != null) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setData(Uri.parse(MediaStore.Images.Media.insertImage(
                            getContentResolver(), bm, null, null)));
                    intent.putExtra("crop", "true");
                    intent.putExtra("noFaceDetection", true);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.save:
                if (bm != null) {
                    File file = new File("/sdcard/" + savedir + "/" + title + "_"
                            + num + ".png");
                    try {
                        boolean isok = file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        if (isok) {
                            Toast.makeText(Meinv2Activity.this,
                                    String.format(getString(R.string.savepic), savedir),
                                    Toast.LENGTH_SHORT).show();
                            // btn_hidden.setVisibility(View.VISIBLE);
                        }
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.forward:
                num++;
                getImage();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                try {
                    setWallpaper(photo);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Toast.makeText(Meinv2Activity.this, getString(R.string.set_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}