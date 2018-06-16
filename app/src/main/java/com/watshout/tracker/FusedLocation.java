package com.watshout.tracker;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.alternativevision.gpx.beans.Track;
import org.alternativevision.gpx.beans.TrackPoint;
import org.alternativevision.gpx.beans.Waypoint;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class FusedLocation {

    private Context context;
    private MapPlotter mapPlotter;
    private String uid;
    private ArrayList<Waypoint> trackPoints;
    private static GPXCreator gpxCreator;
    private UploadFinishedActivity uploadFinishedActivity;
    private WriteXML writeXML;


    FusedLocation(Context context, MapPlotter mapPlotter, String uid) throws TransformerException, ParserConfigurationException {

        this.context = context;
        this.mapPlotter = mapPlotter;
        this.uid = uid;
        this.trackPoints = new ArrayList<>();
        gpxCreator = new GPXCreator(context, uid);
        this.uploadFinishedActivity = new UploadFinishedActivity(uid);

        writeXML = new WriteXML(context, uid);

    }

    public LocationCallback buildLocationCallback() {

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                MainActivity.GPSconnected = true;

                Location location = locationResult.getLocations().get(0);

                double lat = location.getLatitude();
                double lon = location.getLongitude();
                double speed = location.getSpeed();
                double bearing = location.getBearing();
                double altitude = location.getAltitude();
                long time = location.getTime();

                float accuracy = location.getAccuracy();

                Log.wtf("GPSACCURACY", accuracy + "");

                // Adds the point to the map
                mapPlotter.addMarker(lat, lon);

                Log.wtf("GPS", "Lat: " + lat + "\nLong" + lon + "\nTracking: " + MainActivity.currentlyTrackingLocation);

                if (MainActivity.currentlyTrackingLocation){
                    new LocationObject(context, uid, lat, lon, speed, bearing, altitude, time).uploadToFirebase();

                    TrackPoint temp = new TrackPoint();
                    trackPoints.add(temp);

                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                    df.setTimeZone(tz);
                    String nowAsISO = df.format(new Date());

                    writeXML.addPoint(lat, lon, altitude, 69, nowAsISO);

                }
                else if (trackPoints.size() > 0) {

                    if (!MainActivity.activityRunning) {

                        uploadFinishedActivity.moveCurrentToPast();
                        String date = uploadFinishedActivity.getFormattedDate();

                        try {
                            writeXML.saveFile(date);
                        } catch (TransformerException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {

            }
        };

        return locationCallback;

    }

    public LocationRequest buildLocationRequest() {

        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(1000)
                .setSmallestDisplacement(1);

        return locationRequest;

    }

    public ArrayList<Waypoint> getTrackPoints() {
        return trackPoints;
    }
}