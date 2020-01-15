package android.example.speellees.activities.data;

import android.example.speellees.activities.domain.Client;
import android.example.speellees.activities.domain.Level;

import java.util.List;

public interface DataStatus {
    void DataIsLoaded(List<Client> members, List<String> keys);
}
