package test.example.handycamera.gallery;

import android.content.Intent;
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

	public static final int DEFAULT_ITEM_WIDTH = 120;//px

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
		RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		int dpWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ITEM_WIDTH, getResources().getDisplayMetrics());
		mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getContext(), dpWidth));

		mAdapter = new GridAdapter();
		mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
				Intent intent = new Intent(getContext(), ImagePreviewActivity.class);
				intent.putExtra(ImageItem.EXTRAS_KEY_IMAGE, mAdapter.getItem(pos).getId());
				startActivity(intent);
			}
		});

		mRecyclerView.setAdapter(mAdapter);

		// Create loader for reading data
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<List<ImageItem>> onCreateLoader(int id, Bundle args) {
		int dpWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ITEM_WIDTH, getResources().getDisplayMetrics());

		ImagesDataSource ds = ImagesDataSource.getInstance(getContext());
		ds.setHeight(dpWidth);
		ds.setWidth(dpWidth);

		return new ImagesLoader(getContext(), ds);
	}

	@Override
	public void onLoadFinished(Loader<List<ImageItem>> loader, List<ImageItem> data) {
		if (data == null) {
			Log.e(LOG_TAG, "Failed to load images");
		} else {
			mAdapter.setData(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<ImageItem>> loader) {
	}
}
