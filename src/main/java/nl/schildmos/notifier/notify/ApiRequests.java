package nl.schildmos.notifier.notify;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONObject;

public class ApiRequests {

  private Gson gson = new Gson();
  private Context context;
  private ProgressDialog progressDialog;
  private RequestQueue mRequestQueue;
  public static final String DEV_SERVER = "http://10.0.2.2:9000/api/notification";
  SharedPreferences SP;

  public ApiRequests(Context context) {
    mRequestQueue =  Volley.newRequestQueue(context);
    this.context = context;
    SP = PreferenceManager.getDefaultSharedPreferences(context);
    progressDialog = new ProgressDialog(context);
    progressDialog.setCancelable(false);
    progressDialog.setTitle(context.getString(R.string.progress_save_title));
    progressDialog.setMessage(context.getString(R.string.progress_save_message));
  }

  public void saveNotification(Notification notification) {
    progressDialog.show();
    JsonObjectRequest jr = new JsonObjectRequest(Request.Method.PUT,SP.getString("serverUrl",DEV_SERVER),gson.toJson(notification),new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        progressDialog.dismiss();
        Toast toast = Toast.makeText(context, context.getString(R.string.save_success_toast), Toast.LENGTH_SHORT);
        toast.show();
      }
    },new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.save_fail_message) + error).setTitle(context.getString(R.string.save_fail_title));
        AlertDialog dialog = builder.create();
        dialog.show();
      }
    });
    mRequestQueue.add(jr);
  }


}
