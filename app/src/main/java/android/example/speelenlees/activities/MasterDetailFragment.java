package android.example.speelenlees.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.example.speelenlees.R;
import android.example.speelenlees.activities.master.ClientListActivity;

//Pagina wat gebruikt wordt voor redirect
public class MasterDetailFragment extends Fragment {
    public MasterDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        startActivity(new Intent(this.getContext(), ClientListActivity.class));
        return inflater.inflate(R.layout.fragment_master_detail, container, false);
    }
}
