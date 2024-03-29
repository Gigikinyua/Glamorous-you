package com.jemimah.glamorous_you.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.adapter.BusinessesRVAdapter;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.model.Service;
import com.jemimah.glamorous_you.retrofit.RetrofitApiClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "CategoryActivity";
    private static final Boolean HIDE_MENU = false;

    private Toolbar toolbar;
    private Boolean mState = true;
    //    private SearchView searchView;
    private LinearLayout noItemsLayout;
    private RecyclerView rvBusinesses;
    private BusinessesRVAdapter businessesRVAdapter;
    private ShimmerFrameLayout mShimmerViewContainer;

    List<Business> businesses;
    Service activeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        initViews();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(activeService.getName() + "s");

        runOnUiThread(this::getBusinesses);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        noItemsLayout = findViewById(R.id.noItemsLayout);

        rvBusinesses = findViewById(R.id.rvBusinesses);

        //get extras
        Bundle bundle = getIntent().getExtras();
        activeService = (Service) bundle.getSerializable("category");
    }

    private void getBusinesses() {
        RetrofitApiClient.getInstance(CategoryActivity.this)
                .getApi()
                .getBusinesses(activeService.getId())
                .enqueue(new Callback<List<Business>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Business>> call, @NotNull Response<List<Business>> response) {
                        if (response.code() == 200) {
                            businesses = new ArrayList<>();
                            businesses = response.body();

                            if (businesses.isEmpty()) {
                                // stop animating Shimmer and hide the layout
                                mShimmerViewContainer.stopShimmerAnimation();
                                mShimmerViewContainer.setVisibility(View.GONE);

                                noItemsLayout.setVisibility(View.VISIBLE);
                                rvBusinesses.setVisibility(View.GONE);

                                mState = HIDE_MENU; // setting state
                                invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again

                            } else {
                                noItemsLayout.setVisibility(View.GONE);
                                rvBusinesses.setVisibility(View.VISIBLE);

                                rvBusinesses.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
                                rvBusinesses.setHasFixedSize(true);

                                businessesRVAdapter = new BusinessesRVAdapter(CategoryActivity.this);
                                rvBusinesses.setAdapter(businessesRVAdapter);
                                businessesRVAdapter.setBusinessesList(businesses);

                                // stop animating Shimmer and hide the layout
                                mShimmerViewContainer.stopShimmerAnimation();
                                mShimmerViewContainer.setVisibility(View.GONE);
                            }
                        } else {
                            runOnUiThread(() -> new SweetAlertDialog(CategoryActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                    .setConfirmText(getResources().getString(R.string.okay))
                                    .show());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Business>> call, @NotNull Throwable t) {
                        Log.e(TAG, "onFailure: Could not retrieve data. ", t);
                        runOnUiThread(() -> new SweetAlertDialog(CategoryActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.error_title))
                                .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                .setConfirmText(getResources().getString(R.string.okay))
                                .show());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Business");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        searchView.setBackground(ResourcesCompat.getDrawable(getResources(),
                R.drawable.pink_rounded, getTheme()));

        if (mState == HIDE_MENU) {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //******************************************************************************
    //               Search View Query Listener
    //*****************************************************************************

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        businessesRVAdapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}