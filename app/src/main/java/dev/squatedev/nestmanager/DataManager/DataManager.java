package dev.squatedev.nestmanager.DataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dev.squatedev.nestmanager.Immutable.Immutable;
import dev.squatedev.nestmanager.R;

public class DataManager {
    private static DataManager instance;
    private List<Immutable> immutableList;

    private DataManager(){
        immutableList = new ArrayList<>();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public List<Immutable> getImmutableList(){
        return Collections.unmodifiableList(immutableList);
    }


    public boolean addList(Immutable immutable){
        if(immutable == null) return false;

        for(Immutable p : immutableList){
            if(p.getApk_name().equals(immutable.getApk_name()) &&
                    p.getPackage_name().equals(immutable.getPackage_name())){
                return false;
            }
        }
        immutableList.add(immutable);
        return true;
    }

    public Immutable getImmutableAt(int position) {
        if (position >= 0 && position < immutableList.size()) {
            Immutable original = immutableList.get(position);
            return new Immutable(
                    original.getApk_name(),
                    original.getPackage_name(),
                    original.getVersion_apk(),
                    original.getSign_apk(),
                    original.getApk_icon()
            );
        }
        return null;
    }

    public int getSize() {
        return immutableList.size();
    }

    public void removeAt(int position) {
        if (position >= 0 && position < immutableList.size()) {
            immutableList.remove(position);
        }
    }

    public void clearAll() {
        immutableList.clear();
    }
}