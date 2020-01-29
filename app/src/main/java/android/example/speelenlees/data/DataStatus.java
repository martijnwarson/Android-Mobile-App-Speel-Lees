package android.example.speelenlees.data;

import android.example.speelenlees.domain.Client;

import java.util.List;

public interface DataStatus {
    void DataIsLoaded(List<Client> clients, List<String> keys);
}
