package com.synchroteam.synchroteam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Filter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.TypeIntervention;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.ClientJobTypeAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;

import java.util.ArrayList;

public class ClientJobType extends Activity {
    ArrayList<TypeIntervention> clientJobTypeArrayList;
    Dao dao;
    RecyclerView recyclerView;
    ClientJobTypeAdapter clientJobTypeAdapter;
    EditText searchEditText;
    TextView clearData, cancelJobType;
    Filter filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.synchroteam.synchroteam3.R.layout.activity_client_job_type);

        //initializing view
        recyclerView = findViewById(R.id.recycle_client_job_type);
        searchEditText = findViewById(R.id.searchEditText);
        clearData = findViewById(R.id.clearSearchData);
        cancelJobType = findViewById(R.id.cancel_text_view);

        Log.e("Check","Client job is created");
        dao = DaoManager.getInstance();
        clientJobTypeArrayList = dao.getTypeIntervention();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
         clientJobTypeAdapter = new ClientJobTypeAdapter(this,clientJobTypeArrayList);
        recyclerView.setAdapter(clientJobTypeAdapter);
        searchEditText.addTextChangedListener(textWatcher);
        filter = clientJobTypeAdapter.getFilter();

        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText("");
            }
        });

        cancelJobType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void passOnValues(TypeIntervention value) {
        TypeIntervention typeIntervention = value;
        Intent intent = new Intent();
        intent.putExtra("TypeIntervention", (Parcelable) typeIntervention);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if(filter!= null){
                filter.filter(charSequence);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
