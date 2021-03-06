// written by: Kyung Lee
// tested by: Kyung Lee and Trirmadura Ariyawansa
// debugged by: Kyung Lee and Trirmadura Ariyawansa

// processes the data for the HealthIndex

package se2017.lex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class h_graph extends AppCompatActivity {
    public XYPlot plot;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_graph);
        plot = (XYPlot) findViewById(R.id.plot);
        // domainLabels = X values (Time in days)
        final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14};

        // series1Numbers = Y values (Health Index)
        Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};

        // Array of health index
        XYSeries healthIndex = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Health Index");

        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);

        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        // Generate graph
        plot.addSeries(healthIndex, series1Format);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }});
    }
    public void viewHGraph(View v)
    {
        Intent toHGraph = new Intent(this, h_graph.class);
        startActivity(toHGraph);
    }

    public void viewWGraph(View v)
    {
        Intent toGraph = new Intent(this, w_graph.class);
        startActivity(toGraph);
    }

    public void viewRGraph(View v)
    {
        Intent toGraph = new Intent(this, r_graph.class);
        startActivity(toGraph);
    }

    public void viewGGraph(View v)
    {
        Intent toGraph = new Intent(this, g_graph.class);
        startActivity(toGraph);
    }

}
