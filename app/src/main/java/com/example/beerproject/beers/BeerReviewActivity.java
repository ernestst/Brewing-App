package com.example.beerproject.beers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beerproject.R;
import com.example.beerproject.beerDAO.Beer;
import com.example.beerproject.beerDAO.BeerViewModel;
import com.example.beerproject.beerDAO.Ingredient;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class BeerReviewActivity extends AppCompatActivity {
    private static final String ARG_BEER_ID="arg_beer_id";

    Beer beer;
    private TextView nameField;
    private TextView timeField;
    private TextView temperatureField;
    private TextView rateField;
    private TextView commentField;
    private TextView beerStyle;
    private RecyclerView ingredients;
    Button addReview;
    private BeerViewModel beerViewModel;
    ImageView imageView;
    Uri photoURI = null;
    File photoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_review_layout);
        Integer beerId = (Integer) getIntent().getSerializableExtra(BeerListActivity.KEY_EXTRA_BEER_ID);
        nameField = findViewById(R.id.beer_review_name);
        imageView =  findViewById(R.id.imageView);
        timeField = findViewById(R.id.beer_review_time);
        temperatureField = findViewById(R.id.beer_review_temp);
        commentField = findViewById(R.id.beer_comment_text);
        rateField = findViewById(R.id.beer_review_rate);
        beerStyle = findViewById(R.id.beer_style_review);
        ingredients = findViewById(R.id.ingredient_review_recycler_view);
        ingredients.setLayoutManager(new LinearLayoutManager(this));

        addReview = findViewById(R.id.add_review);
        beerViewModel = ViewModelProviders.of(this).get(BeerViewModel.class);
        beerViewModel.findBeerWithId(beerId).observe(this, new Observer<Beer>() {
            @Override
            public void onChanged(Beer beer1) {
                beer = beer1;
                if(beer1 != null) {
                    prepareBeer(beer1);
                }
            }
        });


        imageView.setOnClickListener(view1 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureImage();
            } else {
                displayMessage(this, "nie obłsugiwane");
            }
        });




    }

    void prepareBeer(Beer beer1){
        if (beer.getPhotoPath() != null) {
            BitmapLoadTask task = new BitmapLoadTask(imageView);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, beer.getPhotoPath());
            imageView.setClickable(false);
        } else {
            imageView.setClickable(true);
        }
        beerStyle.setText(beer1.getBeerStyle());
        nameField.setText(beer1.getName());
        timeField.setText(beer1.getBrewingTime().toString() + " min");
        temperatureField.setText(beer1.getBrewingTemperature().toString() + " C");
        IngredientAdapter adapter = new IngredientAdapter();
        adapter.setIngredients(beer1.getIngredientList());
        ingredients.setAdapter(adapter);
        if (beer1.getApperanceRate() != null) {
            Float rate = beer1.getApperanceRate() + beer1.getAromaRate() + beer1.getBitternessRate() + beer1.getTasteRate();
            rate = rate / 4;
            rateField.setText(rate.toString());
        }
        if (beer1.getComment() != null) {
            commentField.setText(beer1.getComment());
        }

        addReview.setOnClickListener(view1 -> {
            clickAddReview(beer1);
        });
    }

    void clickAddReview(Beer beer1){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(BeerReviewActivity.this).create();
        LayoutInflater inflater2 = getLayoutInflater();
        View dialogView = inflater2.inflate(R.layout.add_review_layout, null);

        EditText reviewTaste = dialogView.findViewById(R.id.taste);
        reviewTaste.setFilters(new InputFilter[]{new RateInputFilter("1", "5")});

        EditText reviewAroma = dialogView.findViewById(R.id.aroma);
        reviewAroma.setFilters(new InputFilter[]{new RateInputFilter("1", "5")});

        EditText reviewApperance = dialogView.findViewById(R.id.apperance);
        reviewApperance.setFilters(new InputFilter[]{new RateInputFilter("1", "5")});

        EditText reviewBitterness = dialogView.findViewById(R.id.bitterness);
        reviewBitterness.setFilters(new InputFilter[]{new RateInputFilter("1", "5")});

        if (beer1.getApperanceRate() != null) {
            reviewBitterness.setText(beer1.getBitternessRate().toString());
            reviewApperance.setText(beer1.getApperanceRate().toString());
            reviewAroma.setText(beer1.getAromaRate().toString());
            reviewTaste.setText(beer1.getTasteRate().toString());
        }

        EditText addComment = dialogView.findViewById(R.id.beer_comment_edit);
        Button addIngredientButton = dialogView.findViewById(R.id.add_review_button);

        if (beer1.getComment() != null) {
            addComment.setText(beer1.getComment());
        }

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAddIngredient(reviewTaste.getText().toString(),reviewAroma.getText().toString(),reviewApperance.getText().toString(),reviewBitterness.getText().toString(),addComment.getText().toString());
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    void clickAddIngredient(String reviewTaste, String reviewAroma, String reviewApperance, String reviewBitterness, String addComment){
        if (reviewTaste.equals("") || reviewAroma.equals("") || reviewApperance.equals("") || reviewBitterness.equals("")) {
        } else {
            Float rate = Float.parseFloat(reviewTaste) + Float.parseFloat(reviewAroma) + Float.parseFloat(reviewApperance) + Float.parseFloat(reviewBitterness);
            rate = rate / 4;
            beer.setComment(addComment);
            beer.setAromaRate(Float.parseFloat(reviewAroma));
            beer.setApperanceRate(Float.parseFloat(reviewApperance));
            beer.setTasteRate(Float.parseFloat(reviewTaste));
            beer.setBitternessRate(Float.parseFloat(reviewBitterness));
            beerViewModel.update(beer);
            rateField.setText(rate.toString());
            commentField.setText(addComment);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.beer_fragment_menu, menu);


        MenuItem info = menu.findItem(R.id.menu_item_delete);
        info.setOnMenuItemClickListener( menuItem -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(BeerReviewActivity.this);
            builder1.setMessage("Are you sure you want to delete this beer?");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            beerViewModel = ViewModelProviders.of(BeerReviewActivity.this).get(BeerViewModel.class);
                            beerViewModel.delete(beer);
                            Intent intent = new Intent(BeerReviewActivity.this, BeerListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(BeerListActivity.KEY_EXTRA_BEER_ID,beer.getId());
                            startActivity(intent,bundle);
                        }
                    });



            AlertDialog alert11 = builder1.create();
            alert11.show();
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }

    private class IngredientHolder extends RecyclerView.ViewHolder{

        private TextView ingredientNameHolder;
        private TextView ingredientAmountHolder;
        private TextView ingredientTimeHolder;

        public IngredientHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.ingredient_review_element,parent,false));
            ingredientNameHolder = itemView.findViewById(R.id.ingredient_name_element);
            ingredientAmountHolder = itemView.findViewById(R.id.ingredient_amount_element);
            ingredientTimeHolder = itemView.findViewById(R.id.ingredient_time_element);
        }

        public void bind(Ingredient ingredient)
        {
            ingredientNameHolder.setText(ingredient.getName());
            ingredientAmountHolder.setText(ingredient.getAmount().toString()+" g");
            ingredientTimeHolder.setText(ingredient.getMinute().toString()+" min");
        }
    }

    private class IngredientAdapter extends RecyclerView.Adapter<BeerReviewActivity.IngredientHolder>
    {
        private ArrayList<Ingredient> ingredients;

        public void setIngredients(ArrayList<Ingredient> ingredients)
        {
            this.ingredients = ingredients;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public BeerReviewActivity.IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BeerReviewActivity.IngredientHolder(getLayoutInflater(), parent);

        }


        @Override
        public void onBindViewHolder(@NonNull BeerReviewActivity.IngredientHolder holder, int position) {

            if (ingredients != null) {
                Ingredient ingredient = ingredients.get(position);
                holder.bind(ingredient);
            } else {
                Log.d("BeerReview", "No ingredients");
            }
        }

        @Override
        public int getItemCount()
        {
            if (ingredients != null){
                return ingredients.size();
            } else {
                return 0;
            }
        }

    }


    private void captureImage()
    {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        else
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                try {

                    photoFile = createImageFile();

                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(this,
                                "com.example.beerproject.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception ex) {
                    displayMessage(this,ex.getMessage());
                }


            }else
            {
                displayMessage(this,"Nullll");
            }
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            try {
                myBitmap = modifyOrientation(myBitmap, photoFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                beer.setPhotoPath(photoFile.getAbsolutePath());
                beerViewModel.update(beer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            displayMessage(this, "Request cancelled or something went wrong.");
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }

    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
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

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public class BitmapLoadTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView mImageView;

        public BitmapLoadTask(ImageView imageView) {
            this.mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = BitmapFactory.decodeFile(params[0]);
            try {
                if (bitmap != null){
                    bitmap = modifyOrientation(bitmap, params[0]);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 300, 400, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }

}
