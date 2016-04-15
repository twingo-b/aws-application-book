package jp.co.app.aws;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class SampleDetailActivity extends AppCompatActivity {

    private SampleDetailType detailData;
    private long ITEM_ID;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // SwipeRefreshLayoutの設定
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setRefreshing(true);

        Intent intent = getIntent();
        ITEM_ID = intent.getLongExtra("ItemID", 0);

        sendRequest();
    }

    private void sendRequest(){
        SampleApplication.getInstance().addToRequestQueue(new SampleDetailRequest(ITEM_ID, new SampleDetailRequest.SampleDetailRequestResponseListener() {
            @Override
            public void onResponse(SampleDetailType response) {
                mSwipeRefreshLayout.setRefreshing(false);
                detailData = response;
                viewDetail();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                //エラー処理省略
            }
        }));
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            sendRequest();
        }
    };

    private void viewDetail(){
        ((TextView)findViewById(R.id.title)).setText(detailData.Item.Name);

        NetworkImageView view = (NetworkImageView) findViewById(R.id.network_image_view);
        view.setImageUrl(detailData.Item.ImageUrl, new ImageLoader(SampleApplication.getInstance().getRequestQueue(), new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {

            }
        }));

        ((TextView)findViewById(R.id.Description)).setText(detailData.Item.Description);
        ((TextView)findViewById(R.id.Price)).setText(String.valueOf(detailData.Item.Price));

        findViewById(R.id.url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(detailData.Item.Url));
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
