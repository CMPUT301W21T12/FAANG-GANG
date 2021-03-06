package com.faanggang.wisetrack.controllers;

import android.util.Log;

import com.faanggang.wisetrack.model.stats.StatHistogram;
import com.faanggang.wisetrack.model.stats.StatPlot;
import com.faanggang.wisetrack.model.stats.StatReport;
import com.google.firebase.Timestamp;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage and handle all statistic related methods for experiments
 * To be used in all activity files
 * Important things to keep in mind:
 * Trial type is determined by an int via "trialType"
 * 0 - Count
 * 1 - Binomial
 * 2 - Non - Negative
 * 3 - Measurements
 */

// add test for values
public class StatManager {
    public StatReport currentTrialReport = new StatReport();
    private StatHistogram currentTrialHistogram = new StatHistogram();
    private StatPlot currentTrialPlot = new StatPlot();

    public StatManager() {

    }
    /**
     * Calculate the following:
     * Median , Mean, Std. Deviation and quartiles
     * @param trialData from trial as a float array
     */
    public void generateStatReport(List<Float> trialData) {
        if (trialData.size() > 1) {
            currentTrialReport.setMean(currentTrialReport.calculateMean(trialData));
            currentTrialReport.setMedian(currentTrialReport.calculateMedian(trialData));
            currentTrialReport.setStdev(currentTrialReport.calculateStdev(trialData));
            currentTrialReport.setQuartiles(currentTrialReport.calculateQuartiles(trialData));
            currentTrialReport.setInterquartileRange(currentTrialReport.calculateInterquartileRange(trialData));
            currentTrialReport.setMaximum(currentTrialReport.calculateMax(trialData));
            currentTrialReport.setMinimum(currentTrialReport.calculateMin(trialData));
            Log.i("Stat Manager",
                    "Max " + String.valueOf(currentTrialReport.getMaximum()) +
                            "Min " + String.valueOf(currentTrialReport.getMinimum()) +
                            "QTR " + String.valueOf(currentTrialReport.getQuartiles()) +
                            "Mean " + String.valueOf(currentTrialReport.getMean()) +
                            "Median " + String.valueOf(currentTrialReport.getMedian()) +
                            "STDEV " + String.valueOf(currentTrialReport.getStdev())
            );
        }


    }

    /**
     * Following methods get the min,max,median,std. dev and quartiles for ease of use for GUI side
     */
    public float getMin() {return currentTrialReport.getMinimum();}
    public float getMax() {return currentTrialReport.getMaximum();}
    public float getMean() { return currentTrialReport.getMean();}
    public float getMedian() { return currentTrialReport.getMedian();}
    public double getStdev() { return currentTrialReport.getStdev();}
    public List<Float> getQuartiles() {return currentTrialReport.getQuartiles();}
    public float getIQR() {return currentTrialReport.getInterquartileRange();}


    /**
     * Generate Data Points for histogram use.
     * @param trialData: Results from trial runs
     * @param trialType: Type of the experiment
     * @return dataPoints list for histogram use.
     */
    public List<DataPoint> generateStatHistogram(List<Float> trialData, int trialType) {
        List<DataPoint> dataPointList = new ArrayList<>();
        String msg = trialData.toString() + "::" + String.valueOf(trialType) + "::" + trialData.size();
        switch (trialType){
            case 0: // count
                Log.i("results log COUNT", msg);
                dataPointList = currentTrialHistogram.drawHistogramCount(trialData);
                return dataPointList;
            case 1: // binomial
                Log.i("results log BINOM", msg);
                dataPointList = currentTrialHistogram.drawHistogramBinomial(trialData);
                return dataPointList;
            case 2: // NNIC
                Log.i("results log NNIC", trialData.toString() + "::" + String.valueOf(trialType) + "::" + trialData.size());
                dataPointList =  currentTrialHistogram.drawHistogramNNIC(trialData);
                return dataPointList;
            case 3: // Measurement
                Log.i("results log MST", msg);
                dataPointList =  currentTrialHistogram.drawHistogramMeasurement(trialData);
                return dataPointList;
            default:
                Log.w("STSManager", "Histogram: Error Trial Type");
                return null;
        }
    }

    /**
     * Generate Data Points for plot use.
     * @param trialData: Results from trial runs
     * @param timeStamps: Time at which the result was taken
     * @param trialType: Type of the experiment.
     * @return  dataPoints list for plot use.
     */
    public List<DataPoint> generateStatPlot(List<Float> trialData, List<Timestamp> timeStamps, int trialType) {// plots overtime * change return
        switch (trialType){
            case 0: // count
                return currentTrialPlot.drawPlotCount(trialData, timeStamps);
            case 1: // binomial
                return currentTrialPlot.drawPlotBinomial(trialData, timeStamps);
            case 2: // NNIC
                return currentTrialPlot.drawPlotNNIC(trialData, timeStamps);
            case 3: // Measurement
                return currentTrialPlot.drawPlotMeasurement(trialData, timeStamps);
            default:
                Log.w("STSManager", "Plot: Error Trial Type");
                return null;


        }
    }

}



