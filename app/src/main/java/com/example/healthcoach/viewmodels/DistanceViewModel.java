package com.example.healthcoach.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.recordingapi.Distance;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import android.content.Context;

public class DistanceViewModel extends ViewModel {
    private MutableLiveData<Float> distance = new MutableLiveData<>();

    public MutableLiveData<Float> getDistance() {
        return distance;
    }

    public void setDistance(float distanceCovered) {
        distance.postValue(distanceCovered);
    }


    /**
     * Reads the distance data for a specified time range from Google Fit.
     * Updates the LiveData object distance upon successful retrieval.
     *
     * @param startTime The start time of the range in milliseconds.
     * @param endTime The end time of the range in milliseconds.
     * @param context The application's context.
     */

    public void readDistanceForRange(long startTime, long endTime, Context context) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
        if (googleSignInAccount != null) {
            Distance distanceInstance = new Distance(context, googleSignInAccount);
            distanceInstance.readDistanceData(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    float totalDistance = 0;
                    if (dataReadResponse.getBuckets().size() > 0) {
                        for (Bucket bucket : dataReadResponse.getBuckets()) {
                            DataSet dataSet = bucket.getDataSet(DataType.AGGREGATE_DISTANCE_DELTA);
                            for (DataPoint point : dataSet.getDataPoints()) {
                                for (Field field : point.getDataType().getFields()) {
                                    float distance = point.getValue(field).asFloat();
                                    totalDistance += distance;
                                }
                            }
                        }
                    }
                    setDistance(totalDistance);
                }
            });
        }
    }
}
