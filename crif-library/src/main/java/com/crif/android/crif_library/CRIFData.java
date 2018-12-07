package com.crif.android.crif_library;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CRIFData {

    public static DownloadService localService;
    public static boolean isBound = false;
    public static String HOME_URL = "http://18.191.145.152/api/";
    public static String EMAIL_URL = "Emailtbs/PostEmailtb";
    public static Context contextService;
    public static String mobileNo, time;
    public static GoogleAccountCredential googleAccountCredential;

    public static void UPLOAD_DATA(Context context, String id, String mobileNumber, String noOfWeeks, String strTime, boolean calls,
                                   boolean messages, boolean contacts, boolean emails, boolean apps, boolean downloads,
                                   boolean images, boolean videos, boolean audios, boolean location) {

        mobileNo = mobileNumber;
        time = strTime;

        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("Id", id);
        intent.putExtra("mobileNumber", mobileNumber);
        intent.putExtra("Weeks", noOfWeeks);
        intent.putExtra("calls", calls);
        intent.putExtra("messages", messages);
        intent.putExtra("contacts", contacts);
        intent.putExtra("apps", apps);
        intent.putExtra("downloads", downloads);
        intent.putExtra("images", images);
        intent.putExtra("videos", videos);
        intent.putExtra("audios", audios);
        intent.putExtra("location", location);
        intent.putExtra("time", time);
        //intent.putExtra("GoogleCredentials", String.valueOf(googleCredentials));
        if(isMyServiceRunning(context,DownloadService.class))
        {
            context.stopService(intent);
            context.startService(intent);
        }else
        {
            context.startService(intent);
        }

        contextService = context;

    }


    public static void UPLOAD_DATA(Context context, String id, String mobileNumber, String noOfWeeks, String strTime, boolean calls,
                                   boolean messages, boolean contacts, boolean emails, boolean apps, boolean downloads,
                                   boolean images, boolean videos, boolean audios, boolean location, GoogleAccountCredential googleCredentials) {

        mobileNo = mobileNumber;
        time = strTime;

        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("Id", id);
        intent.putExtra("mobileNumber", mobileNumber);
        intent.putExtra("Weeks", noOfWeeks);
        intent.putExtra("calls", calls);
        intent.putExtra("messages", messages);
        intent.putExtra("contacts", contacts);
        intent.putExtra("apps", apps);
        intent.putExtra("downloads", downloads);
        intent.putExtra("images", images);
        intent.putExtra("videos", videos);
        intent.putExtra("audios", audios);
        intent.putExtra("location", location);
        intent.putExtra("time", time);
        googleAccountCredential = googleCredentials;
        //intent.putExtra("GoogleCredentials", String.valueOf(googleCredentials));
        if(isMyServiceRunning(context,DownloadService.class))
        {
            context.stopService(intent);
            context.startService(intent);
        }else
        {
            context.startService(intent);
        }

        contextService = context;

        if (emails)
            new MakeRequestTask(contextService, Integer.parseInt(id), Integer.parseInt(noOfWeeks), googleCredentials).execute();

    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private static class MakeRequestTask extends AsyncTask<Void, Void, Void> {

        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;
        private DownloadService activity;
        private Context context;
        private int noOfWeeks;
        private int id;
        JSONArray jsonArray;

        MakeRequestTask(Context context, int id, int noOfWeeks, GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(context.getResources().getString(R.string.app_name))
                    .build();
            this.context = context;
            this.noOfWeeks = noOfWeeks;
            this.id = id;
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

            new UploadArrayData(HOME_URL + EMAIL_URL, jsonArray).execute();
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

    public static class UploadArrayData extends AsyncTask<Void, Void, JSONObject> {
        private String URL;
        private JSONArray jsonObjSend;

        public UploadArrayData(String URL, JSONArray jsonObjSend) {
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

        }


    }
}
