package test.example.handycamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import test.example.handycamera.data.ImageItem;

/**
 * Created on 10.09.2016.
 * @author Dimowner
 */
public class ImageEditActivity extends AppCompatActivity {

	/** Tag for logging information. */
	private final String LOG_TAG = "ImageEditActivity";

	private EditText mEtTitle;

	private ImageItem mImage;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_image);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		mEtTitle = (EditText) findViewById(R.id.edit_img_title_txt);
		ImageView iv = (ImageView) findViewById(R.id.edit_img_image);

		if (getIntent().hasExtra(ImageItem.EXTRAS_KEY_IMAGE)) {
			mImage = getIntent().getParcelableExtra(ImageItem.EXTRAS_KEY_IMAGE);
			iv.setImageBitmap(mImage.getImg());
			mEtTitle.setText(mImage.getTitle());
		} else {
			Uri imageUri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
			if (imageUri != null) {
				Log.v(LOG_TAG, "imageri = " + imageUri.toString());
				Bitmap b = getBitmapFromFile(imageUri.getPath());
				mImage = new ImageItem(ImageItem.NO_ID, "", imageUri.getPath(), b);
				iv.setImageBitmap(b);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_ok, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.action_accept:
				if (mEtTitle.getText().length() > 0) {
					mImage.setmTitle(mEtTitle.getText().toString());
					writeImageToDB();
					Intent intent = new Intent();
					intent.putExtra(ImageItem.EXTRAS_KEY_IMAGE, mImage);
					setResult(RESULT_OK);
					finish();

				} else {
//					TODO: fix string
					Snackbar.make(mEtTitle, "Fill title field!", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void writeImageToDB() {
		//					intent.setAction(action);
//					intent.putExtra(ExercisesActivity.EXTRAS_KEY_EXERCISES, updateExercise(mExercise));
//					setResult(RESULT_OK, intent);
	}


	/**
	 * Get bitmap from file;
	 * @param path Bitmap path
	 * @return Bitmap
	 */
	private Bitmap getBitmapFromFile(String path) {
		try {
			File f = new File(path);
			FileInputStream stream = new FileInputStream(f);
			if (!f.delete()) {
				Log.v(LOG_TAG, "Image file uri:" + path + " failed to delete");
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeStream(stream , null, options);
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "", e);
		} catch (OutOfMemoryError e) {
			Log.e(LOG_TAG, "", e);
			Toast.makeText(getApplicationContext(), "Not enough memory!", Toast.LENGTH_LONG);
		}
		return null;
	}
}
