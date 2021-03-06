package com.faanggang.wisetrack.controllers;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.faanggang.wisetrack.model.WiseTrackApplication;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * QRCodeManager is primarily concerned with the handing of String to QR Codes and Codes back to Strings.
 */
public class QRCodeManager {
    private FirebaseFirestore db;

    private codeScanListener scanListener;
    private barcodeRegisterListener barcodeListener;

    public interface codeScanListener {
        void onScanValid(String expID, int trialResult);
        void onScanInvalid();
    }

    public interface barcodeRegisterListener {
        void onBarcodeAvailable(String code);
        void onBarcodeUnavailable();
    }


    public QRCodeManager(){
        db = FirebaseFirestore.getInstance();
    }
    public QRCodeManager(codeScanListener listener){
        db = FirebaseFirestore.getInstance();
        this.scanListener = listener;
    }
    public QRCodeManager(barcodeRegisterListener listener){
        db = FirebaseFirestore.getInstance();
        this.barcodeListener = listener;
    }

    /**
     * Takes in a string, and dimensions and returns a bitmap that represents the string.
     * Source: Adapter from Alexander Farber, https://stackoverflow.com/a/30529128
     * @param str
     * String to be encoded.
     * @param qrWidth
     * Width of the final bitmap.
     * @param qrHeight
     * Height of the final bitmap.
     * @return
     * Returns a bitmap representing the string input.
     */
    public Bitmap stringToBitmap(String str, int qrWidth, int qrHeight) {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, qrWidth, qrHeight, null);
        } catch (IllegalArgumentException | WriterException iae) {
            Log.e("QR", iae.toString());
            result = null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, qrWidth, 0, 0, w, h);
        return bitmap;
    }

    /**
     * Generates a new qr code for an experiment if it does not exist already. If it does, then retrives that code.
     * It will then set teh ImageView to display the QR code.
     * @param expID
     * ID of the Experiment
     * @param trialResult
     * Result that the QR indicates
     * @param image
     * ImageView that will be changed to the QR code.
     */
    public void requestCodesForExperiment(String expID, int trialResult, ImageView image){
        db.collection("QRCodes").whereEqualTo("expID", expID)
            .whereEqualTo("trialResult", trialResult)
            .get().addOnCompleteListener(task ->{
            if (task.isSuccessful()) {
                List<DocumentSnapshot> doc = task.getResult().getDocuments();
                String code;
                if (doc.isEmpty()){
                    code = addQRCode(expID, trialResult);
                } else {
                    code = doc.get(0).getId();
                }
                Bitmap bitmap = stringToBitmap(code,200,200);
                image.setImageBitmap(bitmap);
            } else {
                Log.d("QR", "Failed with: ", task.getException());
            }
        });
    }

    /**
     * Finds out if a scanned QRcode exists and points to a valid experiment. If it does, it will call the scanListener interface function.
     * @param code
     * String that is read from the QR code.
     */
    public void readCode(String code) {
        db.collection("QRCodes").document(code).get()
            .addOnCompleteListener( task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        Boolean open = doc.getBoolean("isPublic");
                        String onlineID = doc.getString("uID");
                        String currentID = WiseTrackApplication.getCurrentUser().getUserID();
                        if (open || (onlineID.equals(currentID))){
                            String expID = doc.getString("expID");
                            int trialResult = doc.getLong("trialResult").intValue();
                            scanListener.onScanValid(expID, trialResult);
                        } else{
                            scanListener.onScanInvalid();
                        }

                    } else {
                        scanListener.onScanInvalid();
                    }
                } else {
                    Log.d("QR", "Failed with: ", task.getException());
                }
            });
    }

    /**
     * Finds out if a scanned bar code exists and points to a valid experiment. If it does, it will call the barcodeListener interface function.
     * @param code
     * String read from barcode
     */
    public void checkBarcode(String code){
        db.collection("QRodes").document(code).get()
            .addOnCompleteListener( task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        barcodeListener.onBarcodeUnavailable();
                    } else{
                        barcodeListener.onBarcodeAvailable(code);
                    }
                } else {
                    Log.d("QR", "Failed with: ", task.getException());
                }
            });
    }

    /**
     * Registers a barcode to be associated with an experiment trial result.
     * @param expID
     * ID of the experiment.
     * @param trialResult
     * Result of the Trial the bar code will be associated with.
     * @param id
     * String of the barcode to save.
     * @param userID
     * ID of the user that the barcode will belong to.
     */
    public void addBarcode(String expID, int trialResult, String id, String userID){
        Map<String, Object> codeMap = new HashMap<>();
        codeMap.put("expID", expID);
        codeMap.put("trialResult", trialResult);
        codeMap.put("isPublic", false);
        codeMap.put("uID", userID);
        DocumentReference newCode = db.collection("QRCodes").document(id);
        newCode.set(codeMap);
    }

    /**
     * Registers a QR code to be associated with an experiment trial result. Unlike barcode, this QR code is public.
     * @param expID
     * ID of the experiment.
     * @param trialResult
     * Result of the trial the bar code will be associated with.
     * @return
     */
    private String addQRCode(String expID, int trialResult){
        Map<String, Object> codeMap = new HashMap<>();
        codeMap.put("expID", expID);
        codeMap.put("trialResult", trialResult);
        codeMap.put("isPublic", true);
        codeMap.put("uID", "null");
        DocumentReference newCode = db.collection("QRCodes").document();
        newCode.set(codeMap);
        return newCode.getId();
    }


}
