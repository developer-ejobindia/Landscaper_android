package com.seazoned.landscaper.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.model.FilterData;
import com.seazoned.landscaper.other.GPSTracker;
import com.seazoned.landscaper.service.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationSearchActivity extends AppCompatActivity {

    ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
    ArrayList<FilterData> filter_data;
    //SignupAutoCompleteAdapter mAutoAdapter;
    LocationAdapter mAutoAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private String location;
    private String id;
    private String country;
    private GPSTracker gps;
    private double source_lat=0.0;
    private double source_long=0.0;
    private final int REQUEST_ID_MULTIPLE_PERMISSIONS=1;
    private LinearLayout lnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.tvAutocomplete);
        lnBack=(LinearLayout)findViewById(R.id.lnBack);
        int locationpermission = ContextCompat.checkSelfPermission(LocationSearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT >= 23) {
            if (locationpermission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ID_MULTIPLE_PERMISSIONS);
            } else {
                my_location();
            }
        } else {
            my_location();
        }


        filter_data = new ArrayList<FilterData>();
       /* mAutoAdapter = new SignupAutoCompleteAdapter(this, R.layout.spinner_item);
        mAutoAdapter.notifyDataSetChanged();*/
        mAutoAdapter = new LocationAdapter(this, R.layout.spinner_item);
        mAutoAdapter.notifyDataSetChanged();
        autoCompleteTextView.setAdapter(mAutoAdapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setFocusableInTouchMode(true);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Util.closeKeyBoard(LocationSearchActivity.this);
                getlatlongfromaddress(AppData.sAddress);
            }
        });
        lnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    public void getlatlongfromaddress(String str) {

        try {
            Geocoder geocoder = new Geocoder(this);
            // String locationName = loc.getText().toString();
            List<Address> addressList = geocoder.getFromLocationName(
                    str, 5);
            if (addressList != null && addressList.size() > 0) {

                AppData.sSearchLatitiude = addressList.get(0).getLatitude();
                AppData.sSearchLongitiude = addressList.get(0).getLongitude();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void my_location() {
        //LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        //location1 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        gps = new GPSTracker(LocationSearchActivity.this);
        if (gps.canGetLocation) {
            source_lat = gps.getLatitude();
            source_long = gps.getLongitude();
        }
        //Criteria criteria = new Criteria();
        // Getting the name of the best provider
        //String provider = lm.getBestProvider(criteria, true);
        // Getting Current Location
        //Location location = lm.getLastKnownLocation(provider);
        //Toast.makeText(Parking_car_item_details.this, "Latitude: "+my_latitude+"\nLongitude: "+my_longitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                my_location();
            } else {
                //Util.showToast(Home.this,"Please give permission",2);
            }
        }
    }
    public ArrayList<HashMap<String, String>> autocomplete(String inputSearch) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            String sb = "https://maps.googleapis.com/maps/api/place/autocomplete/json?location=" + source_lat + "," + source_long + "&input=" + URLEncoder.encode(inputSearch, "utf8") + "&language=en&key=AIzaSyDJvNKFF3zHyOKcuSWzJ2RFMQV1Eu3gihI";
            URL url = new URL(sb);
            Log.i("URL", "%%%%%%%%%%%%%%%%%%%%%%%%%%" + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray resultSet = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            //resultList = new ArrayList<String>(resultSet.length());
            if (resultSet.length()>0)
                resultList.clear();

            for (int i = 0; i < resultSet.length(); i++) {
                JSONObject searchJsonDetail = resultSet.getJSONObject(i);
                HashMap<String, String> hashmap = new HashMap<>();
                hashmap.put("result_text", searchJsonDetail.getString("description"));
                //resultList.add(searchJsonDetail.getString("description"));
                JSONArray jarr_terms = searchJsonDetail.getJSONArray("terms");
                String desc = "";
                for (int j = 0; j < jarr_terms.length(); j++) {
                    JSONObject jobj_term = jarr_terms.getJSONObject(j);
                    if (j == 0)
                        hashmap.put("terms", jobj_term.getString("value"));
                    else {
                        if (j == jarr_terms.length() - 1)
                            desc = desc + jobj_term.getString("value");
                        else
                            desc = desc + jobj_term.getString("value") + ", ";

                    }
                }
                hashmap.put("description", desc);
                resultList.add(hashmap);
                /*String ss = jrr.getJSONObject(0).getString("value");
                 city = jrr.getJSONObject(1).getString("value");
                 state = jrr.getJSONObject(2).getString("value");
                 country = jrr.getJSONObject(3).getString("value");

                Log.e("city","*******"+city);
                Log.e("state","*******"+state);
                Log.e("country","*******"+country);*/
                location = searchJsonDetail.getString("description");
                id = searchJsonDetail.getString("id");
                Log.i("id", "%%%%%%%%%%%%%%%%%%%%%%%%%%" + id);
                String s = location;
                String[] parts = s.split(",");
                country = parts[parts.length - 1];
                Log.i("country", "%%%%%%%%%%%%%%%%%%%%%%%%%%" + country);
                filter_data.add(new FilterData(s, id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public class LocationAdapter extends BaseAdapter implements Filterable {
        private ArrayList<HashMap<String, String>> resultList;
        Context context;
        Fragment fr;

        public LocationAdapter(Context context, int resource) {
            this.context = context;
            this.fr = fr;
        }


        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int position) {
            AppData.sAddress=resultList.get(position).get("result_text");
            //return resultList.get(position).get("result_text");
            return resultList.get(position).get("terms");
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.search_item, parent, false);
            try {

                TextView tv_description = (TextView) v.findViewById(R.id.tv_description);
                TextView tv_terms = (TextView) v.findViewById(R.id.tv_terms);
                tv_description.setText(resultList.get(position).get("description"));
                tv_terms.setText(resultList.get(position).get("terms"));
            } catch (Exception e) {

            }
            return v;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {

                        // Retrieve the autocomplete results.
                        //  resultList = ((getActivity())context).autocomplete(constraint.toString());
                        resultList = autocomplete(constraint.toString());

                        filterResults.values = resultList;
                        filterResults.count = resultList.size();

                    }
                    Log.e("filterResults", " " + filterResults);
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
