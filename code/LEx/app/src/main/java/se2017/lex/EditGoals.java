// written by: Kevin Lee
// tested by: Kevin Lee
// debugged by: Kevin Lee

// edits an existing GoalObject in the Goals Tab



package se2017.lex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EditGoals extends AppCompatActivity {
    private DatabaseReference mDatabase;
    public String userid = "jariy";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goals);

        //On Opening the Edit Goals Page, set the name and progress in the provided TextView;
        TextView goalName = (TextView) findViewById(R.id.goalName);
        goalName.setText( GoalsTab.Goals[GoalsTab.targetGoal].n );

        //convert integer to string
        TextView goalProgress = (TextView) findViewById(R.id.goalProgress);
        String p = String.valueOf(GoalsTab.Goals[GoalsTab.targetGoal].c);
        goalProgress.setText(p);
        //Opening Database
        mDatabase = FirebaseDatabase.getInstance().getReference(userid+"/Goals");




    }

    /** On clicking the cancel button, return to the Goals Tab without saving any entries */
    public void cancelEdit(View v){
        Intent newGoal = new Intent (this, GoalsTab.class);
        startActivity(newGoal);
    }


    /** On click, read entries from the user, save them to variables */
    public void confirmEdit(View v) {
        //Get the inputs
        EditText newProg = (EditText) findViewById(R.id.newProgress);

        //Process data
        int np = Integer.valueOf(newProg.getText().toString());

        //Overwrite old progress with new
        GoalsTab.Goals[GoalsTab.targetGoal].c = np;
        //Overwrite old goal in database
        mDatabase.child(GoalsTab.Goals[GoalsTab.targetGoal].k).setValue(GoalsTab.Goals[GoalsTab.targetGoal]);



        //Return to the Goals Tab after adding the goal
        Intent toGoals = new Intent(this, GoalsTab.class);
        startActivity(toGoals);

    }

}
