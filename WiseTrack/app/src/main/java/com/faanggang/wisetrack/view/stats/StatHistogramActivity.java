package com.faanggang.wisetrack.view.stats;


import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.faanggang.wisetrack.R;
import com.google.common.graph.Graph;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Displays an experiment's activity through a histogram.
 * https://github.com/jjoe64/GraphView/wiki/Documentation
 */
public class StatHistogramActivity extends AppCompatActivity {
    private BarGraphSeries<DataPoint> series = new BarGraphSeries<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_histogram_screen);

        GraphView histogram = (GraphView) findViewById(R.id.stats_histogram);

        int x,y;
        x = 0;

        int bars = 7 ;
        for (int j = 0 ; j < bars; j++ ) {
            x = j;
            y = j;
            series.appendData(new DataPoint(x,y),true,100   );
        }
        histogram.addSeries(series);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            public int get(DataPoint data) {
                series.setDrawValuesOnTop(true);
                series.setValuesOnTopColor(Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100));
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(50);

    // draw values on top
    //series.setValuesOnTopSize(50);

    }
}
