package nl.schildmos.notifier.notify;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import im.delight.android.baselib.Identity;


public class MainActivity extends AppCompatActivity implements OnClickListener, LocationListener {

  private EditText etDescription;
  private Button bSend;
  private double latitude;
  private double longitude;
  private LocationManager locationManager;
  private Criteria criteria;
  private String bestProvider;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main__notify);
    getLocation();
    etDescription = (EditText) findViewById(R.id.etDescription);
    bSend = (Button) findViewById(R.id.bSend);
    bSend.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.bSend:
        String description = etDescription.getText().toString();
        if (isLocationEnabled()){
          Notification notification = new Notification(getAGoogleEmailAdress(),latitude,longitude, Identity.getDeviceId(this),description);
          persistData(notification);
        } else {
          showEnableLocationMessage();
        }
        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main__notify, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id){
      case R.id.action_settings:
        Intent i = new Intent(this, PreferencesActivity.class);
        startActivity(i);
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onPause() {
    super.onPause();
    locationManager.removeUpdates(this);
  }

  @Override
  public void onLocationChanged(Location location) {
    locationManager.removeUpdates(this);
    latitude = location.getLatitude();
    longitude = location.getLongitude();
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }

  private String getAGoogleEmailAdress() {
    AccountManager manager = AccountManager.get(this);
    Account[] accounts = manager.getAccountsByType("com.google");

    if (accounts.length > 0) {
      return accounts[0].name;
    }else{
      return null;
    }

  }

  private void persistData(Notification notification){
    ApiRequests apiRequests = new ApiRequests(this);
    apiRequests.saveNotification(notification);
  }

  private boolean isLocationEnabled() {
    int locationMode = 0;
    String locationProviders;
    // this does not work for versions below KitKat
    try {
      locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
    } catch (Settings.SettingNotFoundException e) {
      e.printStackTrace();
    }
    return locationMode != Settings.Secure.LOCATION_MODE_OFF;
  }

  private void getLocation() {
    if (isLocationEnabled()) {
      locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
      criteria = new Criteria();
      bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

      Location location = locationManager.getLastKnownLocation(bestProvider);
      if (location != null) {
        Log.wtf("AAI",location.getLatitude()+"");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
      }
      else{
        locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
        Log.wtf("huh",bestProvider);
      }
  }
    else
    {
      showEnableLocationMessage();
    }
  }

  private void showEnableLocationMessage(){
    Toast.makeText(MainActivity.this, getString(R.string.enable_location), Toast.LENGTH_SHORT).show();
  }



}
