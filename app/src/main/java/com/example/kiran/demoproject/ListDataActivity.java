package com.example.kiran.demoproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.kiran.demoproject.Utils.Utils;
import com.example.kiran.demoproject.beans.BookPojo;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListDataActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerAdapter recyclerAdapter;
    RecyclerView rvData;
    ;
    private String TAG = ListDataActivity.class.getSimpleName();
    private List<BookPojo> mArrayList = new ArrayList<>();
    private FloatingActionButton fbAddData;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);
        intiViews();
    }

    private void callWebServices() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                , Utils.URL_BOOKS, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: " + response);
                try {
                    JSONArray dataArray = response.getJSONArray("data");
//                    Log.d(TAG, "onResponse: dataArray"+dataArray);
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject jsonObject = dataArray.getJSONObject(i);
                        BookPojo bookPojo = new BookPojo();
                        bookPojo.setmName(jsonObject.getString("name"));
                        bookPojo.setmEndDate(jsonObject.getString("endDate"));
                        bookPojo.setmIconUrl(jsonObject.getString("icon"));
                        mArrayList.add(bookPojo);
                        Log.d(TAG, "onResponse: " + bookPojo.getmName());
                    }
                    recyclerAdapter = new RecyclerAdapter(mArrayList);
                    rvData.setAdapter(recyclerAdapter);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: JSONException" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.getMessage());
            }
        });
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.add(jsonObjectRequest);
        requestQueue.start();
    }


    private void intiViews() {
        progressDialog = Utils.dialogLoading(ListDataActivity.this, getString(R.string.dialog_mainactivity_load));
        progressDialog.show();
        callWebServices();
        Log.d(TAG, "onResponse: OnCreate size" + mArrayList.size());
        rvData = (RecyclerView) findViewById(R.id.rv_data);
        fbAddData = (FloatingActionButton) findViewById(R.id.fab_listdata_add);
        rvData.setLayoutManager(new LinearLayoutManager(ListDataActivity.this));
        fbAddData.setOnClickListener(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(setupItemTouchHelper());
        itemTouchHelper.attachToRecyclerView(rvData);


    }

    private ItemTouchHelper.SimpleCallback setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListDataActivity.this);
                    builder.setMessage("Are you sure you want to delete");
                    builder.setPositiveButton(R.string.dialog_delete_yesbt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mArrayList.remove(position);
                            recyclerAdapter.notifyItemRemoved(position);
                            return;
                        }
                    }).setNegativeButton(R.string.dialog_delete_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            recyclerAdapter.notifyDataSetChanged();
                            rvData.scrollToPosition(position);
                            return;

                        }
                    }).show();

                }

            }
        };
        return simpleCallback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_listdata_add:
                addData();
                break;
        }
    }

    private void addData() {
        Log.e(TAG, "addData: size" + mArrayList.size());

        BookPojo bookPojo = new BookPojo();
        bookPojo.setmName("Text");
        bookPojo.setmEndDate("Apr 01,2017");
        bookPojo.setmIconUrl("https://s3.amazonaws.com/media.guidebook.com/service/B9MwGvYgZg5Mt6xSvETPiRoB0WDbfzJFrdEZSPW5/logo.png");
        mArrayList.add(0, bookPojo);
        Log.e(TAG, "addData: size" + mArrayList.size() + "" + mArrayList);
        recyclerAdapter.notifyDataSetChanged();
        rvData.smoothScrollToPosition(0);
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

        private List<BookPojo> alData;

        public RecyclerAdapter(List<BookPojo> mArrayList) {
            this.alData = mArrayList;
        }

        @Override
        public RecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_rowitem, parent, false);
            Log.d(TAG, "onCreateViewHolder: " + view);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.RecyclerViewHolder holder, int position) {
            BookPojo bookPojo = this.alData.get(position);
            holder.tvEndDate.setText(bookPojo.getmEndDate());
            holder.tvName.setText(bookPojo.getmName());
            Picasso.with(ListDataActivity.this).load(bookPojo.getmIconUrl()).into(holder.ivIcon);

        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount: " + this.alData.size());
            return this.alData.size();
        }

        class RecyclerViewHolder extends RecyclerView.ViewHolder {
            private TextView tvName;
            private TextView tvEndDate;
            private ImageView ivIcon;

            public RecyclerViewHolder(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.tv_name);
                tvEndDate = (TextView) itemView.findViewById(R.id.tv_enddate);
                ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            }
        }
    }

    class ImageDownloadManager extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public ImageDownloadManager(ImageView imageView) {
            super();
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String imageUrl = url[0];
            Bitmap icon = null;
            try {
                InputStream inputStream = new java.net.URL(imageUrl).openStream();
                icon = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return icon;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            this.imageView.setImageBitmap(bitmap);
        }
    }
}
