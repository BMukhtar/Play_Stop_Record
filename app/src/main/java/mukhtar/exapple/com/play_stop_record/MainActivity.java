package mukhtar.exapple.com.play_stop_record;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public static int n;
    Fragment1 fragment1;
    FragmentTransaction ftrans;
    public static Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment1 = new Fragment1();
        ftrans = getFragmentManager().beginTransaction();
        ftrans.add(R.id.cont, fragment1);
        ftrans.commit();
        ctx = this;
    }
}
