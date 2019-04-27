package edu.ewu20.nasa;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public final String API = "Zy4Xq7fcdknG12rph9X0QF8ofutah5IkqlaB0n8F";
    public final String URL_BASE = "https://api.nasa.gov/planetary/apod";
    private RequestQueue requestQueue;
    private ImageView imageView;
    private TextView tv;
    private Button butt;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = findViewById(R.id.button2);
        butt = b;
        et = findViewById(R.id.editText);
        clearText();
        doAPI();
        tv = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        b.setOnClickListener(view -> doAPI());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doImageStuff(String address) {
        try {
            ImageRequest request = new ImageRequest(
                    address,
                    response -> {
                        putImageOnScreen(response);
                    },
                    imageView.getWidth(),
                    imageView.getHeight(),
                    ImageView.ScaleType.FIT_CENTER,
                    Bitmap.Config.RGB_565,
                    error -> {
                        tv.setText("error in getting");
                    });
            requestQueue.add(request);
        } catch (Exception e) {
            tv.setText("other error");
        }
    }

    private String getImageLoc(String json) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject e = parser.parse(json).getAsJsonObject();
            String s = e.get("url").getAsString();
            return s;
        } catch (Exception e) {
            return "";
        }
    }
    private void putImageOnScreen(final Bitmap b) {
        imageView.setImageBitmap(b);
    }
    private void doCap(String json) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject e = parser.parse(json).getAsJsonObject();
            String c = e.get("explanation").getAsString();
            tv.setText(c);
            String t = e.get("title").getAsString();
            butt.setText(t);
        } catch (Exception e) {}
    }
    private void clearText() {
        et.setText("YYYY-MM-DD");
    }
    private void doAPI() {
        try {
            String apiLoc = URL_BASE + "?api_key=" + API;
            if (valiDATE(et.getText().toString())) {
                apiLoc += "&date=" + et.getText().toString();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    apiLoc,
                    null,
                    response -> {
                        tv.setText(response.toString());
                        // CALL IMAGE WITH ADDRESS FROM JSON
                        doImageStuff(getImageLoc(response.toString()));
                        doCap(response.toString());
                        Log.d("TAG", response.toString());
                    }, error -> {
                        clearText();
                        doAPI();
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            tv.setText(Arrays.deepToString(e.getStackTrace()));
        }
    }
    private boolean valiDATE(String s) {
        try {
            String[] splot = s.split("-");
            int y = Integer.parseInt(splot[0]);
            int m = Integer.parseInt(splot[1]);
            int d = Integer.parseInt(splot[2]);
            if (y >= 1995
            && y <= 2019
            && m > 0
            && m < 13
            && d > 0
            && d < 32) {
                return true;
            } else {
                clearText();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
