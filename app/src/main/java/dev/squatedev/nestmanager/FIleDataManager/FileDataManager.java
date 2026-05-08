package dev.squatedev.nestmanager.FIleDataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dev.squatedev.nestmanager.FileImuttable.FileImmutable;
import dev.squatedev.nestmanager.R;

public class FileDataManager {
        private static FileDataManager instance;
        private List<FileImmutable> immutableList;

        private FileDataManager(){
            immutableList = new ArrayList<>();
        }

        public static synchronized FileDataManager getInstance() {
            if (instance == null) {
                instance = new FileDataManager();
            }
            return instance;
        }

        public List<FileImmutable> getImmutableList(){
            return Collections.unmodifiableList(immutableList);
        }


        public boolean addList(FileImmutable immutable){
            if(immutable == null) return false;

            for(FileImmutable p : immutableList){
                if(p.File_get_name().equals(immutable.File_get_name()) &&
                        p.File_getdata_name().equals(immutable.File_getdata_name())){
                    return false;
                }
            }
            immutableList.add(immutable);
            return true;
        }

        public FileImmutable getImmutableAt(int position) {
            if (position >= 0 && position < immutableList.size()) {
                FileImmutable original = immutableList.get(position);
                return new FileImmutable(
                        original.File_get_name(),
                        original.File_getdata_name(),
                        original.File_getSign_file(),
                        original.File_getType()
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
