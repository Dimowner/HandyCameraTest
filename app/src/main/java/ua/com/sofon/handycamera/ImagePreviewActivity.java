package ua.com.sofon.handycamera;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import ua.com.sofon.handycamera.data.ImageItem;
import ua.com.sofon.handycamera.data.ImagesDataSource;
import ua.com.sofon.handycamera.data.ImagesLoader;
import ua.com.sofon.handycamera.util.FileUtil;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Activity for preview images with zoom.
 * Created on 11.09.2016.
 * @author Dimowner
 */
public class ImagePreviewActivity extends AppCompatActivity
			implements LoaderManager.LoaderCallbacks<List<ImageItem>> {

	/** Tag for logging information. */
	private final String LOG_TAG = "ImagePreviewActivity";

	public static final int REQUEST_CODE_EDIT = 1;

	private ImageView mImageView;

	private PhotoViewAttacher mAttacher;

	private long mImageId = ImageItem.NO_ID;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_image);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		mImageView = (ImageView) findViewById(R.id.image_preview_photo);
		mAttacher = new PhotoViewAttacher(mImageView);

		if (savedInstanceState == null) {
			mImageId = getIntent().getLongExtra(ImageItem.EXTRAS_KEY_IMAGE, ImageItem.NO_ID);
		}

		// Create loader for reading data
		this.getSupportLoaderManager().initLoader(1, null, this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ImageItem.EXTRAS_KEY_IMAGE, mImageId);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageId = savedInstanceState.getLong(ImageItem.EXTRAS_KEY_IMAGE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mAttacher.cleanup();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_edit_delete, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.action_delete:
				AlertDialog.Builder builder = new AlertDialog.Builder(ImagePreviewActivity.this);
				builder.setTitle(R.string.image_preview_warning);
				builder.setMessage(R.string.image_preview_accept_deletion);
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new DeleteImageTask().execute(mImageId);
					}
				});
				builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				builder.create().show();
				return true;
			case R.id.action_edit:
				Intent intent = new Intent(getApplicationContext(), ImageEditActivity.class);
				intent.putExtra(ImageItem.EXTRAS_KEY_IMAGE, mImageId);
				startActivityForResult(intent, REQUEST_CODE_EDIT);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Loader<List<ImageItem>> onCreateLoader(int id, Bundle args) {
		ImagesDataSource ds = ImagesDataSource.getInstance(getApplicationContext());
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		ds.setHeight(size.y);
		ds.setWidth(size.x);

		return new ImagesLoader(getApplicationContext(), ds, mImageId);
	}

	@Override
	public void onLoadFinished(Loader<List<ImageItem>> loader, List<ImageItem> data) {
		if (data.size() > 0 && data.get(0) != null) {
			mImageView.setImageBitmap(data.get(0).getImg());
			getSupportActionBar().setTitle(data.get(0).getTitle());
			getSupportActionBar().setSubtitle(data.get(0).getFormattedDate());
		}
	}

	@Override
	public void onLoaderReset(Loader<List<ImageItem>> loader) {
	}

	public class DeleteImageTask extends AsyncTask<Long, Void, Void> {
		@Override
		protected Void doInBackground(Long... ids) {
			ImagesDataSource dataSource = ImagesDataSource.getInstance(getApplicationContext());
			ImageItem item = dataSource.getImage(ids[0]);
			if (item != null) {
				FileUtil.deleteRecursivelyDirs(new File(item.getPath()));
			}
			dataSource.deleteImage(ids[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			finish();
		}
	}
}
