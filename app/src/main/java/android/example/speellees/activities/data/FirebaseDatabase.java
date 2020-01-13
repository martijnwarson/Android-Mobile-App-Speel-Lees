package android.example.speellees.activities.data;

import android.example.speellees.activities.domain.Level;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabase {
    private DatabaseReference databaseReference;
    private List<Level> levels = new ArrayList<>();

    public FirebaseDatabase(){
        databaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Levels");
    }

    public FirebaseDatabase(String levelId) {
        databaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Levels").child(levelId);
    }

    public void addMember(Level level) {
        String id = databaseReference.push().getKey(); // push() creëert een unieke string in 'Levels' in Firebase
        level.setLevelId(id);

        databaseReference.child(id).setValue(level);
    }

    public void updateMember(Level level) {
        databaseReference.child(level.getLevelId()).setValue(level);
    }

    public void readLevels(final DataStatus dataStatus) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            // onDataChange wordt altijd aangeroepen als er iets verandert in de database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                levels.clear();
                List<String> keys = new ArrayList<>();

                // DataSnapshot bevat key en value van een specifieke node (dataSnapshot.getChildren bevat de key en value van members)
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Level level = keyNode.getValue(Level.class);
                    levels.add(level);
                }

                dataStatus.DataIsLoaded(levels, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
        public void deleteLevel() {
            databaseReference.removeValue();
        }
}


