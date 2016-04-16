package informations;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.maps.R;

/**
 * Created by Narmi on 05/04/2016.
 */

public class MainListeMonument extends Activity {

    TextView nameView;
    TextView ageView;
    TextView jobView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti);
        StrictMode.enableDefaults(); //STRICT MODE ENABLED

        nameView = (TextView) findViewById(R.id.nametxt);
        ageView = (TextView) findViewById(R.id.agetxt);
        jobView = (TextView) findViewById(R.id.jobtxt);

        getData();
    }

    public void getData() {
        String result = "";
        InputStream isr = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(""); //YOUR PHP SCRIPT ADDRESS
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
            nameView.setText("Couldnt connect to database");
        }
        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();

            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        //parse json data
        try {
            String n = "";
            String a = "";
            String j = "";
            JSONArray jArray = new JSONArray(result);

            for (int i = 0; i < jArray.length(); i++) {
                json = jArray.getJSONObject(i);
                n = n + "Name : " + json.getString("FirstName") + " " + json.getString("LastName") + "\n";
                a = a + "Age : " + json.getInt("Age") + "\n";
                j = j + "Job : " + json.getString("Job") + "\n";
            }

            nameView.setText(n);
            ageView.setText(a);
            jobView.setText(j);

        } catch (Exception e) {

            Log.e("log_tag", "Error Parsing Data " + e.toString());
        }

    }
}