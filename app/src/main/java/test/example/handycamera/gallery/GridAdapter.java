package test.example.handycamera.gallery;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import test.example.handycamera.R;
import test.example.handycamera.data.ImageItem;

/**
 * Created on 11.09.2016.
 * @author Dimowner
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View v) {
			super(v);
			mView = v;
		}

		public View mView;
	}

	public GridAdapter(ArrayList<ImageItem> items) {
		if (items != null) {
			this.data = items;
		} else {
			this.data = new ArrayList<>();
		}
	}

	@Override
	public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext())
							.inflate(R.layout.grid_item_image, parent, false));

	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {

		final int pos = holder.getAdapterPosition();
		ImageItem img = data.get(pos);

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (itemClickListener != null) {
					itemClickListener.onItemClick(null, v, pos, v.getId());
				}
			}
		});
		TextView tvTitle = (TextView) holder.mView.findViewById(R.id.grid_item_title);
		ImageView ivImg = (ImageView) holder.mView.findViewById(R.id.grid_item_img);

		tvTitle.setText(img.getTitle());
		ivImg.setImageBitmap(img.getImg());
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public void addItem(ImageItem item) {
		data.add(item);
		notifyDataSetChanged();
	}

	public void addItems(List<ImageItem> items) {
		Log.v("GridAdapter", "addItems");
		data.addAll(items);
		notifyDataSetChanged();
	}

	public ImageItem getItem(int pos) {
		return data.get(pos);
	}

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		itemClickListener = listener;
	}


	protected List<ImageItem> data;

	protected AdapterView.OnItemClickListener itemClickListener;

//	public interface OnItemClickListener {
//		void onItemClick(View view, int position);
//	}
}