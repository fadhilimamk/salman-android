package com.salmanitb.alumnisalman.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.salmanitb.alumnisalman.R;
import com.salmanitb.alumnisalman.SalmanApplication;
import com.salmanitb.alumnisalman.adapter.PostAdapter;
import com.salmanitb.alumnisalman.helper.APIConnector;
import com.salmanitb.alumnisalman.helper.WebService;
import com.salmanitb.alumnisalman.model.Post;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReadPostActivity extends AppCompatActivity {

    @BindView(R.id.my_toolbar)
    Toolbar toolbar;

    @BindView(R.id.news_title)
    TextView newsTitle;

    @BindView(R.id.news_time)
    TextView newsTime;

    @BindView(R.id.news_view_count)
    TextView newsViewCount;

    @BindView(R.id.news_like_count)
    TextView newsLikeCount;

    @BindView(R.id.webview)
    WebView webView;

    @BindView(R.id.main_image)
    ImageView mainImage;

    @BindView(R.id.like_btn)
    ImageButton imgLove;

    Post post;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_post);
        ButterKnife.bind(this);

        progressDialog = ProgressDialog.show(this, "Loading", "Sedang memuat konten ...", true);

        webView.setVisibility(View.GONE);
        imgLove.setVisibility(View.GONE);
        prepareWebView();

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            String id = data.getQueryParameter("q");
            loadSalmanMenyapaDetail(Integer.valueOf(id));
            return;
        }

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        post = (Post) intent.getSerializableExtra("POST");

        prepareLoveButton();
        loadPost(post);

    }

    private void prepareWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressDialog.show();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressDialog.dismiss();
                Toast.makeText(ReadPostActivity.this, "Gagal memuat konten", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prepareLoveButton() {
        APIConnector.getInstance().getSalmanMenyapaDetail(post.getId(), SalmanApplication.getCurrentUserAuth().getId(), new APIConnector.ApiCallback<Post>() {
            @Override
            public void onSuccess(Post response) {
                post.setLikedByMe(response.isLikedByMe());
                showLoveButton();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(ReadPostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        imgLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLoveStatus();
            }
        });
    }

    private void loadPost(Post post) {
        newsTitle.setText(post.getTitle());
        Picasso.get().load(post.getImageURL()).fit().centerCrop().into(mainImage);
        newsTime.setText(PostAdapter.decodeUnixTime(post.getCreatedAt()));

        String txtLike = String.valueOf(post.getLoveCount()) + " suka";
        String txtView = String.valueOf(post.getViewCount()) + " tayang";
        newsLikeCount.setText(txtLike);
        newsViewCount.setText(txtView);

        webView.loadUrl(post.getContentURL());
    }


    private void showLoveButton() {
        imgLove.setVisibility(View.VISIBLE);
        if (post.isLikedByMe()) {
            imgLove.setImageResource(R.drawable.ic_love_active);
        } else {
            imgLove.setImageResource(R.drawable.ic_love);
        }
    }

    private void changeLoveStatus() {
        APIConnector.getInstance().doLoveSalmanMenyapa(post.getId(), SalmanApplication.getCurrentUserAuth().getId(), new APIConnector.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void voids) {
                post.setLikedByMe(!post.isLikedByMe());
                showLoveButton();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(ReadPostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.fab_share)
    protected void shareArticle() {
        StringBuilder sb = new StringBuilder();
        sb.append(post.getTitle());
        sb.append("\n\n");
        sb.append(post.getShortContent());
        sb.append("\n\nSelengkapnya: ");
        sb.append(post.getContentURL());

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(shareIntent, "Bagikan artikel ke media sosial"));
    }

    private void loadSalmanMenyapaDetail(int id) {
        APIConnector.getInstance().getSalmanMenyapaDetail(id, 1, new APIConnector.ApiCallback<Post>() {
            @Override
            public void onSuccess(Post response) {
                progressDialog.dismiss();
                loadPost(response);
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ReadPostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
