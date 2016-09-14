package test.example.handycamera.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import test.example.handycamera.camera.CameraActivity;
import test.example.handycamera.ImageEditActivity;
import test.example.handycamera.R;
import test.example.handycamera.util.FileUtil;

public class GalleryActivity extends AppCompatActivity {

	private static final int REQUEST_CODE_MAKE_PHOTO = 1;

	private static final int REQUEST_WRITE_PERMISSION = 2;

	public static final String IMAGES_FOLDER_NAME = "HandyCamera";

	/** Ссылка на изображение в формате Uri */
	protected Uri imageUri = null;

	/** Tag for logging information. */
	private final String LOG_TAG = "GalleryActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		GalleryFragment tasksFragment =
				(GalleryFragment) getSupportFragmentManager().findFragmentById(R.id.gallery_fragment);
		if (tasksFragment == null) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.gallery_fragment, GalleryFragment.newInstance());
			transaction.commit();
		}

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//						.setAction("Action", null).show();
			makePhoto();
			}
		});
	}

	private void makePhoto() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			runCameraActivityLollipop();
		} else{
			runCameraActivityPreLollipop();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(LOG_TAG, "onACtivityResult reqCode = " + requestCode + " resultCode= " + resultCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_CODE_MAKE_PHOTO:
					//Open edit ImageEditActivity with camera results.
					Intent intent = new Intent(getApplicationContext(), ImageEditActivity.class);
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

						String str = data.getStringExtra(MediaStore.EXTRA_OUTPUT);
						Log.v(LOG_TAG, "cameraResults = " + str);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, data.getStringExtra(MediaStore.EXTRA_OUTPUT));
					} else {
						intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri.getPath());
					}
					startActivity(intent);
					break;
			}
		}
	}

	private void runCameraActivityPreLollipop() {
		// do something for phones running an SDK before lollipop
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		imageUri = getOutputMediaFileUri();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, REQUEST_CODE_MAKE_PHOTO);
	}

	private void runCameraActivityLollipop() {
		if (ContextCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			requestWritePermission();
		} else {
			Log.v(LOG_TAG, "Has permission");
			startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE_MAKE_PHOTO);
		}
	}

	/**
	 * Get Uri for image file.
	 * @return Uri.
	 */
	private Uri getOutputMediaFileUri() {
		String timeStamp = new SimpleDateFormat(
				"yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File f = FileUtil.createFile(FileUtil.getPicturesStorageDir(""),
				"IMG_"+ timeStamp + ".jpeg");
		if (f != null) {
			return Uri.fromFile(f);
		} else {
			return null;
		}
	}

	/**
	 * Ask permission write files into file system.
	 */
	private void requestWritePermission() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(
					GalleryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			ActivityCompat.requestPermissions(
					GalleryActivity.this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					REQUEST_WRITE_PERMISSION
			);
		} else {
			startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE_MAKE_PHOTO);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
														@NonNull int[] grantResults) {
		if (requestCode == REQUEST_WRITE_PERMISSION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE_MAKE_PHOTO);
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
