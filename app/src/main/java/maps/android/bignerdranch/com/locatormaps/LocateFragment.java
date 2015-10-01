package maps.android.bignerdranch.com.locatormaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Bender on 29/09/2015.
 */
public class LocateFragment extends Fragment {

	private ImageView mImageView;
	private GoogleApiClient mClient;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API)
				.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
					@Override
					public void onConnected(Bundle bundle) {
						getActivity().invalidateOptionsMenu();
					}

					@Override
					public void onConnectionSuspended(int i) {

					}
				})
				.build();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_locator, menu);
		MenuItem item = menu.findItem(R.id.action_locate);
		item.setEnabled(mClient.isConnected());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.action_locate:
				findImage();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mClient.connect();
		getActivity().invalidateOptionsMenu();
	}

	@Override
	public void onStop() {
		super.onStop();
		mClient.disconnect();
	}

	public static LocateFragment newInstance(){
		LocateFragment fragment = new LocateFragment();
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_locator, container, false);
		mImageView = (ImageView) view.findViewById(R.id.imageView);

		return view;
	}

	private void findImage(){
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setNumUpdates(1);
		request.setInterval(0);
		LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				new SearchTask().execute(location);
			}
		});

	}

	private class SearchTask extends AsyncTask<Location, Void, Void>{
		private GalleryItem mItem;
		private Bitmap mBitmap;

		@Override
		protected Void doInBackground(Location... params) {
			FlickerFetcher fetcher = new FlickerFetcher();
			List<GalleryItem> galleryItems = fetcher.searchPhotos(params[0]);
			if(galleryItems.size() == 0) {
				return null;
			}
			Random rand = new Random();
			mItem = galleryItems.get(rand.nextInt(galleryItems.size()));
			try{
				byte[] bytes = fetcher.getUrlBytes(mItem.getUrl());
				mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			} catch(IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			mImageView.setImageBitmap(mBitmap);
		}
	}
}
