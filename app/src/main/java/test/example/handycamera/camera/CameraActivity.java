package test.example.handycamera.camera;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import test.example.handycamera.R;

/**
 * Created on 10.09.2016.
 * @author Dimowner
 */
@TargetApi(21)
public class CameraActivity extends AppCompatActivity {

	private Camera2Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		if (null == savedInstanceState) {
			fragment = Camera2Fragment.newInstance();
			getFragmentManager().beginTransaction()
					.replace(R.id.container, fragment)
					.commit();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		fragment.removeSavedImage();
	}
}
