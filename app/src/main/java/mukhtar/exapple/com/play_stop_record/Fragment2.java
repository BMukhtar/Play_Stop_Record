package mukhtar.exapple.com.play_stop_record;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static mukhtar.exapple.com.play_stop_record.MainActivity.ctx;

public class Fragment2 extends Fragment{
    ListView lv;
    ArrayList<String> data = new ArrayList();
    String mFilePath = Environment.getExternalStorageDirectory()+"/recordsForPlayer";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2,null);

        prepareDirectories(mFilePath+"/records");
        prepareDirectories(mFilePath+"/tmp");

        lv = (ListView) v.findViewById(R.id.list_view);
        for(int i = 1; i <= MainActivity.n; i++){
            data.add(i+". Запишите аудио");
        }
        MyAdapter myAdapter = new MyAdapter(MainActivity.ctx,data,R.layout.item);
        lv.setAdapter(myAdapter);
        return v;
    }
    public boolean cleanDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = cleanDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }else{
            return dir.delete();
        }
        return true;
    }

    public void prepareDirectories(String path){
        File dir = new File(path);
        if(!dir.exists()) {
            dir.mkdirs();
        }else{
            if(!cleanDir(dir)) Toast.makeText(ctx,"Failed cleaning directory", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        cleanDir(new File(mFilePath+"/records"));
        cleanDir(new File(mFilePath+"/tmp"));
        super.onDestroy();
    }
}
