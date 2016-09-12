package test.example.handycamera.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import test.example.handycamera.ImagePreviewActivity;
import test.example.handycamera.R;
import test.example.handycamera.data.ImageItem;
import test.example.handycamera.data.ImagesDataSource;
import test.example.handycamera.data.ImagesLoader;

/**
 * Created on 10.09.2016.
 * @author Dimowner
 */
public class GalleryFragment extends Fragment
			implements LoaderManager.LoaderCallbacks<List<ImageItem>> {

	/** Tag for logging information. */
	private final String LOG_TAG = "GalleryFragment";

	public static final int DEFAULT_ITEM_WIDTH = 120;

	/** Grid recycler view. */
	private RecyclerView mRecyclerView;

	private GridAdapter mAdapter;


	public static GalleryFragment newInstance() {
		return new GalleryFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
									 ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_galery, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		float dp = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
		mRecyclerView.setLayoutManager(
				new GridAutofitLayoutManager(getContext(), (int)(DEFAULT_ITEM_WIDTH * dp)));

//		ArrayList<ImageItem> items = new ArrayList<>();

//		for (int i = 0; i < 30; i++) {
//			Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_handy_camera);
//			items.add(new ImageItem(i, "Title " + i, "path", b));
//		}

		mAdapter = new GridAdapter(null);
		mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
				Log.v("Fragment", "onItemClick pos = " + pos);
				Intent intent = new Intent(getContext(), ImagePreviewActivity.class);
				intent.putExtra(ImageItem.EXTRAS_KEY_IMAGE, mAdapter.getItem(pos));
				startActivity(intent);
			}
		});

		mRecyclerView.setAdapter(mAdapter);

		// создаем лоадер для чтения данных
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<List<ImageItem>> onCreateLoader(int id, Bundle args) {
		Log.v(LOG_TAG, "onCreateLoader");
		return new ImagesLoader(getContext(), ImagesDataSource.getInstance(getContext()));
	}

	@Override
	public void onLoadFinished(Loader<List<ImageItem>> loader, List<ImageItem> data) {
		Log.v(LOG_TAG, "onFinishLoad");
		if (data == null) {
			Log.e(LOG_TAG, "Failed to load images");
		} else {
			mAdapter.addItems(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<ImageItem>> loader) {
		Log.v(LOG_TAG, "onLoaderReset");
	}
}
