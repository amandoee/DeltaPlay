package com.example.DeltaPlay;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.yalantis.ucrop.UCrop;
import java.io.File;

public class MainActivity extends AppCompatActivity {
  private FrameLayout container;
  private static final int REQUEST_IMAGE = 101;
  private String filename;
  private int xDelta, yDelta;

  private int billedeID;

  private final static String CAPTURED_PHOTO_URI_KEY = null;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    container = findViewById(R.id.container);
    Button btn_add = findViewById(R.id.btn_add);
    btn_add.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View view) {

        //Måske noget IF - statement med at der skal tastes en kode?

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);

      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case REQUEST_IMAGE: {

          Uri selectedImageUri = data.getData();

          if (data != null && data.getData() != null) {
            //startCrop(data.getData()); DETTE ER KODEN TIL AT CROPPE.
            billedeID++;

          }

          //DETTE ER KODE TIL BLOT AT TILFØJE. Her er det lettest at assigne et tilfældigt id. Lige nu er det bare 1,2,3, ...
          //id skal tilføjes til liste, der skal læses af saveinstance. Brug URI src.
          if (null != selectedImageUri) {

            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageURI(selectedImageUri);

            /* IKKE NØDVENDIG. DET KRÆVEDE BARE AT MAN IGNOREREDE RESIZE, KEYBOARD OG ORIENTATION.
            imageView.setId(billedeID);

            Integer y = new Integer(billedeID);
            String billedeIDString = Integer.toString(billedeID);
            Toast.makeText(getApplicationContext(),billedeIDString,Toast.LENGTH_LONG).show();
            */

            addView(imageView, 350, 350);


          }

          /* SLÅ DETTE TIL FOR AT VÆRE UDEN CROPPING VÆRKTØJ
          Uri selectedImageUri = data.getData();
          if (null != selectedImageUri) {

            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageURI(selectedImageUri);
            image

            addView(imageView,200,200);
          }
           */

          break;
        }


        case UCrop.REQUEST_CROP: {
          createImageView();
          break;
        }

      }
    }
  }

  private void createImageView() {
    File file = new File(getCacheDir(), filename);
    Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
    ImageView imageView = new ImageView(MainActivity.this);

    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(400, 400);
    params.leftMargin = 0;
    params.topMargin = 0;
    imageView.setLayoutParams(params);
    imageView.setImageBitmap(bmp);
    imageView.setOnTouchListener(touchListener);
    container.addView(imageView);
    file.delete();
  }

  public void addView(ImageView imageView, int width, int height) {
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
    layoutParams.setMargins(0,10,0,10);

    imageView.setLayoutParams(layoutParams);
    imageView.setOnTouchListener(touchListener);
    container.addView(imageView);
  }

  private void startCrop(Uri data) {
    String name = getFileName(data);
    File file = new File(getCacheDir(), name);
    filename = name;
    UCrop uCrop = UCrop.of(data, Uri.fromFile(file));
    uCrop.start(this);
  }

  private View.OnTouchListener touchListener = new View.OnTouchListener() {
    @Override public boolean onTouch(View view, MotionEvent event) {
      final int x = (int) event.getRawX();
      final int y = (int) event.getRawY();

      switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN: {
          FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();

          xDelta = x - lParams.leftMargin;
          yDelta = y - lParams.topMargin;
          break;
        }
        case MotionEvent.ACTION_UP: {
          //Toast.makeText(getApplicationContext(), "Flytter billede", Toast.LENGTH_SHORT).show();
          break;
        }
        case MotionEvent.ACTION_MOVE: {
          if (x - xDelta + view.getWidth() <= container.getWidth()
              && y - yDelta + view.getHeight() <= container.getHeight()
              && x - xDelta >= 0
              && y - yDelta >= 0) {
            FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = x - xDelta;
            layoutParams.topMargin = y - yDelta;
            layoutParams.rightMargin = 0;
            layoutParams.bottomMargin = 0;
            view.setLayoutParams(layoutParams);
          }
          break;
        }
      }
      container.invalidate();
      return true;
    }
  };

  public String getFileName(Uri uri) {
    String result = null;
    if (uri.getScheme().equals("content")) {
      Cursor cursor = getContentResolver().query(uri, null, null, null, null);
      try {
        if (cursor != null && cursor.moveToFirst()) {
          result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }
      } finally {
        cursor.close();
      }
    }
    if (result == null) {
      result = uri.getPath();
      int cut = result.lastIndexOf('/');
      if (cut != -1) {
        result = result.substring(cut + 1);
      }
    }
    return result;
  }

/*
  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {

    if (billedeID > 0) {

      savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
    }
    if (billedeID > 0) {

      savedInstanceState.putString(CAPTURED_PHOTO_URI_KEY, mCapturedImageURI.toString());
    }

    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {

    if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {

      mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
    }
    if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)) {

      mCapturedImageURI = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));    }

    super.onRestoreInstanceState(savedInstanceState);

  }

 */
}