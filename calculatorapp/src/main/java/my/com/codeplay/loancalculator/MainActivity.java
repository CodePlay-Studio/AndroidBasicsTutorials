package my.com.codeplay.loancalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
	private long delay = 5000; 
	private TimerTask task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
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
       schedule(task, delay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    public void schedule(TimerTask t, long delay) {}
    
}
