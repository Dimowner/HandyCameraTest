package ua.com.sofon.handycamera;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;

import ua.com.sofon.handycamera.data.ImageItem;
import ua.com.sofon.handycamera.data.ImagesDataSource;
import ua.com.sofon.handycamera.data.ImagesLoader;
import ua.com.sofon.handycamera.util.FileUtil;

/**
 * Created on 10.09.2016.
 * @author Dimowner
 */
public class ImageEditActivity extends AppCompatActivity
					implements LoaderManager.LoaderCallbacks<List<ImageItem>> {

	public static final int ACTION_ADD  = 1;
	public static final int ACTION_EDIT = 2;

	private int mAction;

	private EditText mEtTitle;

	private ImageView mImageView;

	private long mImageId = ImageItem.NO_ID;

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
			mAction = ACTION_EDIT;
			//This code executed after run from ImagePreviewActivity
			mImageId = getIntent().getLongExtra(ImageItem.EXTRAS_KEY_IMAGE, ImageItem.NO_ID);
		} else {
			mAction = ACTION_ADD;
			//This code executed after camera snapshot
			String path = getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
			if (path != null && !path.isEmpty()) {
				new LoadImageTask().execute(path);
			}
		}
		this.getSupportLoaderManager().initLoader(2, null, this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mAction == ACTION_ADD) {
			new DeleteFileTask().execute(new File(mImage.getPath()));
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
				if (mAction == ACTION_ADD) {
					new DeleteFileTask().execute(new File(mImage.getPath()));
				}
				finish();
				return true;
			case R.id.action_accept:
				if (mAction == ACTION_ADD) {
					if (mEtTitle.getText().length() > 0) {
						mImage.setTitle(mEtTitle.getText().toString());
						new SaveImageTask(SaveImageTask.ACTION_ADD).execute(mImage);
					} else {
						Snackbar.make(mEtTitle, R.string.image_edit_title_enter_title, Snackbar.LENGTH_LONG)
								.setAction("Action", null).show();
					}
				} else {
					if (mEtTitle.getText().length() > 0) {
						mImage.setTitle(mEtTitle.getText().toString());
						new SaveImageTask(SaveImageTask.ACTION_UPDATE).execute(mImage);
					} else {
						Snackbar.make(mEtTitle, R.string.image_edit_title_enter_title, Snackbar.LENGTH_LONG)
								.setAction("Action", null).show();
					}
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Loader<List<ImageItem>> onCreateLoader(int id, Bundle args) {
		if (mImageId != ImageItem.NO_ID) {
			ImagesDataSource ds = ImagesDataSource.getInstance(getApplicationContext());

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);

			ds.setHeight(size.y);
			ds.setWidth(size.x);

			return new ImagesLoader(getApplicationContext(), ds, mImageId);
		} else {
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<List<ImageItem>> loader, List<ImageItem> data) {
		if (data != null && data.size() > 0) {
			mImage = data.get(0);
			mImageView.setImageBitmap(data.get(0).getImg());
			mEtTitle.setText(data.get(0).getTitle());
		}
	}

	@Override
	public void onLoaderReset(Loader<List<ImageItem>> loader) {
	}


	public class SaveImageTask extends AsyncTask<ImageItem, Void, Void> {

		public static final int ACTION_ADD  = 1;
		public static final int ACTION_UPDATE = 2;

		private int mAction = ACTION_ADD;

		public SaveImageTask(int mAction) {
			if (mAction == ACTION_ADD || mAction == ACTION_EDIT) {
				this.mAction = mAction;
			}
		}

		@Override
		protected Void doInBackground(ImageItem... items) {
			ImagesDataSource dataSource = ImagesDataSource.getInstance(getApplicationContext());
			if (mAction == ACTION_ADD) {
				dataSource.insertImage(items[0]);
			} else {
				dataSource.updateImage(items[0]);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			setResult(RESULT_OK);
			finish();
		}
	}

	public class DeleteFileTask extends AsyncTask<File, Void, Void> {
		@Override
		protected Void doInBackground(File... file) {
			if (file[0] != null) {
				FileUtil.deleteRecursivelyDirs(file[0]);
			}
			return null;
		}
	}

	public class LoadImageTask extends AsyncTask<String, Void, ImageItem> {

		private int width;
		private int height;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		}

		@Override
		protected ImageItem doInBackground(String... file) {
			Bitmap b = FileUtil.decodeSampledBitmapFromFile(file[0], width, height);
			return new ImageItem(ImageItem.NO_ID, "", new Date(), file[0], b);
		}

		@Override
		protected void onPostExecute(ImageItem item) {
			super.onPostExecute(item);
			mImage = item;
			mImageView.setImageBitmap(item.getImg());
		}
	}
}
