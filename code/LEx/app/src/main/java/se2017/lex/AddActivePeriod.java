// written by: John Eng and Kevin Lee
// tested by: John Eng and Kevin Lee
// debugged by: Kevin Lee

// Class to Add Active Periods to the schedule

package se2017.lex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;



public class AddActivePeriod extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_active_period);

        TextView startTime = (TextView) findViewById(R.id.suggestStart);
        TextView endTime = (TextView) findViewById(R.id.suggestEnd);

        //Gets times in each schedule finds the start and end times

        startTime.setText(HomeTab.startAPSuggestion + ":00");
        endTime.setText(HomeTab.endAPSuggestion + ":00");

    }

    public void acceptEvent(View v){
        //Suggests event to user. If user accepts, will send it to the schedule
        eTime APSuggest = new eTime(0,0,HomeTab.startAPSuggestion,0,0,HomeTab.endAPSuggestion,"Active Period");
        HomeTab.sorted.add(APSuggest);

        Intent toHome = new Intent(this, HomeTab.class);
        startActivity(toHome);
    }

    public void gotoHome(View v) {
        Intent toHome = new Intent(this, HomeTab.class);
        startActivity(toHome);
    }

}
