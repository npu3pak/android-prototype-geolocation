package ru.npu3pak.citythroughmyeyes.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.doctoror.geocoder.Geocoder.LimitExceededException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingService extends IntentService {
    public static final String INTENT_KEY_LOCATION = "Location";
    public static final String INTENT_KEY_RESULT_RECEIVER = "ResultReceiver";

    public static final int RESULT_CODE_SUCCESS = 0;
    public static final int RESULT_CODE_ERROR = 1;

    public static final String RESULT_KEY_ADDRESS = "Address";
    public static final String RESULT_KEY_EXCEPTION = "Exception";

    public GeocodingService() {
        super("City Through My Eyes: Geocoding Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Location location = intent.getParcelableExtra(INTENT_KEY_LOCATION);
        ResultReceiver resultReceiver = intent.getParcelableExtra(INTENT_KEY_RESULT_RECEIVER);

        try {
            Address address = getAddress(location);
            sendAddress(resultReceiver, address);
        } catch (IOException | LimitExceededException addressException) {
            sendException(resultReceiver, addressException);
        }
    }

    private Address getAddress(Location location) throws IOException, LimitExceededException {
        if (Geocoder.isPresent()) {
            return getAddressWithBuiltInGeocoder(location);
        } else {
            return getAddressWithThirdPartyGeocoder(location);
        }
    }

    @Nullable
    private Address getAddressWithBuiltInGeocoder(Location location) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        if (addresses != null && addresses.size() != 0) {
            return addresses.get(0);
        } else {
            return null;
        }
    }

    @Nullable
    private Address getAddressWithThirdPartyGeocoder(Location location) throws IOException, LimitExceededException {
        com.doctoror.geocoder.Geocoder geocoder = new com.doctoror.geocoder.Geocoder(this, Locale.getDefault());
        List<com.doctoror.geocoder.Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1, true);
        if (addresses.size() != 0) {
            com.doctoror.geocoder.Address a = addresses.get(0);
            Address address = new Address(Locale.getDefault());
            address.setAdminArea(a.getAdministrativeAreaLevel1());
            address.setSubAdminArea(a.getAdministrativeAreaLevel2());
            address.setLocality(a.getLocality());
            address.setSubLocality(a.getSubLocality());
            address.setThoroughfare(a.getStreetNumber());
            address.setPremises(a.getPremise());
            address.setPostalCode(a.getPostalCode());
            address.setCountryName(a.getCountry());
            address.setLatitude(a.getLocation().latitude);
            address.setLongitude(a.getLocation().longitude);
            return address;
        } else {
            return null;
        }
    }

    private void sendAddress(ResultReceiver resultReceiver, Address address) {
        Bundle data = new Bundle();
        data.putParcelable(RESULT_KEY_ADDRESS, address);
        resultReceiver.send(RESULT_CODE_SUCCESS, data);
    }

    private void sendException(ResultReceiver resultReceiver, Exception addressException) {
        Bundle data = new Bundle();
        data.putSerializable(RESULT_KEY_EXCEPTION, addressException);
        resultReceiver.send(RESULT_CODE_ERROR, data);
    }
}
