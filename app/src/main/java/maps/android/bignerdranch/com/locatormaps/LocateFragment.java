package maps.android.bignerdranch.com.locatormaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Bender on 29/09/2015.
 */
public class LocateFragment extends SupportMapFragment {

	//private ImageView mImageView;
	private GoogleApiClient mClient;
	private GoogleMap mMap;
	private Bitmap mMapImage;
	private GalleryItem mMapItem;
	private Location mCurrentLocation;



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
		getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				mMap = googleMap;
				updateUI();
			}
		});
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

	private void updateUI(){
		if(mMap == null || mMapImage == null) return;
		LatLng itemPoint = new LatLng(mMapItem.getLat(), mMapItem.getLon());
		LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

		LatLngBounds bounds = new LatLngBounds.Builder().include(itemPoint).include(myPoint).build();
		int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
		CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
		mMap.animateCamera(update);



	}

	private class SearchTask extends AsyncTask<Location, Void, Void>{
		private GalleryItem mItem;
		private Bitmap mBitmap;
		private Location mLocation;

		@Override
		protected Void doInBackground(Location... params) {
			FlickerFetcher fetcher = new FlickerFetcher();
			mLocation = params[0];
			List<GalleryItem> galleryItems = fetcher.searchPhotos(mLocation);
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
			mMapImage = mBitmap;
			mMapItem = mItem;
			mCurrentLocation = mLocation;
			updateUI();
			//mImageView.setImageBitmap(mBitmap);
		}
	}
}
