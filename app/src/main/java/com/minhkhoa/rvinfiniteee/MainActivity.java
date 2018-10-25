package com.minhkhoa.rvinfiniteee;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.minhkhoa.rvinfiniteee.interf.OnLoadMoreListener;
import com.minhkhoa.rvinfiniteee.model.Cat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvCat;
    private ArrayList<Cat> catArrayList = new ArrayList<>();
    private CatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 1; i < 11; i++) {
            Cat cat = new Cat();
            cat.setName("Mèo " + i);
            catArrayList.add(cat);
        }

        rvCat = findViewById(R.id.rv);
        rvCat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CatAdapter(rvCat,catArrayList);
        rvCat.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                catArrayList.add(null); // add loading view
                adapter.notifyItemInserted(catArrayList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        catArrayList.remove(catArrayList.size() - 1);// remove loading view
                        adapter.notifyItemRemoved(catArrayList.size());
                        //Load data
                        int index = catArrayList.size();
                        int end = index + 5;
                        for (int i = index; i < end; i++) {
                            Cat cat = new Cat();
                            cat.setName("Mèo " + i);
                            catArrayList.add(cat);
                        }
                        adapter.notifyDataSetChanged();
                        adapter.setLoaded();
                    }
                }, 3000);
            }
        });
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    static class CatViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;

        public CatViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textView);
        }
    }

    class CatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener mOnLoadMoreListener;
        private ArrayList<Cat> catArrayList;

        public CatAdapter(RecyclerView mRecyclerView, ArrayList<Cat> catArrayList) {
            this.catArrayList = catArrayList;

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return catArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (i == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, viewGroup, false);
                return new CatViewHolder(view);
            } else if (i == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.loading, viewGroup, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof CatViewHolder) {
                Cat cat = catArrayList.get(i);
                CatViewHolder catViewHolder = (CatViewHolder) viewHolder;
                catViewHolder.txtName.setText(cat.getName());
            } else if (viewHolder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return catArrayList == null ? 0 : catArrayList.size();
        }

        public void setLoaded() {
            isLoading = false;
        }
    }
}
