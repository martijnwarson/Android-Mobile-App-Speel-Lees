package android.example.speelenlees.activities.ui.homepages;

import android.content.Intent;
import android.example.speelenlees.activities.master.ClientListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.example.speelenlees.R;
import android.widget.Button;

// HOME FRAGMENT
// wordt ingeladen in HomeActivity
public class HomeFragment extends Fragment{
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //fragment_homescreen is homepage met logo
       return inflater.inflate(R.layout.fragment_homescreen, container, false);

    }


}