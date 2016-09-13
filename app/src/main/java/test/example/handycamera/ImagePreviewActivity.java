package test.example.handycamera;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.List;

import test.example.handycamera.data.ImageItem;
import test.example.handycamera.data.ImagesDataSource;
import test.example.handycamera.data.ImagesLoader;
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
//			buildActivity(getIntent());
			mImageId = getIntent().getLongExtra(ImageItem.EXTRAS_KEY_IMAGE, ImageItem.NO_ID);
		}

		// Create loader for reading data
		this.getSupportLoaderManager().initLoader(1, null, this);
////		ImageItem img;
//		if (getIntent().hasExtra(GalleryFragment.EXTRAS_KEY_IMAGE)) {
//			mImage = getIntent().getParcelableExtra(GalleryFragment.EXTRAS_KEY_IMAGE);
//			Log.v("ImagePreviewActivity", mImage.toString());
//			getSupportActionBar().setTitle(mImage.getTitle());
//		} else {
//			Log.v("ImagePreviewActivity", "mImage is null");
//			mImage = null;
//		}
//
//
//
//		if (mImage != null) {
//			mImageView.setImageBitmap(mImage.getImg());
//
//		}

	}

//	private void buildActivity(Intent data) {
//		if (data.hasExtra(ImageItem.EXTRAS_KEY_IMAGE)) {
//			long id = data.getLongExtra(ImageItem.EXTRAS_KEY_IMAGE, ImageItem.NO_ID);
//			new LoadImageTask()
////			mImage = data.getParcelableExtra(ImageItem.EXTRAS_KEY_IMAGE);
////			mImageView.setImageBitmap(mImage.getImg());
//			Log.v("ImagePreviewActivity", mImage.toString());
//			getSupportActionBar().setTitle(mImage.getTitle());
//		} else {
//			Log.v("ImagePreviewActivity", "mImage is null");
//			mImage = null;
//		}
//	}

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
//				TODO: fix string
				builder.setTitle("Warning");
				builder.setMessage("Accept image deletion");
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("ImagePreview", "onACtivityResult reqCode = " + requestCode + " resultCode= " + resultCode);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_EDIT) {
//				buildActivity(data);
				//TODO: refresh image;
			}
		}
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
		getSupportActionBar().setTitle(data.get(0).getTitle());
	}

	@Override
	public void onLoaderReset(Loader<List<ImageItem>> loader) {
		Log.v(LOG_TAG, "onLoaderReset");
	}

	public class DeleteImageTask extends AsyncTask<Long, Void, Void> {
		@Override
		protected Void doInBackground(Long... ids) {
			ImagesDataSource dataSource = ImagesDataSource.getInstance(getApplicationContext());
			dataSource.deleteImage(ids[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPreExecute();
			finish();
		}
	}

//	/**
//	 * Shows OK/Cancel confirmation dialog about image deletion.
//	 */
//	public static class ConfirmationDialog extends DialogFragment {
//
//
//		public void setId(long id) {
//			this.id = id;
//		}
//
//		@Override
//		public Dialog onCreateDialog(Bundle savedInstanceState) {
//			final Fragment parent = getParentFragment();
//			return new AlertDialog.Builder(getActivity())
////					TODO:fix str
//					.setMessage("do yo want deleta image?")
//					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							new DeleteImageTask().execute(id)
////							FragmentCompat.requestPermissions(parent,
////									new String[]{Manifest.permission.CAMERA},
////									REQUEST_CAMERA_PERMISSION);
//						}
//					})
//					.setNegativeButton(android.R.string.cancel,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									dismiss();
////									Activity activity = parent.getActivity();
////									if (activity != null) {
////										activity.finish();
////									}
//								}
//							})
//					.create();
//		}
//
//		private final long id;
//	}
}
