package android.example.speelenlees.activities.ui.homepages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.example.speelenlees.R;

public class HomeFragment extends Fragment{

    private static final String TAG = "HomeFragment";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "HomeFragment loaded successfully");
        return inflater.inflate(R.layout.fragment_homescreen, container, false);
    }
}