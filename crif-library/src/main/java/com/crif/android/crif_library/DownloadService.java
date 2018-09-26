package com.crif.android.crif_library;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class DownloadService extends IntentService {

    private String HOME_URL = "http://18.191.145.152/api/";
    private String LOCATION_URL = "Locationtbs/PostLocationtb";

    public DownloadService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        final String id = intent.getStringExtra("Id");

        SingleShotLocationProvider.requestSingleUpdate(getApplicationContext(),
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("Location", "my location is " + location.toString());
                        getAddress(getApplicationContext(),location.latitude, location.longitude,id);
                    }
                });

        Toast.makeText(getApplicationContext(), "Service Entered", Toast.LENGTH_SHORT).show();
    }

    public void getAddress(Context context,float latitude, float longitude,String userId) {
        Address locationAddress;

        locationAddress = getStringAddress(latitude, longitude,context);

        if (locationAddress != null) {

            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();


            String currentLocation;

            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation += "," + address1;

                if (!TextUtils.isEmpty(city)) {
                    currentLocation += "," + city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += " - " + postalCode;
                } else {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += "," + postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation += "," + state;

                if (!TextUtils.isEmpty(country))
                    currentLocation += "," + country;

                JSONObject jsonToSend = new JSONObject();
                try {
                    jsonToSend.put("UserId", userId);
                    jsonToSend.put("Location", currentLocation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new UploadData(HOME_URL + LOCATION_URL, jsonToSend).execute();
//                tvAddress.setText(currentLocation);
            }
        }
    }

    public Address getStringAddress(double latitude, double longitude, Context context) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public class UploadData extends AsyncTask<Void, Void, JSONObject> {
        private String URL;
        private JSONObject jsonObjSend;

        public UploadData(String URL, JSONObject jsonObjSend) {
            this.URL = URL;
            this.jsonObjSend = jsonObjSend;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject jsonObjRecv = new JSONObject();
            java.net.URL url = null;
            HttpURLConnection con = null;
            BufferedReader in = null;
            String inputLine;
            String result;
            try {
                url = new URL(URL);
                String jsonString = "";
                jsonString = jsonObjSend.toString();

                Log.i("TAG", "URL:" + url.toString());
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setDoOutput(true);
                con.setConnectTimeout(2000);
                con.setDoInput(true);
                con.setRequestMethod("POST");
                OutputStream os = con.getOutputStream();
                os.write(jsonString.getBytes("UTF-8"));
                os.close();

                InputStreamReader streamReader = new
                        InputStreamReader(con.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                jsonObjRecv = new JSONObject();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObjRecv;
        }

        protected void onPostExecute(JSONObject result) {

          //  Toast.makeText(MainActivity.this, "Data uploaded !!", Toast.LENGTH_SHORT).show();
        }


    }

}
