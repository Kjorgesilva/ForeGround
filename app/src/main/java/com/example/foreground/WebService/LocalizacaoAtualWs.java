package com.example.foreground.WebService;


import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

public class LocalizacaoAtualWs {


    public static void enviarLocalizacao(final Context contexto, String url, Map<String, Double> params) {
        RequestQueue queue = Volley.newRequestQueue(contexto);

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Connection.getUrl() +
                url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("receiver", "Localização enviada");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("receiver", "erro na requisição ao enviar localização: " + error.toString());

            }
        });
        {
            postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(postRequest);
        }


    }



}




