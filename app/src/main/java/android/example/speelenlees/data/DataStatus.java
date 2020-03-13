package android.example.speelenlees.data;

import android.example.speelenlees.domain.Client;

import java.util.List;

public interface DataStatus {

    //wordt gebruikt om data in te laden uit Firebase
   void DataIsLoaded(List<Client> clients, List<String> keys);
}
