package mukhtar.exapple.com.play_stop_record;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class Fragment1 extends Fragment implements View.OnClickListener{
    View v;
    Button ok;
    EditText quantity;
    FragmentTransaction fTrans;
    Fragment fragment2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment1, null);
        ok=(Button)v.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        quantity=(EditText)v.findViewById(R.id.editText);
        fragment2=new Fragment2();
        return v;
    }

    @Override
    public void onClick(View v) {
        if(!quantity.getText().toString().equals("")) {
            MainActivity.n = Integer.parseInt(quantity.getText().toString());
            fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.cont, fragment2);
            fTrans.commit();
            InputMethodManager imm = (InputMethodManager) MainActivity.ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
