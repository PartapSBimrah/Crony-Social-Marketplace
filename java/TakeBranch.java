package com.abhigam.www.foodspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class TakeBranch extends AppCompatActivity {


    private ImageView arch,auto,bio,chem,civil,mech,electrical,electro,electro_tele,info_tech,comp_sci;

    private String URL_BRANCH_ENTRY = "http://13.233.234.79/branch_entry.php";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String arch_str="Architecture",
                   auto_str="Automobile",
                   bio_str="Bio-Technology",
                   chem_str="Chemical",
                   civil_str="Civil",
                   mech_str="Mechanical",
                   electrical_str="Electrical",
                   electro_str="Electronics",
                   electro_tele_str="Electronics and Telecommunications",
                   info_tech_str="Information Technology",
                   comp_sci_str="Computer Science";

    private static final String PREF_BRANCH = "pref_branch";

    private View parentLayout;

    private static final String ENROLL_TAG = "enroll_no";
    private String enrollment_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_branch);
        parentLayout = findViewById(android.R.id.content);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        arch = (ImageView)findViewById(R.id.architecture);
        auto = (ImageView)findViewById(R.id.auto_mob);
        bio = (ImageView)findViewById(R.id.bio_tech);
        civil = (ImageView)findViewById(R.id.civil_eng);
        mech = (ImageView)findViewById(R.id.mechanical);
        chem = (ImageView)findViewById(R.id.chemical_eng);
        electrical = (ImageView)findViewById(R.id.electrical);
        electro = (ImageView)findViewById(R.id.electronics);
        electro_tele = (ImageView)findViewById(R.id.electronics_tele);
        info_tech = (ImageView)findViewById(R.id.info_tech);
        comp_sci = (ImageView)findViewById(R.id.computer_sci);

        enrollment_number = getIntent().getStringExtra(ENROLL_TAG);


        arch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                branchEntry(arch_str);

            }
        });
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branchEntry(auto_str);

            }
        });
        bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branchEntry(bio_str);
            }
        });
        civil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branchEntry(civil_str);

            }
        });
        mech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branchEntry(mech_str);

            }
        });
        electrical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branchEntry(electrical_str);

            }
        });
        chem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                branchEntry(chem_str);
            }
        });
        electro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branchEntry(electro_str);

            }
        });
        electro_tele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                branchEntry(electro_tele_str);
            }
        });

        info_tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branchEntry(info_tech_str);

            }
        });
        comp_sci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branchEntry(comp_sci_str);

            }
        });

    }


    private  void branchEntry(final String branch){

        mStringRequest = new StringRequest(Request.Method.POST, URL_BRANCH_ENTRY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putString(PREF_BRANCH,branch);
                editor.apply();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(parentLayout,"Unable to connect",Snackbar.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("enrollment",enrollment_number);
                hashMap.put("branch",branch);
                return hashMap;
            }
        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }


}
