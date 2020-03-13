package my.com.codeplay.loancalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
	private long delay = 5000;
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			startActivity(new Intent(MainActivity.this, CalculatorActivity.class));
			finish();
		}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
       Timer timer = new Timer();
       timer.schedule(task, delay);
    }
}
