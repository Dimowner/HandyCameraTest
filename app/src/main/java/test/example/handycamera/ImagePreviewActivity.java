package test.example.handycamera;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import test.example.handycamera.data.ImageItem;
import test.example.handycamera.data.ImagesDataSource;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Activity for preview images with zoom.
 * Created on 11.09.2016.
 * @author Dimowner
 */
public class ImagePreviewActivity extends AppCompatActivity {

	public static final int REQUEST_CODE_EDIT = 1;

	private ImageView mImageView;

	private PhotoViewAttacher mAttacher;

	private ImageItem mImage;


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
			buildActivity(getIntent());
		}
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

	private void buildActivity(Intent data) {
		if (data.hasExtra(ImageItem.EXTRAS_KEY_IMAGE)) {
			mImage = data.getParcelableExtra(ImageItem.EXTRAS_KEY_IMAGE);
			mImageView.setImageBitmap(mImage.getImg());
			Log.v("ImagePreviewActivity", mImage.toString());
			getSupportActionBar().setTitle(mImage.getTitle());
		} else {
			Log.v("ImagePreviewActivity", "mImage is null");
			mImage = null;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(ImageItem.EXTRAS_KEY_IMAGE, mImage);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImage = savedInstanceState.getParcelable(ImageItem.EXTRAS_KEY_IMAGE);
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
				//TODO: delete mImage from device and database!
				//TODO: Async deletion;
//				ImagesDataSource dataSource = ImagesDataSource.getInstance(getApplicationContext());
//				dataSource.deleteImage(mImage.getId());
				new DeleteImageTask().execute(mImage.getId());

				return true;
			case R.id.action_edit:
				Intent intent = new Intent(getApplicationContext(), ImageEditActivity.class);
				intent.putExtra(ImageItem.EXTRAS_KEY_IMAGE, mImage);
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
				buildActivity(data);
			}
		}
	}

	public class DeleteImageTask extends AsyncTask<Long, Void, Void> {
		@Override
		protected Void doInBackground(Long... ids) {
			ImagesDataSource dataSource = ImagesDataSource.getInstance(getApplicationContext());
			dataSource.deleteImage(ids[0]);
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			finish();
		}
	}
}
