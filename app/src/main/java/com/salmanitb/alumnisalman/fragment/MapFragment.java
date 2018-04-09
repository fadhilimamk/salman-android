package com.salmanitb.alumnisalman.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.salmanitb.alumnisalman.R;
import com.salmanitb.alumnisalman.activity.SearchActivity;
import com.salmanitb.alumnisalman.model.City;

import static com.salmanitb.alumnisalman.activity.MainActivity.cities;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {


    private GoogleMap googleMap;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        Spinner city_spinner = (Spinner) rootView.findViewById(R.id.city_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.cities_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        city_spinner.setAdapter(adapter);

        ImageButton search = (ImageButton) rootView.findViewById(R.id.search_alumni);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentManager fragmentManager = getChildFragmentManager();
        ((SupportMapFragment) fragmentManager.findFragmentById(R.id.searchMapFragment))
            .getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    Log.d("SearchFragment", "google map ready");
                    googleMap = map;

                    googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            Log.d("Bound: ", googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.toString());
                            Log.d("Bound: ", googleMap.getProjection().getVisibleRegion().latLngBounds.southwest.toString());

                            for (City city : cities) {

                                if (city.getName().equals("Bandung")) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(city.getLatitute(), city.getLongitude())));
                                }

                                LatLng pos = new LatLng(city.getLatitute(), city.getLongitude());

                                if (googleMap.getProjection().getVisibleRegion().latLngBounds.contains(pos)) {
                                    googleMap.addMarker(new MarkerOptions().position(pos)
                                            .title(city.getName()));
                                }
                            }

                        }



                    });

                    googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                        @Override
                        public void onCameraMove() {
                            Log.d("Bound: ", googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.toString());
                            Log.d("Bound: ", googleMap.getProjection().getVisibleRegion().latLngBounds.southwest.toString());

                            for (City city : cities) {

                                LatLng pos = new LatLng(city.getLatitute(), city.getLongitude());

                                if (googleMap.getProjection().getVisibleRegion().latLngBounds.contains(pos)) {
                                    googleMap.addMarker(new MarkerOptions().position(pos)
                                            .title(city.getName()));
                                }
                            }

                        }
                    });


                }
            });

    }

}
