package ru.npu3pak.citythroughmyeyes.storage.db;

import android.app.Application;
import android.location.Address;
import android.support.annotation.NonNull;
import android.test.ApplicationTestCase;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.npu3pak.citythroughmyeyes.business_objects.MarkerColor;
import ru.npu3pak.citythroughmyeyes.business_objects.MarkerInfo;
import ru.npu3pak.citythroughmyeyes.business_objects.MarkerType;

public class MarkersDaoTest extends ApplicationTestCase<Application> {
    private MarkersDao dao;

    public MarkersDaoTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dao = new MarkersDao(getContext());
    }

    public void testAddMarker() throws Exception {
        MarkerInfo marker = getMarkerInfo();
        dao.addMarker(marker);
        assertTrue(marker.id > 0);
    }

    @NonNull
    private MarkerInfo getMarkerInfo() {
        MarkerInfo marker = new MarkerInfo();
        marker.type = MarkerType.HOME;
        marker.color = MarkerColor.RED.getIntValue();
        marker.comment = "Дом";
        marker.timestamp = new Date(0);
        marker.address = new Address(Locale.getDefault());
        marker.address.setLatitude(50);
        marker.address.setLongitude(30);
        return marker;
    }

    public void testDeleteMarker() throws Exception {
        MarkerInfo marker = getMarkerInfo();
        dao.addMarker(marker);
        assertTrue(marker.id > 0);
        dao.deleteMarker(marker);
    }

    public void testUpdateMarker() throws Exception {
        MarkerInfo marker = getMarkerInfo();
        dao.addMarker(marker);
        assertTrue(marker.id > 0);
        marker.comment = "Новый дом";
        dao.updateMarker(marker);
    }

    public void testGetAllMarkers() throws Exception {
        MarkerInfo marker = getMarkerInfo();
        dao.addMarker(marker);
        assertTrue(marker.id > 0);
        List<MarkerInfo> markers = dao.getAllMarkers();
        assertTrue(markers.size() > 0);
    }

    @Override
    public void tearDown() throws Exception {
        List<MarkerInfo> markers = dao.getAllMarkers();
        while(markers.size() > 0){
            MarkerInfo m = markers.get(0);
            dao.deleteMarker(m);
            markers.remove(m);
        }
        super.tearDown();
    }
}