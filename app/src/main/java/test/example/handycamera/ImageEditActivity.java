package test.example.handycamera;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import test.example.handycamera.data.ImageItem;
import test.example.handycamera.data.ImagesDataSource;
import test.example.handycamera.data.ImagesLoader;
import test.example.handycamera.util.FileUtil;

/**
 * Created on 10.09.2016.
 * @author Dimowner
 */
public class ImageEditActivity extends AppCompatActivity
					implements LoaderManager.LoaderCallbacks<List<ImageItem>> {

	/** Tag for logging information. */
	private final String LOG_TAG = "ImageEditActivity";

	private EditText mEtTitle;

	private ImageView mImageView;

	private long mImageId;

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
		mImageView = (ImageView) findViewById(R.id.edit_img_image);

		if (getIntent().hasExtra(ImageItem.EXTRAS_KEY_IMAGE)) {
			//This code executed after run from ImagePreviewActivity
			mImageId = getIntent().getLongExtra(ImageItem.EXTRAS_KEY_IMAGE, ImageItem.NO_ID);
//			mImage = getIntent().getParcelableExtra(ImageItem.EXTRAS_KEY_IMAGE);
//			iv.setImageBitmap(mImage.getImg());
//			mEtTitle.setText(mImage.getTitle());
		} else {
			//This code executed after camera snapshot
//			Uri imageUri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
			String path = getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
			if (path != null && !path.isEmpty()) {
				Log.v(LOG_TAG, "imageri = " + path);
				Bitmap b = FileUtil.readBitmapFromFile(path);
				mImage = new ImageItem(ImageItem.NO_ID, "", path, b);
				mImageView.setImageBitmap(b);
			}
		}

		this.getSupportLoaderManager().initLoader(2, null, this);
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
					new SaveImageTask().execute(mImage);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImage.getImg().recycle();
	}

	@Override
	public Loader<List<ImageItem>> onCreateLoader(int id, Bundle args) {
		Log.v(LOG_TAG, "onCreateLoader");
		return new ImagesLoader(getApplicationContext(),
				ImagesDataSource.getInstance(getApplicationContext()), mImageId);
	}

	@Override
	public void onLoadFinished(Loader<List<ImageItem>> loader, List<ImageItem> data) {
		Log.v(LOG_TAG, "onFinishLoad size = " + data.size());
		mImageView.setImageBitmap(data.get(0).getImg());
		mEtTitle.setText(data.get(0).getTitle());
	}

	@Override
	public void onLoaderReset(Loader<List<ImageItem>> loader) {
		Log.v(LOG_TAG, "onLoaderReset");
	}


	public class SaveImageTask extends AsyncTask<ImageItem, Void, Void> {
		@Override
		protected Void doInBackground(ImageItem... items) {
			ImagesDataSource dataSource = ImagesDataSource.getInstance(getApplicationContext());
			dataSource.saveImage(items[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPreExecute();
			mImage.getImg().recycle();
			setResult(RESULT_OK);
			finish();
		}
	}
}
