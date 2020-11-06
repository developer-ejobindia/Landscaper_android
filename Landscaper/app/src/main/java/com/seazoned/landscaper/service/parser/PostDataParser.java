package com.seazoned.landscaper.service.parser;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.other.MyCustomProgressDialog;
import com.seazoned.landscaper.service.util.Util;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created on 16/9/16.
 *
 * @author Manas
 */
public class PostDataParser {
    ProgressDialog dialog;

    private void showpDialog() {
        if (!dialog.isShowing())
            dialog.show();
    }

    private void hidepDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }

    public PostDataParser(final Context context, String url, final Map<String, String> params, final boolean flag, final OnGetResponseListner listner) {
        if (!Util.isConnected(context)) {
            Util.showSnakBar(context, context.getResources().getString(R.string.internectconnectionerror));
            //TastyToast.makeText(context, "No internet connections.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            listner.onGetResponse(null);
            return;
        }
        if (flag) {
            dialog = MyCustomProgressDialog.ctor(context);
            dialog.setCancelable(false);
            dialog.setMessage("Please wait...");
            showpDialog();
        }
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Util util = new Util();
                            JSONObject jobj = util.getjsonobject(response);
                            listner.onGetResponse(jobj);
                        } catch (Exception e) {
                            listner.onGetResponse(null);
                            e.printStackTrace();
                        }
                        if (flag)
                            hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (flag)
                    hidepDialog();
                Util.showSnakBar(context, context.getResources().getString(R.string.networkerror));
                listner.onGetResponse(null);
                VolleyLog.d("Error: " + error.getMessage());
                //TastyToast.makeText(context, "Network error.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (AppData.sToken != null) {
                    headers.put("token", AppData.sToken);
                }
                return headers;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(postRequest);

    }
    public PostDataParser(final Context context, String url, final Map<String, String> params, final boolean flag,final boolean noHeader, final OnGetResponseListner listner) {
        if (!Util.isConnected(context)) {
            Util.showSnakBar(context, context.getResources().getString(R.string.internectconnectionerror));
            //TastyToast.makeText(context, "No internet connections.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            listner.onGetResponse(null);
            return;
        }
        if (flag) {
            dialog = MyCustomProgressDialog.ctor(context);
            dialog.setCancelable(false);
            dialog.setMessage("Please wait...");
            showpDialog();
        }
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Util util = new Util();
                            JSONObject jobj = util.getjsonobject(response);
                            listner.onGetResponse(jobj);
                        } catch (Exception e) {
                            listner.onGetResponse(null);
                            e.printStackTrace();
                        }
                        if (flag)
                            hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (flag)
                    hidepDialog();
                Util.showSnakBar(context, context.getResources().getString(R.string.networkerror));
                listner.onGetResponse(null);
                VolleyLog.d("Error: " + error.getMessage());
                //TastyToast.makeText(context, "Network error.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(postRequest);

    }

    public PostDataParser(final Context context, String url, final Map<String, String> params, final boolean flag, final View view, final OnGetResponseListner listner) {
        if (!Util.isConnected(context)) {
            Util.showSnakBar(context, context.getResources().getString(R.string.internectconnectionerror), view);
            //TastyToast.makeText(context, "No internet connections.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            listner.onGetResponse(null);
            return;
        }
        if (flag) {
            dialog = MyCustomProgressDialog.ctor(context);
            dialog.setCancelable(false);
            dialog.setMessage("Please wait...");
            showpDialog();
        }
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Util util = new Util();
                            JSONObject jobj = util.getjsonobject(response);
                            listner.onGetResponse(jobj);
                        } catch (Exception e) {
                            listner.onGetResponse(null);
                            e.printStackTrace();
                        }
                        if (flag)
                            hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (flag)
                    hidepDialog();
                Util.showSnakBar(context, context.getResources().getString(R.string.networkerror), view);
                listner.onGetResponse(null);
                VolleyLog.d("Error: " + error.getMessage());
                //TastyToast.makeText(context, "Network error.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (AppData.sToken != null) {
                    headers.put("token", AppData.sToken);
                }
                return headers;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(postRequest);
    }

    public interface OnGetResponseListner {
        void onGetResponse(JSONObject response);
    }
}
