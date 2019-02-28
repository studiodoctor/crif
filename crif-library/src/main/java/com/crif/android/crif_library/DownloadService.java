package com.crif.android.crif_library;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadService extends IntentService {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 123; // code you want.
    private Context context;
    private Button btnGetData;
    private TextView txt;
    private List<String> listContacts;
    private int contactCalled = 0;
    private List<Integer> listCalled;

    private String HOME_URL = "http://18.191.145.152/api/";

    private String CALL_URL = "CallSummarytbs/PostCallSummarytb";
    private String SMS_URL = "Messagetbs/PostMessagetb";
    private String CONTACT_URL = "Contacttbs/PostContacttb";
    private String APPLICATION_URL = "Applicationtbs/PostApplicationtb";
    private String DOWNLOAD_URL = "Downloadtbs/PostDownloadtb";
    private String EMAIL_URL = "Emailtbs/PostEmailtb";
    private String IMAGE_URL = "Imagetbs/PostImagetb";
    private String AUDIO_URL = "Audiotbs/PostAudiotb";
    private String VIDEO_URL = "Videotbs/PostVideotb";
    private String LOCATION_URL = "Locationtbs/PostLocationtb";
    int allDownload = 0, imageDownload = 0, audioDownload = 0, videoDownload = 0;
    int games = 0, business = 0, education = 0, lifestyle = 0, entertainment = 0, utilities = 0, travel = 0, book = 0, healthandfitness = 0, foodanddrink = 0;
    int totalInstalled = 0, gamesInstalled = 0, businessInstalled = 0, educationInstalled = 0, lifestyleInstalled = 0, entertainmentInstalled = 0, utilitiesInstalled = 0, travelInstalled = 0, bookInstalled = 0, healthandfitnessInstalled = 0, foodanddrinkInstalled = 0;
    int totalUsed = 0, gamesUsed = 0, businessUsed = 0, educationUsed = 0, lifestyleUsed = 0, entertainmentUsed = 0, utilitiesUsed = 0, travelUsed = 0, bookUsed = 0, healthandfitnessUsed = 0, foodanddrinkUsed = 0;

    static final String AUTHORITY = "com.google.android.gm";
    static final String BASE_URI_STRING = "content://" + AUTHORITY;
    static final String LABELS_PARAM = "/labels";
    static final String ACCOUNT_TYPE_GOOGLE = "com.google";

    public static final String NUM_UNREAD_CONVERSATIONS = "numUnreadConversations";
    public static final String CANONICAL_NAME = "canonicalName";


    public final static String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
    public static final String ERROR = "error";

    public List<String> appType;
    public static int noOfWeeks = 0;
    public static String mobileNo,time;
    public boolean calls, messages, contacts, apps, downloads, images, videos, audios, location;
    public static String id = "0";
    public static GoogleAccountCredential googleCredentials;

    public DownloadService() {
        super("MyService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = DownloadService.this;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        id = intent.getStringExtra("Id");
        mobileNo = intent.getStringExtra("mobileNumber");
        noOfWeeks = Integer.parseInt(intent.getStringExtra("Weeks"));
        calls = intent.getBooleanExtra("calls", false);
        messages = intent.getBooleanExtra("messages", false);
        contacts = intent.getBooleanExtra("contacts", false);
        apps = intent.getBooleanExtra("apps", false);
        downloads = intent.getBooleanExtra("downloads", false);
        images = intent.getBooleanExtra("images", false);
        videos = intent.getBooleanExtra("videos", false);
        audios = intent.getBooleanExtra("audios", false);
        location = intent.getBooleanExtra("location", false);
        time= intent.getStringExtra("time");

        if (noOfWeeks > 25) {
            noOfWeeks = 25;
        }
        googleCredentials = CRIFData.googleAccountCredential;
        Log.e("Google Data", String.valueOf(googleCredentials));
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                listCalled = new ArrayList<>();
                allDownload = 0;
                imageDownload = 0;
                audioDownload = 0;
                videoDownload = 0;
                contactCalled = 0;
                games = 0;
                business = 0;
                education = 0;
                lifestyle = 0;
                entertainment = 0;
                utilities = 0;
                travel = 0;
                book = 0;
                healthandfitness = 0;
                foodanddrink = 0;
                gamesInstalled = 0;
                businessInstalled = 0;
                educationInstalled = 0;
                lifestyleInstalled = 0;
                entertainmentInstalled = 0;
                utilitiesInstalled = 0;
                travelInstalled = 0;
                bookInstalled = 0;
                healthandfitnessInstalled = 0;
                foodanddrinkInstalled = 0;
                totalUsed = 0;
                gamesUsed = 0;
                businessUsed = 0;
                educationUsed = 0;
                lifestyleUsed = 0;
                entertainmentUsed = 0;
                utilitiesUsed = 0;
                travelUsed = 0;
                bookUsed = 0;
                healthandfitnessUsed = 0;
                foodanddrinkUsed = 0;
                listContacts = new ArrayList<>();

                if (calls)
                    new GetCallsClass().execute();
                if (messages)
                    new GetSMSClass().execute();
                if (images)
                    new GetImagesClass().execute();
                if (audios)
                    new GetAudioClass().execute();
                if (videos)
                    new GetVideoClass().execute();
                if (downloads)
                    new GetFilesDirectoryClass().execute();
                if (apps)
                    new GetApplicationClass().execute();
                if (contacts)
                    new GetContactsClass().execute();

                if (location)
                    SingleShotLocationProvider.requestSingleUpdate(DownloadService.this,
                            new SingleShotLocationProvider.LocationCallback() {
                                @Override
                                public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                                    Log.d("Location", "my location is " + location.toString());
                                    getAddress(getApplicationContext(), location.latitude, location.longitude, id);
                                }
                            });
            }
        });


        Log.e("Data to show", "Service Entered");
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

    public void getAddress(Context context, float latitude, float longitude, String userId) {
        Address locationAddress;

        locationAddress = getStringAddress(latitude, longitude, context);

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
                    jsonToSend.put("AccountNo", userId);
                    jsonToSend.put("MobileNo", mobileNo);
                    jsonToSend.put("time", time);
                    jsonToSend.put("Location", currentLocation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("Location-Data", "Uploaded");
                new UploadData(HOME_URL + LOCATION_URL, jsonToSend,"Location Uploaded").execute();
//                tvAddress.setText(currentLocation);
            }
        }
    }

    public class UploadData extends AsyncTask<Void, Void, JSONObject> {
        private String URL;
        private String from;
        private JSONObject jsonObjSend;

        public UploadData(String URL, JSONObject jsonObjSend,String from) {
            this.URL = URL;
            this.jsonObjSend = jsonObjSend;
            this.from=from;
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
            Toast.makeText(context, from, Toast.LENGTH_SHORT).show();
        }
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {

        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;
        private DownloadService activity;
        JSONArray jsonArray;

        MakeRequestTask(DownloadService activity, GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getResources().getString(R.string.app_name))
                    .build();
            this.activity = activity;
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                String user = "me";
                ListMessagesResponse inboxResponse = null;
                ListMessagesResponse inbox0to6Response = null;
                ListMessagesResponse inbox6to12Response = null;
                ListMessagesResponse inbox12to18Response = null;
                ListMessagesResponse inbox18to24Response = null;

                ListMessagesResponse outboxResponse = null;
                ListMessagesResponse outbox0to6Response = null;
                ListMessagesResponse outbox6to12Response = null;
                ListMessagesResponse outbox12to18Response = null;
                ListMessagesResponse outbox18to24Response = null;

                ListMessagesResponse unreadResponse = null;
                try {

                    jsonArray = new JSONArray();
                    for (int i = 0; i < noOfWeeks; i++) {

                        Calendar calender = Calendar.getInstance();

                        int start = i * 7;
                        int end;
                        if (i == 0) {
                            end = 7;
                        } else {
                            end = start + 7;
                        }

                        calender.setTimeInMillis(System.currentTimeMillis());
                        calender.add(Calendar.DAY_OF_YEAR, -(start));
                        String toDate = String.valueOf(calender.getTimeInMillis() / 1000);

                        calender.setTimeInMillis(System.currentTimeMillis());
                        calender.add(Calendar.DAY_OF_YEAR, -(end));
                        String fromDate = String.valueOf(calender.getTimeInMillis() / 1000);

                        JSONObject jsonObject = new JSONObject();
                        int inboxFrom0to6 = 0, inboxFrom6to12 = 0, inboxFrom12to18 = 0, inboxFrom18to24 = 0;
                        int outboxFrom0to6 = 0, outboxFrom6to12 = 0, outboxFrom12to18 = 0, outboxFrom18to24 = 0;

                        List<String> inboxLabelsIds = new ArrayList<>();
                        inboxLabelsIds.add("INBOX");

                        inboxResponse = mService.users().messages().list(user).setQ("after:" + fromDate + " before:" + toDate + "").setLabelIds(inboxLabelsIds).execute();
                        List<Message> inboxMessages = inboxResponse.getMessages();
                        while (inboxResponse.getMessages() != null) {
                            inboxMessages.addAll(inboxResponse.getMessages());
                            if (inboxResponse.getNextPageToken() != null) {
                                String pageToken = inboxResponse.getNextPageToken();
                                inboxResponse = mService.users().messages().list(user).setQ("after:" + fromDate + " before:" + toDate + "").setPageToken(pageToken).setLabelIds(inboxLabelsIds).execute();
                            } else {
                                break;
                            }
                        }

                        if (inboxMessages != null)
                            jsonObject.put("NumberofinboundEmailsLastWeeks1to25", inboxMessages.size());
                        else
                            jsonObject.put("NumberofinboundEmailsLastWeeks1to25", "0");

                        jsonObject.put("NumberofinboundEmailsbetween0and6LastWeeks1to25", inboxFrom0to6);
                        jsonObject.put("NumberofinboundEmailsbetween6and12LastWeeks1to25", inboxFrom6to12);
                        jsonObject.put("NumberofinboundEmailsbetween12and18LastWeeks1to25", inboxFrom12to18);
                        jsonObject.put("NumberofinboundEmailsbetween18and24LastWeeks1to25", inboxFrom18to24);


                        List<String> outboxLabelsIds = new ArrayList<>();
                        outboxLabelsIds.add("SENT");

                        outboxResponse = mService.users().messages().list(user).setQ("after:" + fromDate + " before:" + toDate + "").setLabelIds(outboxLabelsIds).execute();
                        List<Message> outboxMessages = outboxResponse.getMessages();
                        while (outboxResponse.getMessages() != null) {
                            outboxMessages.addAll(outboxResponse.getMessages());
                            if (outboxResponse.getNextPageToken() != null) {
                                String pageToken = outboxResponse.getNextPageToken();
                                outboxResponse = mService.users().messages().list(user).setQ("after:" + fromDate + " before:" + toDate + "").setPageToken(pageToken).setLabelIds(outboxLabelsIds).execute();
                            } else {
                                break;
                            }
                        }
                        if (outboxMessages != null)
                            jsonObject.put("NumberofoutboundEmailsLastWeeks1to25", outboxMessages.size());
                        else
                            jsonObject.put("NumberofoutboundEmailsLastWeeks1to25", "0");

                        jsonObject.put("NumberofoutboundEmailsbetween0and6LastWeeks1to25", outboxFrom0to6);
                        jsonObject.put("NumberofoutboundEmailsbetween6and12LastWeeks1to25", outboxFrom6to12);
                        jsonObject.put("NumberofoutboundEmailsbetween12and18LastWeeks1to25", outboxFrom12to18);
                        jsonObject.put("NumberofoutboundEmailsbetween18and24LastWeeks1to25", outboxFrom18to24);

                        List<String> unreadLabelsIds = new ArrayList<>();
                        outboxLabelsIds.add("UNREAD");

                        unreadResponse = mService.users().messages().list(user).setQ("after:" + fromDate + " before:" + toDate + "").setLabelIds(unreadLabelsIds).execute();
                        List<Message> unreadMessages = unreadResponse.getMessages();
                        while (unreadResponse.getMessages() != null) {
                            unreadMessages.addAll(unreadResponse.getMessages());
                            if (unreadResponse.getNextPageToken() != null) {
                                String pageToken = unreadResponse.getNextPageToken();
                                unreadResponse = mService.users().messages().list(user).setQ("after:" + fromDate + " before:" + toDate + "").setPageToken(pageToken).setLabelIds(unreadLabelsIds).execute();
                            } else {
                                break;
                            }
                        }
                        if (unreadMessages != null)
                            jsonObject.put("NumberofnotopenedEmailsLastWeeks1to25", unreadMessages.size());
                        else
                            jsonObject.put("NumberofnotopenedEmailsLastWeeks1to25", "0");


                        jsonObject.put("weekNumber", i);

                        jsonObject.put("NumberofoutboundEmailsfromContactListLastWeeks1to25", 0);
                        jsonObject.put("NumberofinboundEmailsfromContactListLastWeeks1to25", 0);
                        jsonObject.put("AccountNo", id);
                        jsonObject.put("MobileNo", mobileNo);
                        jsonObject.put("time", time);

                        jsonArray.put(jsonObject);
                        //Log.e("Inbox Count", String.valueOf(inboxMessages.size()));
                    }
                } catch (IOException e) {
                    mLastError = e;
                    cancel(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            int size = jsonArray.length();
            new UploadArrayData(HOME_URL + EMAIL_URL, jsonArray,"Emails Uploaded").execute();
        }


//        @Override
//        protected void onCancelled() {
//            if (mLastError != null) {
//                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    GetGmail.showGooglePlayServicesAvailabilityErrorDialog(
//                            ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                    .getConnectionStatusCode());
//                } else if (mLastError instanceof UserRecoverableAuthIOException) {
//                    startActivityForResult(
//                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
//                            GetGmail.REQUEST_AUTHORIZATION);
//                } else {
//                    //showMessage(view, "The following error occurred:\n" + mLastError);
//                    Log.v("Error", mLastError + "");
//                }
//            } else {
//                //showMessage(view, "Request Cancelled.");
//            }
//        }
    }


//    public static Uri getLabelsUri(String account) {
//        return Uri.parse(BASE_URI_STRING + "/" + account + LABELS_PARAM);
//    }
//
//    static String[] getAllAccountNames(Context context) {
//        final Account[] accounts = AccountManager.get(context).getAccountsByType(
//                ACCOUNT_TYPE_GOOGLE);
//        final String[] accountNames = new String[accounts.length];
//        for (int i = 0; i < accounts.length; i++) {
//            accountNames[i] = accounts[i].name;
//        }
//        return accountNames;
//    }

//    public void getAddress(float latitude, float longitude) {
//        Address locationAddress;
//
//        locationAddress = getStringAddress(latitude, longitude);
//
//        if (locationAddress != null) {
//
//            String address = locationAddress.getAddressLine(0);
//            String address1 = locationAddress.getAddressLine(1);
//            String city = locationAddress.getLocality();
//            String state = locationAddress.getAdminArea();
//            String country = locationAddress.getCountryName();
//            String postalCode = locationAddress.getPostalCode();
//
//
//            String currentLocation;
//
//            if (!TextUtils.isEmpty(address)) {
//                currentLocation = address;
//
//                if (!TextUtils.isEmpty(address1))
//                    currentLocation += "," + address1;
//
//                if (!TextUtils.isEmpty(city)) {
//                    currentLocation += "," + city;
//
//                    if (!TextUtils.isEmpty(postalCode))
//                        currentLocation += " - " + postalCode;
//                } else {
//                    if (!TextUtils.isEmpty(postalCode))
//                        currentLocation += "," + postalCode;
//                }
//
//                if (!TextUtils.isEmpty(state))
//                    currentLocation += "," + state;
//
//                if (!TextUtils.isEmpty(country))
//                    currentLocation += "," + country;
//
//                JSONObject jsonToSend = new JSONObject();
//                try {
//                    jsonToSend.put("AccountNo", id);
//                    jsonToSend.put("MobileNo", mobileNo);
//                    jsonToSend.put("time", time);
//
//                    jsonToSend.put("Location", currentLocation);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                new UploadData(HOME_URL + LOCATION_URL, jsonToSend).execute();
////                tvAddress.setText(currentLocation);
//            }
//        }
//    }

    public Address getStringAddress(double latitude, double longitude) {
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


    class GetApplicationClass extends AsyncTask<String, Void, String> {
        int applicationInstalled = 0;
        private UsageStatsManager mUsageStatsManager;
        JSONObject jsonMasterCall = new JSONObject();
        JSONArray arrayCalls = new JSONArray();

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPreExecute() {

            mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        protected String doInBackground(String... strings) {
            PackageManager pm = getPackageManager();

            try {

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -1000);
                final List<UsageStats> stats =
                        mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                                cal.getTimeInMillis(), System.currentTimeMillis());

                jsonMasterCall.put("AccountNo", id);
                jsonMasterCall.put("MobileNo", mobileNo);
                jsonMasterCall.put("time", time);
                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                for (ApplicationInfo packageInfo : packages) {
                    if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                        applicationInstalled++;
                        PackageManager pm2 = context.getPackageManager();
                        ApplicationInfo appInfo = pm2.getApplicationInfo(packageInfo.packageName, 0);
                        String appFile = appInfo.sourceDir;
                        long installed = new File(appFile).lastModified();

                        JSONObject objectCall = new JSONObject();
                        for (int i = 0; i < stats.size(); i++) {
                            Date date = new Date(stats.get(i).getLastTimeUsed());
                            SimpleDateFormat df2 = new SimpleDateFormat("MM-dd-yyyy");
                            String dateText2 = df2.format(System.currentTimeMillis());
                            if (stats.get(i).getPackageName().equals(packageInfo.packageName)) {
                                if (stats.get(i).getLastTimeUsed() > 0) {

                                    String dateText = df2.format(date);


                                    objectCall.put("lastUsedDate", dateText);
                                } else {
                                    objectCall.put("lastUsedDate", dateText2);
                                }
                            }
                        }
                        Date date = new Date(installed);
                        SimpleDateFormat df2 = new SimpleDateFormat("MM-dd-yyyy");
                        String dateText = df2.format(date);

                        objectCall.put("packageName", packageInfo.packageName);
                        objectCall.put("installationDate", dateText);

                        arrayCalls.put(objectCall);
                    }
                }
                jsonMasterCall.put("totalInstalled", applicationInstalled);
                jsonMasterCall.put("installationsArray", arrayCalls);

                JSONObject jsonObjRecv = new JSONObject();
                java.net.URL url = null;
                HttpURLConnection con = null;
                BufferedReader in = null;
                String inputLine;
                String result;
                try {
                    url = new URL(HOME_URL + APPLICATION_URL);
                    String jsonString = "";
                    jsonString = jsonMasterCall.toString();

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

                    Log.e("App-Data", "Uploaded");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {

        }
    }


    class GetCallsClass extends AsyncTask<String, Void, String> {

        JSONArray jsonArray = new JSONArray();


        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... strings) {

            try {

                for (int i = 0; i < noOfWeeks; i++) {

                    String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

                    Calendar calender = Calendar.getInstance();

                    int start = i * 7;
                    int end;
                    if (i == 0) {
                        end = 7;
                    } else {
                        end = start + 7;
                    }
                    calender.setTimeInMillis(System.currentTimeMillis());
                    calender.add(Calendar.DAY_OF_YEAR, -(start));
                    String toDate = String.valueOf(calender.getTimeInMillis());
                    calender.setTimeInMillis(System.currentTimeMillis());
                    calender.add(Calendar.DAY_OF_YEAR, -(end));
                    String fromDate = String.valueOf(calender.getTimeInMillis());


                    String[] whereValue = {fromDate, toDate};
                    ContentResolver cr = getContentResolver();
                    @SuppressLint("MissingPermission") Cursor cur = cr.query(CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue,
                            strOrder);

                    JSONObject jsonObject = getCallJsonData(cur, i);

                    jsonArray.put(jsonObject);
                }
            } catch (Exception e) {
                Log.e("Call error", e.toString());
            }

            return "";
        }

        protected void onPostExecute(String result) {
            Log.e("Call-Data", "Uploaded");
            new UploadArrayData(HOME_URL + CALL_URL, jsonArray,"Calls Uploaded").execute();
        }
    }

    public JSONObject getCallJsonData(Cursor managedCursor, int noOfWeek) throws JSONException {
        int inbound = 0, outbound = 0, missed = 0;
        int from12AMto6AMOutbound = 0, from6AMto12PMOutbound = 0, from12PMto6PMOutbound = 0, from6PMto12PMOutbound = 0;
        int from12AMto6AMInbound = 0, from6AMto12PMInbound = 0, from12PMto6PMInbound = 0, from6PMto12PMInbound = 0;
        int avgInboundCall = 0, avgOutboundCall = 0;
        int internationalInbound = 0, internationalOutbound = 0;

        JSONObject jsonToUpload = new JSONObject();

        if (managedCursor != null) {

            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);
                int dircode = Integer.parseInt(callType);

                long msDiff = Calendar.getInstance().getTimeInMillis() - Long.parseLong(callDate);
                long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

                if (daysDiff < 85 && contactExists(context, phNumber)) {
                    listContacts.add(phNumber);
                }


                String callDateForSplit = getDate(Long.parseLong(callDate));
                String[] callTimeSplit = callDateForSplit.split(" ");
                String callHour = firstTwo(callTimeSplit[1]);


                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:

                        outbound++;
                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                        try {
                            Phonenumber.PhoneNumber usNumberProto = phoneUtil.parse(phNumber, GetCountryZipCodeName());
                            boolean isValid = phoneUtil.isValidNumber(usNumberProto);
                            String usNumber = phoneUtil.format(usNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                            if (isValid) {

                                if (contactExists(context, phNumber)) {
                                    contactCalled++;
                                    listCalled.add(noOfWeek);
                                }
                            }
                            if (Integer.parseInt(GetCountryZipCode()) != getCtryCode(usNumber)) {
                                internationalOutbound++;
                            }

                        } catch (NumberParseException e) {
                            System.err.println("NumberParseException was thrown: " + e.toString());
                        }

                        avgOutboundCall = avgOutboundCall + Integer.parseInt(callDuration);
                        if (Integer.parseInt(callHour) > 0 && Integer.parseInt(callHour) < 7) {
                            from12AMto6AMOutbound++;
                        } else if (Integer.parseInt(callHour) > 6 && Integer.parseInt(callHour) < 13) {
                            from6AMto12PMOutbound++;
                        } else if (Integer.parseInt(callHour) > 12 && Integer.parseInt(callHour) < 19) {
                            from12PMto6PMOutbound++;
                        } else {
                            from6PMto12PMOutbound++;
                        }

                        break;
                    case CallLog.Calls.INCOMING_TYPE:

                        inbound++;
                        PhoneNumberUtil phoneUtil2 = PhoneNumberUtil.getInstance();
                        try {
                            Phonenumber.PhoneNumber usNumberProto = phoneUtil2.parse(phNumber, GetCountryZipCodeName());
                            boolean isValid = phoneUtil2.isValidNumber(usNumberProto);
                            String usNumber = phoneUtil2.format(usNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                            if (isValid) {

                            }
                            if (Integer.parseInt(GetCountryZipCode()) != getCtryCode(usNumber)) {
                                internationalInbound++;
                            }

                        } catch (NumberParseException e) {
                            System.err.println("NumberParseException was thrown: " + e.toString());
                        }

                        avgInboundCall = avgInboundCall + Integer.parseInt(callDuration);
                        if (Integer.parseInt(callHour) > 0 && Integer.parseInt(callHour) < 7) {
                            from12AMto6AMInbound++;
                        } else if (Integer.parseInt(callHour) > 6 && Integer.parseInt(callHour) < 13) {
                            from6AMto12PMInbound++;
                        } else if (Integer.parseInt(callHour) > 12 && Integer.parseInt(callHour) < 19) {
                            from12PMto6PMInbound++;
                        } else {
                            from6PMto12PMInbound++;
                        }
                        break;
                    case CallLog.Calls.MISSED_TYPE:

                        missed++;

                        PhoneNumberUtil phoneUtil3 = PhoneNumberUtil.getInstance();
                        try {
                            Phonenumber.PhoneNumber usNumberProto = phoneUtil3.parse(phNumber, GetCountryZipCodeName());
                            boolean isValid = phoneUtil3.isValidNumber(usNumberProto);
                            String usNumber = phoneUtil3.format(usNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                            if (isValid) {

                            }


                        } catch (NumberParseException e) {
                            System.err.println("NumberParseException was thrown: " + e.toString());
                        }
                        break;

                    case CallLog.Calls.REJECTED_TYPE:
                        missed++;
                        break;
                }


            }
        }
        managedCursor.close();

        jsonToUpload.put("AccountNo", id);
        jsonToUpload.put("MobileNo", mobileNo);
        jsonToUpload.put("time", time);
        jsonToUpload.put("numberOfInboundCalls", inbound);
        jsonToUpload.put("numberOfOutboundCalls", outbound);
        jsonToUpload.put("numberOfMissedCalls", missed);
        jsonToUpload.put("numberOfInboundCallsBetween0to6", from12AMto6AMInbound);
        jsonToUpload.put("numberOfInboundCallsBetween6to12", from6AMto12PMInbound);
        jsonToUpload.put("numberOfInboundCallsBetween12to18", from12PMto6PMInbound);
        jsonToUpload.put("numberOfInboundCallsBetween18to24", from6PMto12PMInbound);
        jsonToUpload.put("numberOfOutboundCallsBetween0to6", from12AMto6AMOutbound);
        jsonToUpload.put("numberOfOutboundCallsBetween6to12", from6AMto12PMOutbound);
        jsonToUpload.put("numberOfOutboundCallsBetween12to18", from12PMto6PMOutbound);
        jsonToUpload.put("numberOfOutboundCallsBetween18to24", from6PMto12PMOutbound);
        jsonToUpload.put("averageInboundCallDuration", avgInboundCall / 60);
        jsonToUpload.put("averageOutboundCallDuration", avgOutboundCall / 60);
        jsonToUpload.put("numberOfInternationalOutboundCalls", internationalOutbound);
        jsonToUpload.put("numberOfInternationalInboundCalls", internationalInbound);
        jsonToUpload.put("numberOfInboundCallsRecieved", inbound);
        jsonToUpload.put("numberOfOutboundPhoneCalled", outbound);
        jsonToUpload.put("weekNumber", noOfWeek);

        return jsonToUpload;
    }

    class GetSMSClass extends AsyncTask<String, Void, String> {

        JSONArray jsonArray = new JSONArray();


        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... strings) {


            try {

                for (int i = 0; i < noOfWeeks; i++) {
                    Uri uri = Uri.parse("content://sms");
//                            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                    String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

                    Calendar calender = Calendar.getInstance();

                    int start = i * 7;
                    int end;
                    if (i == 0) {
                        end = 7;
                    } else {
                        end = start + 7;
                    }

                    calender.setTimeInMillis(System.currentTimeMillis());
                    calender.add(Calendar.DAY_OF_YEAR, -(start));
                    String toDate = String.valueOf(calender.getTimeInMillis());

                    calender.setTimeInMillis(System.currentTimeMillis());
                    calender.add(Calendar.DAY_OF_YEAR, -(end));
                    String fromDate = String.valueOf(calender.getTimeInMillis());


                    String[] whereValue = {fromDate, toDate};

                    @SuppressLint("MissingPermission") Cursor cur = getContentResolver().query(uri, null, Telephony.Sms.DATE + " BETWEEN ? AND ?", whereValue,
                            strOrder);

//                            CursorLoader cursorLoader = new CursorLoader(MainActivity.this,
//                                    CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue, strOrder);
//                            Cursor managedCursor = cursorLoader.loadInBackground();

                    JSONObject jsonObject = getMsgJsonData(cur, i);

                    jsonArray.put(jsonObject);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            return "";
        }

        protected void onPostExecute(String result) {
            Log.e("Sms-Data", "Uploaded");
            new UploadArrayData(HOME_URL + SMS_URL, jsonArray,"SMS Uploaded").execute();
        }
    }

    public JSONObject getMsgJsonData(Cursor cursor, int numberOfWeek) throws JSONException {
        int inboxMessage = 0, outboxMessage = 0;
        int from12AMto6AMInbox = 0, from6AMto12PMInbox = 0, from12PMto6PMInbox = 0, from6PMto12PMInbox = 0;
        int from12AMto6AMOutbox = 0, from6AMto12PMOutbox = 0, from12PMto6PMOutbox = 0, from6PMto12PMOutbox = 0;
        int internationalInbox = 0, internationalOutbox = 0;
        JSONObject jsonToUpload = new JSONObject();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
                    String smsnumber = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
                    String smsdate = cursor.getString(cursor.getColumnIndexOrThrow("date")).toString();
                    Date smsDayTime = new Date(Long.valueOf(smsdate));
                    String smstype = cursor.getString(cursor.getColumnIndexOrThrow("type")).toString();
                    String typeOfSMS = null;

                    Pattern patrn = Pattern.compile("[a-zA-Z]");
                    Matcher mater = patrn.matcher(smsnumber);
                    long msDiff = Calendar.getInstance().getTimeInMillis() - Long.parseLong(smsdate);
                    long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
                    if (!mater.find()) {

                        if (daysDiff > 0 && daysDiff < 85 && contactExists(context, smsnumber)) {
                            listContacts.add(smsnumber);
                        }
                    }

                    String callDateForSplit = getDate(Long.parseLong(smsdate));
                    String[] callTimeSplit = callDateForSplit.split(" ");
                    String callHour = firstTwo(callTimeSplit[1]);
                    switch (Integer.parseInt(smstype)) {
                        case 1:
                            typeOfSMS = "INBOX";
                            inboxMessage++;
                            if (Integer.parseInt(callHour) > 0 && Integer.parseInt(callHour) < 7) {
                                from12AMto6AMInbox++;
                            } else if (Integer.parseInt(callHour) > 6 && Integer.parseInt(callHour) < 13) {
                                from6AMto12PMInbox++;
                            } else if (Integer.parseInt(callHour) > 12 && Integer.parseInt(callHour) < 19) {
                                from12PMto6PMInbox++;
                            } else {
                                from6PMto12PMInbox++;
                            }

                            Pattern p = Pattern.compile("[a-zA-Z]");
                            Matcher m = p.matcher(smsnumber);

                            if (!m.find()) {
                                PhoneNumberUtil phoneUtil2 = PhoneNumberUtil.getInstance();
                                try {
                                    Phonenumber.PhoneNumber usNumberProto = phoneUtil2.parse(smsnumber, GetCountryZipCodeName());
                                    boolean isValid = phoneUtil2.isValidNumber(usNumberProto);
                                    String usNumber = phoneUtil2.format(usNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);

                                    if (Integer.parseInt(GetCountryZipCode()) != getCtryCode(usNumber)) {
                                        internationalInbox++;
                                    }

                                } catch (NumberParseException e) {
                                    System.err.println("NumberParseException was thrown: " + e.toString());
                                }
                            }
                            break;

                        case 2:
                            typeOfSMS = "SENT";
                            outboxMessage++;
                            if (Integer.parseInt(callHour) > 0 && Integer.parseInt(callHour) < 7) {
                                from12AMto6AMOutbox++;
                            } else if (Integer.parseInt(callHour) > 6 && Integer.parseInt(callHour) < 13) {
                                from6AMto12PMOutbox++;
                            } else if (Integer.parseInt(callHour) > 12 && Integer.parseInt(callHour) < 19) {
                                from12PMto6PMOutbox++;
                            } else {
                                from6PMto12PMOutbox++;
                            }

                            Pattern pat = Pattern.compile("[a-zA-Z]");
                            Matcher mat = pat.matcher(smsnumber);

                            if (!mat.find()) {
                                PhoneNumberUtil phoneUtil2 = PhoneNumberUtil.getInstance();
                                try {
                                    Phonenumber.PhoneNumber usNumberProto = phoneUtil2.parse(smsnumber, GetCountryZipCodeName());
                                    boolean isValid = phoneUtil2.isValidNumber(usNumberProto);
                                    String usNumber = phoneUtil2.format(usNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);

                                    if (Integer.parseInt(GetCountryZipCode()) != getCtryCode(usNumber)) {
                                        internationalOutbox++;
                                    }

                                } catch (NumberParseException e) {
                                    System.err.println("NumberParseException was thrown: " + e.toString());
                                }
                            }
                            break;

                        case 3:
                            typeOfSMS = "DRAFT";
                            break;
                    }


                    cursor.moveToNext();
                }
            }
        }
        cursor.close();

        jsonToUpload.put("AccountNo", id);
        jsonToUpload.put("MobileNo", mobileNo);
        jsonToUpload.put("time", time);
        jsonToUpload.put("numberOfInboundMessages", inboxMessage);
        jsonToUpload.put("numberOfOutboundMessages", outboxMessage);
        jsonToUpload.put("numberOfInboundMessagesBetween0to6", from12AMto6AMInbox);
        jsonToUpload.put("numberOfInboundMessagesBetween6to12", from6AMto12PMInbox);
        jsonToUpload.put("numberOfInboundMessagesBetween12to18", from12PMto6PMInbox);
        jsonToUpload.put("numberOfInboundMessagesBetween18to24", from6PMto12PMInbox);
        jsonToUpload.put("numberOfOutboundMessagesBetween0to6", from12AMto6AMOutbox);
        jsonToUpload.put("numberOfOutboundMessagesBetween6to12", from6AMto12PMOutbox);
        jsonToUpload.put("numberOfOutboundMessagesBetween12to18", from12PMto6PMOutbox);
        jsonToUpload.put("numberOfOutboundMessagesBetwee18to24", from6PMto12PMOutbox);
        jsonToUpload.put("numberOfInternationalInboundMessages", internationalInbox);
        jsonToUpload.put("numberOfInternationalOutboundMessages", internationalOutbox);
        jsonToUpload.put("numberOfOutboundPhoneNumberMessages", inboxMessage);
        jsonToUpload.put("numberOfInboundPhoneNumberMessages", outboxMessage);
        jsonToUpload.put("weekNumber", numberOfWeek);


        return jsonToUpload;
    }

    class GetContactsClass extends AsyncTask<String, Void, String> {
        JSONArray jsonToUpload = new JSONArray();
        int numberOfContact = 0, numberOfInternational = 0, activeContacts = 0;


        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... strings) {

            try {
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);

                if ((cur != null ? cur.getCount() : 0) > 0) {

                    while (cur != null && cur.moveToNext()) {
                        String id = cur.getString(
                                cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));

                        if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));

                                Pattern pat = Pattern.compile("[a-zA-Z]");
                                Matcher mat = pat.matcher(phoneNo);

                                if (!mat.find() && !phoneNo.contains("*") && !phoneNo.contains("#")) {
                                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                                    try {
                                        Phonenumber.PhoneNumber usNumberProto = phoneUtil.parse(phoneNo, GetCountryZipCodeName());
                                        boolean isValid = phoneUtil.isValidNumber(usNumberProto);
                                        String usNumber = phoneUtil.format(usNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                                        if (isValid) {
                                            numberOfContact++;
                                        }
                                        if (Integer.parseInt(GetCountryZipCode()) != getCtryCode(usNumber)) {
                                            numberOfInternational++;
                                        }
                                    } catch (NumberParseException e) {
                                        System.err.println("NumberParseException was thrown: " + e.toString());
                                    }
                                }
                            }
                            pCur.close();
                        }
                    }
                }
                if (cur != null) {
                    cur.close();
                }


                for (int i = 0; i < noOfWeeks; i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("AccountNo", id);
                    jsonObject.put("MobileNo", mobileNo);
                    jsonObject.put("time", time);
                    jsonObject.put("Numberofcontacts", numberOfContact);
                    jsonObject.put("Numberofinternationalcontacts", numberOfInternational);

                    HashSet<String> hashSet = new HashSet<String>();
                    hashSet.addAll(listContacts);
                    listContacts.clear();
                    listContacts.addAll(hashSet);

                    jsonObject.put("Numberofactivecontacts", listContacts.size());

                    jsonObject.put("weekNumber", i);
                    jsonObject.put("NumnbrofcontactscalledLastWeeks1to25", Collections.frequency(listCalled, i));
                    jsonToUpload.put(jsonObject);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            return "";
        }

        protected void onPostExecute(String result) {

            new UploadArrayData(HOME_URL + CONTACT_URL, jsonToUpload,"Contacts Uploaded").execute();
        }
    }

    class GetImagesClass extends AsyncTask<String, Void, String> {
        long avgInternalImageSize = 0;
        long internalImageSize = 0;
        int imageCountInternal = 0;
        long avgExternalImageSize = 0;
        int imageCountExternal = 0;
        long externalImageSize = 0;
        JSONObject jsonToUpload = new JSONObject();

        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... strings) {
            try {

                jsonToUpload.put("AccountNo", id);
                jsonToUpload.put("MobileNo", mobileNo);
                jsonToUpload.put("time", time);

                // getting image data
                {
                    final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};//get all columns of type images
                    final String orderBy = MediaStore.Images.Media.DATE_TAKEN;//order data by date

                    ContentResolver cr = getContentResolver();
                    Cursor imageInternalCursor = cr.query(
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI, columns, null,
                            null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

                    if (imageInternalCursor.getCount() > 0) {
                        imageCountInternal = imageInternalCursor.getCount();
                        for (int i = 0; i < imageInternalCursor.getCount(); i++) {
                            imageInternalCursor.moveToPosition(i);
                            int dataColumnIndex = imageInternalCursor.getColumnIndex(MediaStore.Images.Media.SIZE);//get column index
                            internalImageSize = internalImageSize + imageInternalCursor.getLong(dataColumnIndex);//get Image size from column index

                        }
                        avgInternalImageSize = internalImageSize / imageInternalCursor.getCount();
                    }

                    jsonToUpload.put("NumberofimagefilesstoredIntertnal", imageCountInternal);
                    jsonToUpload.put("AveragesizeofimagefilesstoredInternal", avgInternalImageSize);

                    imageInternalCursor.close();

                    Cursor imageExternalCursor = cr.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                            null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

                    if (imageExternalCursor.getCount() > 0) {
                        imageCountExternal = imageExternalCursor.getCount();
                        for (int i = 0; i < imageExternalCursor.getCount(); i++) {
                            imageExternalCursor.moveToPosition(i);
                            int dataColumnIndex = imageExternalCursor.getColumnIndex(MediaStore.Images.Media.SIZE);//get column index
                            externalImageSize = externalImageSize + imageExternalCursor.getLong(dataColumnIndex);//get Image size from column index

                        }
                        avgExternalImageSize = externalImageSize / imageExternalCursor.getCount();
                    }
                    jsonToUpload.put("NumberofimagefilesstoredExternal", imageCountExternal);
                    jsonToUpload.put("AveragesizeofimagefilesstoredExternal", avgExternalImageSize);
                    jsonToUpload.put("StorageusedInternal", 0);
                    jsonToUpload.put("StorageusedExternal", 0);

                    imageExternalCursor.close();
                }
            } catch (Exception e) {

            }
            return "";
        }

        protected void onPostExecute(String result) {
            Log.e("Images-Data", "Uploaded");
            new UploadData(HOME_URL + IMAGE_URL, jsonToUpload,"Images Uploded").execute();
        }
    }

    class GetAudioClass extends AsyncTask<String, Void, String> {
        long avgInternalAudioSize = 0;
        int audioCountInternal = 0;
        long internalAudioSize = 0;
        long avgExternalAudioSize = 0;
        int countAudioExternal = 0;
        long externalAudioSize = 0;
        JSONObject jsonToUpload = new JSONObject();

        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... strings) {
            try {

                jsonToUpload.put("AccountNo", id);
                jsonToUpload.put("MobileNo", mobileNo);
                jsonToUpload.put("time", time);

                {
                    final String[] columns = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.SIZE};//get all columns of type images
                    final String orderBy = MediaStore.Audio.Media.DATE_ADDED;//order data by date
                    ContentResolver cr = getContentResolver();
                    Cursor audioInternalCursor = cr.query(
                            MediaStore.Audio.Media.INTERNAL_CONTENT_URI, columns, null,
                            null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

                    if (audioInternalCursor.getCount() > 0) {
                        audioCountInternal = audioInternalCursor.getCount();
                        for (int i = 0; i < audioInternalCursor.getCount(); i++) {
                            audioInternalCursor.moveToPosition(i);
                            int dataColumnIndex = audioInternalCursor.getColumnIndex(MediaStore.Audio.Media.SIZE);//get column index
                            internalAudioSize = internalAudioSize + audioInternalCursor.getLong(dataColumnIndex);//get Image size from column index

                        }
                        avgInternalAudioSize = internalAudioSize / audioInternalCursor.getCount();
                    }
                    jsonToUpload.put("NumberofaudiofilesstoredIntertnal", audioCountInternal);
                    jsonToUpload.put("AveragesizeofaudiofilesstoredInternal", avgInternalAudioSize);
                    audioInternalCursor.close();

                    Cursor audioExternalCursor = cr.query(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null,
                            null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

                    if (audioExternalCursor.getCount() > 0) {
                        countAudioExternal = audioExternalCursor.getCount();
                        for (int i = 0; i < audioExternalCursor.getCount(); i++) {
                            audioExternalCursor.moveToPosition(i);
                            int dataColumnIndex = audioExternalCursor.getColumnIndex(MediaStore.Audio.Media.SIZE);//get column index
                            externalAudioSize = externalAudioSize + audioExternalCursor.getLong(dataColumnIndex);//get Image size from column index

                        }
                        avgExternalAudioSize = externalAudioSize / audioExternalCursor.getCount();
                    }
                    jsonToUpload.put("NumberofaudiofilesstoredExternal", countAudioExternal);
                    jsonToUpload.put("AveragesizeofaudiofilesstoredExternal", avgExternalAudioSize);
                    jsonToUpload.put("StorageusedInternal", 0);
                    jsonToUpload.put("StorageusedExternal", 0);

                    audioExternalCursor.close();
                }
            } catch (Exception e) {

            }
            return "";
        }

        protected void onPostExecute(String result) {
            Log.e("Audio-Data", "Uploaded");
            new UploadData(HOME_URL + AUDIO_URL, jsonToUpload,"Audios Uploaded").execute();
        }
    }

    class GetVideoClass extends AsyncTask<String, Void, String> {
        long avgInternalVideoSize = 0;
        int countVideoInternal = 0;
        long internalVideoSize = 0;
        long avgExternalVideoSize = 0;
        int countVideoExternal = 0;
        long externalVideoSize = 0;
        JSONObject jsonToUpload = new JSONObject();


        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... strings) {
            try {

                jsonToUpload.put("AccountNo", id);
                jsonToUpload.put("MobileNo", mobileNo);
                jsonToUpload.put("time", time);
                {
                    final String[] columns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE};//get all columns of type images
                    final String orderBy = MediaStore.Video.Media.DATE_ADDED;//order data by date
                    ContentResolver cr = getContentResolver();
                    Cursor videoInternalCursor = cr.query(
                            MediaStore.Video.Media.INTERNAL_CONTENT_URI, columns, null,
                            null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

                    if (videoInternalCursor.getCount() > 0) {
                        countVideoInternal = videoInternalCursor.getCount();
                        for (int i = 0; i < videoInternalCursor.getCount(); i++) {
                            videoInternalCursor.moveToPosition(i);
                            int dataColumnIndex = videoInternalCursor.getColumnIndex(MediaStore.Video.Media.SIZE);//get column index
                            internalVideoSize = internalVideoSize + videoInternalCursor.getLong(dataColumnIndex);//get Image size from column index

                        }
                        avgInternalVideoSize = internalVideoSize / videoInternalCursor.getCount();
                    }
                    jsonToUpload.put("NumberofvideofilesstoredIntertnal", countVideoInternal);
                    jsonToUpload.put("AveragesizeofvideofilesstoredInternal", avgInternalVideoSize);

                    videoInternalCursor.close();

                    Cursor videoExternalCursor = cr.query(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null,
                            null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

                    if (videoExternalCursor.getCount() > 0) {
                        countVideoExternal = videoExternalCursor.getCount();
                        for (int i = 0; i < videoExternalCursor.getCount(); i++) {
                            videoExternalCursor.moveToPosition(i);
                            int dataColumnIndex = videoExternalCursor.getColumnIndex(MediaStore.Video.Media.SIZE);//get column index
                            externalVideoSize = externalVideoSize + videoExternalCursor.getLong(dataColumnIndex);//get Image size from column index

                        }
                        avgExternalVideoSize = externalVideoSize / videoExternalCursor.getCount();
                    }
                    jsonToUpload.put("NumberofvideofilesstoredExternal", countVideoExternal);
                    jsonToUpload.put("AveragesizeofvideofilesstoredExternal", avgExternalVideoSize);
                    jsonToUpload.put("StorageusedInternal", 0);
                    jsonToUpload.put("StorageusedExternal", 0);

                    videoExternalCursor.close();
                }

            } catch (Exception e) {

            }
            return "";
        }

        protected void onPostExecute(String result) {
            Log.e("Video-Data", "Uploaded");
            new UploadData(HOME_URL + VIDEO_URL, jsonToUpload,"Videos Uploaded").execute();
        }
    }

    class GetFilesDirectoryClass extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... strings) {
            File downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            getFilesFromDir(downloadDir);
            return "";
        }

        protected void onPostExecute(String result) {

        }
    }


    public boolean contactExists(Context context, String number) {
        // number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public void getFilesFromDir(File filesFromSD) {

        try {

            JSONObject jsonToUpload = new JSONObject();
            jsonToUpload.put("AccountNo", id);
            jsonToUpload.put("MobileNo", mobileNo);
            jsonToUpload.put("time", time);

            File listAllFiles[] = filesFromSD.listFiles();

            if (listAllFiles != null && listAllFiles.length > 0) {
                allDownload = listAllFiles.length;
                for (File currentFile : listAllFiles) {
                    if (currentFile.isDirectory()) {
                        getFilesFromDir(currentFile);
                        return;
                    } else {
                        Date lastModDate = new Date(currentFile.lastModified());
                        if (currentFile.getName().contains(".jpg") || currentFile.getName().contains(".jpeg") || currentFile.getName().contains(".png")) {
                            imageDownload++;
                        } else if (currentFile.getName().contains(".mp4") || currentFile.getName().contains(".mkv")) {
                            videoDownload++;
                        } else if (currentFile.getName().contains(".mp3")) {
                            audioDownload++;
                        } else {

                        }
//                    if (currentFile.getName().endsWith("")) {
//                        // File absolute path
//                        Log.e("File path", currentFile.getAbsolutePath());
//                        // File Name
//                        Log.e("File path", currentFile.getName());
//                    }
                    }
                }
            }

            jsonToUpload.put("AudioCount", audioDownload);
            jsonToUpload.put("DownloadCount", allDownload);
            jsonToUpload.put("ImageCount", imageDownload);
            jsonToUpload.put("VideoCount", videoDownload);

            Log.e("Downloads-Data", "Uploaded");
            new UploadData(HOME_URL + DOWNLOAD_URL, jsonToUpload,"Downloads Uploaded").execute();

        } catch (Exception e) {
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public class UploadArrayData extends AsyncTask<Void, Void, JSONObject> {
        private String URL;
        private String from;
        private JSONArray jsonObjSend;

        public UploadArrayData(String URL, JSONArray jsonObjSend,String from) {
            this.URL = URL;
            this.from=from;
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
            Toast.makeText(context, from, Toast.LENGTH_SHORT).show();
        }


    }

    public String firstTwo(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }

    public String GetCountryZipCodeName() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryID;
    }

    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }


    public int getCtryCode(String phoneNumber) {
        String ctryCodeAtIndex1 = phoneNumber.substring(1, 2);
        Integer ctryCode = 0;
        String ctryCodeStr = "0";

        List<Integer> ctryCodeList = countryCodeMap.get(Integer.valueOf(ctryCodeAtIndex1));
        if (ctryCodeList.isEmpty()) {
            ctryCode = Integer.valueOf(ctryCodeAtIndex1);
            return ctryCode.intValue();
        }
        String ctryCodeAtIndex2 = phoneNumber.substring(2, 3);
        for (Integer ctryCodePrefix : ctryCodeList) {
            if (Integer.valueOf(ctryCodeAtIndex2) == ctryCodePrefix) {
                ctryCodeStr = phoneNumber.substring(1, 3);
                ctryCode = Integer.valueOf(ctryCodeStr);
                return ctryCode.intValue();

            }
        }
        ctryCodeStr = phoneNumber.substring(1, 4);
        ctryCode = Integer.valueOf(ctryCodeStr);
        return ctryCode.intValue();
    }

    private List<Integer> forCtryCodePrefix1 = new ArrayList<Integer>();
    @SuppressWarnings("serial")
    List<Integer> forCtryCodePrefix2 = new ArrayList<Integer>() {
        {
            add(0);
            add(7);
            add(8);

        }
    };
    @SuppressWarnings("serial")
    private List<Integer> forCtryCodePrefix3 = new ArrayList<Integer>() {
        {
            add(0);
            add(1);
            add(2);
            add(3);
            add(4);
            add(6);
            add(9);
        }
    };
    @SuppressWarnings("serial")
    private List<Integer> forCtryCodePrefix4 = new ArrayList<Integer>() {
        {
            add(0);
            add(1);
            add(3);
            add(4);
            add(5);
            add(6);
            add(7);
            add(8);
            add(9);
        }
    };
    @SuppressWarnings("serial")
    private List<Integer> forCtryCodePrefix5 = new ArrayList<Integer>() {
        {
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
            add(7);
            add(8);
        }
    };
    @SuppressWarnings("serial")
    private List<Integer> forCtryCodePrefix6 = new ArrayList<Integer>() {
        {
            add(0);
            add(1);
            add(3);
            add(4);
            add(5);
            add(6);

        }
    };
    private List<Integer> forCtryCodePrefix7 = new ArrayList<Integer>();
    @SuppressWarnings("serial")
    private List<Integer> forCtryCodePrefix8 = new ArrayList<Integer>() {
        {
            add(1);
            add(3);
            add(4);
            add(6);
            add(9);

        }
    };
    @SuppressWarnings("serial")
    private List<Integer> forCtryCodePrefix9 = new ArrayList<Integer>() {
        {
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
            add(8);
        }
    };
    @SuppressWarnings("serial")
    private Map<Integer, List<Integer>> countryCodeMap = new HashMap<Integer, List<Integer>>() {
        {
            put(1, forCtryCodePrefix1);
            put(2, forCtryCodePrefix2);
            put(3, forCtryCodePrefix3);
            put(4, forCtryCodePrefix4);
            put(5, forCtryCodePrefix5);
            put(6, forCtryCodePrefix6);
            put(7, forCtryCodePrefix7);
            put(8, forCtryCodePrefix8);
            put(9, forCtryCodePrefix9);

        }
    };

    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


}
