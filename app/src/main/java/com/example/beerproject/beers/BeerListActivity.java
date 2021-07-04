package com.example.beerproject.beers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beerproject.R;
import com.example.beerproject.beerDAO.Beer;
import com.example.beerproject.beerDAO.BeerViewModel;
import com.example.beerproject.beerDAO.Ingredient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class BeerListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BeerViewModel beerViewModel;
    public static final String KEY_EXTRA_BEER_ID = "BeerId";
    public static final int NEW_BEER_ACTIVITY_REQUEST_CODE = 1;
    private BeerAdapter beerAdapter;
    HashMap<String,Bitmap>mMemoryCache;


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.beer_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String beerName){
                beerAdapter.getFilter().filter(beerName);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String beerName){
                beerAdapter.getFilter().filter(beerName);
                return false;
            }
        });

        MenuItem sortRate = menu.findItem(R.id.menu_item_sort_rate);
        sortRate.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                beerAdapter.sortByRate(sortRate);
                return false;
            }
        });

        MenuItem sortName = menu.findItem(R.id.menu_item_sort_name);
        sortName.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                beerAdapter.sortByName(sortName);
                return false;
            }
        });

        MenuItem clear = menu.findItem(R.id.menu_item_clear);
        clear.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                beerAdapter.getFilter().filter("");
                beerAdapter.setNameAsc(true);
                beerAdapter.sortByName(sortName);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beers_list);
        beerViewModel = ViewModelProviders.of(this).get(BeerViewModel.class);
        Integer beerId = (Integer) getIntent().getSerializableExtra(BeerListActivity.KEY_EXTRA_BEER_ID);
        if(beerId != null){
            beerViewModel.findBeerWithId(beerId).observe(this, new Observer<Beer>() {
                @Override
                public void onChanged(Beer beer) {
                    beerViewModel.delete(beer);
                }
            });

        }
        mMemoryCache = new HashMap<>();
        recyclerView = findViewById(R.id.beer_recycler_view);
        beerAdapter = new BeerAdapter();

        beerViewModel.findAll().observe(this, new Observer<List<Beer>>() {
            @Override
            public void onChanged(List<Beer> beers) {
                beerAdapter.setBeers(beers);
            }
        });
        recyclerView.setAdapter(beerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BeerListActivity.this, BeerAddActivity.class);
                startActivityForResult(intent,NEW_BEER_ACTIVITY_REQUEST_CODE);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_BEER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            String name = data.getStringExtra(BeerAddActivity.NEW_BEER_NAME);
            Float time = data.getFloatExtra(BeerAddActivity.NEW_BEER_TIME,0.0F);
            Float temeprature = data.getFloatExtra(BeerAddActivity.NEW_BEER_TEMPERATURE, 0.0F);
            String beerStyle = data.getStringExtra(BeerAddActivity.NEW_BEER_STYLE);
            Bundle extra = data.getBundleExtra(BeerAddActivity.NEW_BEER_LIST_INGREDIENTS);
            ArrayList<Ingredient> objects = (ArrayList<Ingredient>) extra.getSerializable(BeerAddActivity.NEW_BEER_INGREDIENTS);
            beerViewModel.insert(new Beer(name,objects,time,temeprature,beerStyle));

            Snackbar.make(this.findViewById(R.id.coordinator_layout), getString(R.string.beer_added), Snackbar.LENGTH_LONG).show();
        }
        else{
            Snackbar.make(this.findViewById(R.id.coordinator_layout), getString(R.string.beer_not_added), Snackbar.LENGTH_LONG).show();
        }

    }

    private class BeerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private ImageView imageBeer;
        private Beer beer;

        public BeerHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.beers_list_fragment,parent,false));
            itemView.setOnClickListener(this);
            nameTextView = itemView.findViewById(R.id.beer_item_name);
            imageBeer = itemView.findViewById(R.id.beer_item_image);

        }

        public void bind(Beer beer)
        {
            this.beer = beer;
            nameTextView.setText(this.beer.getName());
            if(beer.getPhotoPath()!=null){
                Integer id = beer.getId();
                String TAG = String.valueOf(id);
                imageBeer.setTag(TAG);
                Bitmap bitmap = getBitmapFromMemCache(TAG);
                if(bitmap==null) {
                    BitmapLoadTask task = new BitmapLoadTask(imageBeer, TAG);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,beer.getPhotoPath());
                }
                else {
                    imageBeer.setImageBitmap(bitmap);
                }

            }
        }



        @Override
        public void onClick(View v) {
            Intent intent = new Intent(BeerListActivity.this, BeerReviewActivity.class);
            intent.putExtra(KEY_EXTRA_BEER_ID, beer.getId());
            startActivity(intent);
        }
    }

    private class BeerAdapter extends RecyclerView.Adapter<BeerHolder> implements Filterable {
        List<Beer> beers;
        List<Beer> filteredBeers;

        boolean rateAsc;
        boolean nameAsc;

        void setBeers(List<Beer> beers) {
            this.beers = beers;
            this.filteredBeers = beers;
            rateAsc = true;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public BeerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BeerHolder(getLayoutInflater(), parent);

        }

        @Override
        public void onBindViewHolder(@NonNull BeerHolder holder, int position) {

            if (filteredBeers != null) {
                Beer beer = filteredBeers.get(position);
                holder.setIsRecyclable(false);
                if (position % 2 == 0)
                    holder.itemView.setBackgroundColor(getResources().getColor(R.color.itemColor));
                holder.bind(beer);

            } else {
                Log.d("MainActivity", "No beers");
            }
        }

        @Override
        public int getItemCount() {
            if (filteredBeers != null) {
                return filteredBeers.size();
            } else {
                return 0;
            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        filteredBeers = beers;
                    } else {
                        List<Beer> filteredList = new ArrayList<>();
                        for (Beer row : beers) {
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        filteredBeers = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredBeers;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filteredBeers = (ArrayList<Beer>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }


        @RequiresApi(api = Build.VERSION_CODES.N)
        public void sortByRate(MenuItem sortRate){
            if(rateAsc == true) {
                sortRate.setTitle(R.string.rate_asc);
                rateAsc = false;
                filteredBeers.sort(new Comparator<Beer>() {
                    @Override
                    public int compare(Beer o1, Beer o2) {
                        if (o1.getApperanceRate() == null) return -1;
                        if (o2.getApperanceRate() == null) return 1;
                        if ((o1.getTasteRate() + o1.getBitternessRate() + o1.getAromaRate() + o1.getApperanceRate()) > (o2.getTasteRate() + o2.getBitternessRate() + o2.getAromaRate() + o2.getApperanceRate())) {
                            return 1;
                        }
                        return -1;
                    }
                });
            }
            else{
                rateAsc = true;
                sortRate.setTitle(R.string.rate_desc);
                filteredBeers.sort(new Comparator<Beer>() {
                    @Override
                    public int compare(Beer o1, Beer o2) {
                        if (o1.getApperanceRate() == null) return -1;
                        if (o2.getApperanceRate() == null) return 1;
                        if ((o1.getTasteRate() + o1.getBitternessRate() + o1.getAromaRate() + o1.getApperanceRate()) > (o2.getTasteRate() + o2.getBitternessRate() + o2.getAromaRate() + o2.getApperanceRate())) {
                            Log.d(o1.getName() + " true", String.valueOf(+(o1.getTasteRate() + o1.getBitternessRate() + o1.getAromaRate() + o1.getApperanceRate())));
                            Log.d(o2.getName() + " true", String.valueOf((o2.getTasteRate() + o2.getBitternessRate() + o2.getAromaRate() + o2.getApperanceRate())));
                            return -1;
                        }
                        return 1;
                    }
                });
            }
            notifyDataSetChanged();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void sortByName(MenuItem sortName){
            if(nameAsc == true) {
                sortName.setTitle(R.string.name_asc);
                nameAsc = false;
                filteredBeers.sort(new Comparator<Beer>() {
                    @Override
                    public int compare(Beer o1, Beer o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }
            else{
                nameAsc = true;
                sortName.setTitle(R.string.name_desc);
                filteredBeers.sort(new Comparator<Beer>() {
                    @Override
                    public int compare(Beer o1, Beer o2) {
                        return o1.getName().compareTo(o2.getName())*-1;
                    }
                });
            }
            notifyDataSetChanged();
        }

        void setNameAsc(boolean nameAsc){
            this.nameAsc = nameAsc;
        }
    }





    public class BitmapLoadTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView mImageView;
        private String TAG;


        public BitmapLoadTask(ImageView imageView, String TAG) {
            this.mImageView = imageView;
            this.TAG = TAG;

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = BitmapFactory.decodeFile(params[0]);

            try {
                if(bitmap != null) {
                    bitmap = modifyOrientation(bitmap, params[0]);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 133, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            addBitmapToCache(TAG,bitmap);
            return bitmap;


        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(mImageView.getTag().toString().equals(TAG)) {
                mImageView.setImageBitmap(bitmap);
            }
        }

        public Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
            ExifInterface ei = new ExifInterface(image_absolute_path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotate(bitmap, 90);

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotate(bitmap, 180);

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotate(bitmap, 270);

                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    return flip(bitmap, true, false);

                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    return flip(bitmap, false, true);

                default:
                    return bitmap;
            }
        }

        public Bitmap rotate(Bitmap bitmap, float degrees) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        public Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
            Matrix matrix = new Matrix();
            matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
    }



    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        if(mMemoryCache.containsKey(key)){
            return mMemoryCache.get(key);
        }
        return null;
    }

}
