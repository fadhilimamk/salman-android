package com.salmanitb.alumnisalman;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.TextView;

public class ReadPostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView datetime, headline, content;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_post);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra("POST");

        datetime = (TextView) findViewById(R.id.datetime);
        headline = (TextView) findViewById(R.id.headline);
        content = (TextView) findViewById(R.id.content);
        webView = (WebView) findViewById(R.id.webview_image);

        datetime.setText(post.getDatetime());
        headline.setText(post.getHeadline());
        content.setText(post.getContent());
        webView.loadUrl(post.getImageLocation());
    }
}
