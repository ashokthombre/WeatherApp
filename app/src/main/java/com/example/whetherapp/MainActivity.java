package com.example.whetherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRl;
    private ProgressBar loadingPB;
    private TextView cityNameTV,tempratureTV,conditionTV;
    private RecyclerView wetherRv;
    private TextInputEditText cityEdt;
    private ImageView backIV,iconTV,searchTV;

    private ArrayList<WhetherRvModel>whetherRvModels;
    private WhetherRvAdapter weWhetherRvAdapter;

    private LocationManager locationManager;
    private int PERMISSION_CODE=1;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        homeRl=findViewById(R.id.idRLHome);
        loadingPB=findViewById(R.id.idPBLoading);
       cityNameTV=findViewById(R.id.idTVCityName);
       tempratureTV=findViewById(R.id.idTVtemprature);
       conditionTV=findViewById(R.id.idTVCondition);
       wetherRv=findViewById(R.id.idRvWether);
       cityEdt=findViewById(R.id.idEdtCity);
       backIV=findViewById(R.id.idIVBlack);
       iconTV=findViewById(R.id.idTVIcon);
       searchTV=findViewById(R.id.idIVSearch);

       whetherRvModels=new ArrayList<>();

       weWhetherRvAdapter=new WhetherRvAdapter(this,whetherRvModels);
        wetherRv.setAdapter(weWhetherRvAdapter);

       locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       if (ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
       {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);

       }
        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        Location location= locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

       Log.d("TAG","langitude"+location.getLongitude());
       Log.d("TAG","latitude"+location.getLatitude());

       getWetherInfo(cityName);

       Log.d("TAG","City Name"+cityName);

       searchTV.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String city=cityEdt.getText().toString();
               if (city.isEmpty())
               {
                   Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
               }
               else
               {
                   cityNameTV.setText(cityName);
                   getWetherInfo(city);
               }
           }
       });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       if (requestCode==PERMISSION_CODE)
       {
           if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
           {
               Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
           }
           else {
               Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
               finish();
           }
       }

    }

    private String getCityName(double longitude, double latitude)
    {
        String cityName="Not Found";
        Geocoder gcd=new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses=gcd.getFromLocation(latitude,longitude,10);

            for (Address adr:addresses)
            {
                if (adr!=null)
                {
                    String city=adr.getLocality();
                    {
                        if (city!=null && !city.equals(""))
                        {
                            cityName =city;
                        }
                        else
                        {
                            Log.d("TAG","CITY NOT FOUND");
                            Toast.makeText(this, "User City not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
 return cityName;
    }

    private void getWetherInfo(String cityName)
    {
        String url="https://api.weatherapi.com/v1/forecast.json?key=2d721faef3534437b9172821231506&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        Log.d("TAG",url);
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", String.valueOf(response));
                loadingPB.setVisibility(View.GONE);
                homeRl.setVisibility(View.VISIBLE);
                whetherRvModels.clear();

                try {
                    String temprature=response.getJSONObject("current").getString("temp_c");

                    tempratureTV.setText(temprature+" c");

                    int isDay=response.getJSONObject("current").getInt("is_day");

                    String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    String conditionCode=response.getJSONObject("current").getJSONObject("condition").getString("code");

                    Picasso.get().load("https:".concat(conditionIcon)).into(iconTV);
                    conditionTV.setText(condition);
                    if (isDay==1)
                    {
//                        Picasso.get().load("").into(backIV);
                        backIV.setImageResource(R.drawable.morning);
                    }
                    else
                    {
//                        Picasso.get().load().into(backIV);
                        backIV.setImageResource(R.drawable.night);
                    }

                    JSONObject forecastObj=response.getJSONObject("forecast");
                    JSONObject forcastO=forecastObj.getJSONArray("forecastday").getJSONObject(0);


                    JSONArray hourArray=forcastO.getJSONArray("hour");

                    for (int i=0;i<hourArray.length();i++)
                    {
                        JSONObject hourObj=hourArray.getJSONObject(i);
                        String time=hourObj.getString("time");
                        String tempr=hourObj.getString("temp_c");
                        String img=hourObj.getJSONObject("condition").getString("icon");
                        String wind=hourObj.getString("wind_kph");

                        whetherRvModels.add(new WhetherRvModel(time,tempr,img,wind));

                    }
                    weWhetherRvAdapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }
}