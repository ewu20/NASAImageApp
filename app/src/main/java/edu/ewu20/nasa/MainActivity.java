package edu.ewu20.nasa;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity {
    private String API_KEY;
    private String URL_BASE;
    private String ERROR_TEXT;
    private String DEFAULT_DATE;
    private RequestQueue requestQueue;
    private ImageView imageView;
    private TextView textView;
    private Button mainButton;
    private EditText editText;
    private JsonParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        // initialize fields
        mainButton = findViewById(R.id.button2);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        ERROR_TEXT = getString(R.string.error_text);
        DEFAULT_DATE = getString(R.string.default_date);
        API_KEY = getString(R.string.api_key);
        URL_BASE = getString(R.string.url_base);
        parser = new JsonParser();
        // set text box values and find first image
        clearTextField();
        doAPI();
        mainButton.setOnClickListener(view -> doAPI());
    }

    /*
     *  Begin Android UI initialization functions
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*
     * End Android UI initialization functions
     */

    /*
     * Begin helper functions
     */
    private void doImageRequest(String address) {
        try {
            ImageRequest request = new ImageRequest(
                    address,
                    response -> imageView.setImageBitmap(response),
                    imageView.getWidth(),
                    imageView.getHeight(),
                    ImageView.ScaleType.FIT_CENTER,
                    Bitmap.Config.RGB_565,
                    error -> putErrorInTextView("ImageRequest inside"));
            requestQueue.add(request);
        } catch (Exception e) {
            putErrorInTextView("ImageRequest outside");
        }
    }

    private String getImageAddress(String json) {
        try {
            return parser.parse(json)
                    .getAsJsonObject()
                    .get("url")
                    .getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    private void setCaptionAndTitle(String json) {
        try {
            String caption = parser.parse(json)
                    .getAsJsonObject()
                    .get("explanation")
                    .getAsString();
            textView.setText(caption);
            String title = parser.parse(json)
                    .getAsJsonObject()
                    .get("title")
                    .getAsString();
            mainButton.setText(title);
        } catch (Exception e) {
            putErrorInTextView("Setting title and caption");
        }
    }

    private void clearTextField() {
        editText.setText(DEFAULT_DATE);
    }

    private void doAPI() {
        try {
            String apiURL = URL_BASE + "?api_key=" + API_KEY;
            String textInBox = getEditableText();
            if (valiDATE(textInBox)) {
                // if valid date in box, add date parameter
                apiURL += "&date=" + textInBox;
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    apiURL,
                    null,
                    response -> {
                        doImageRequest(getImageAddress(response.toString()));
                        setCaptionAndTitle(response.toString());
                    }, error -> {
                        clearTextField();
                        doAPI();
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            putErrorInTextView("API Call" + e.toString());
        }
    }

    private boolean valiDATE(String date) {
        try {
            String[] pieces = date.split("-");
            int year = Integer.parseInt(pieces[0]);
            int month = Integer.parseInt(pieces[1]);
            int day = Integer.parseInt(pieces[2]);
            if (year > 1994 && year <= 2019) {
                if (month > 0 && month < 13 && day > 0) {
                    switch (month) {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 8:
                        case 10:
                        case 12:
                            return day <= 31;
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            return day <= 30;
                        case 2:
                            return day <= 29;
                        default:
                            break;
                    }
                }
            }
            clearTextField();
            return false;
        } catch (Exception e) {
            clearTextField();
            return false;
        }
    }

    private void putErrorInTextView(String arg) {
        String toSet = ERROR_TEXT + "\n" + arg;
        textView.setText(toSet);
        clearTextField();
    }

    private String getEditableText() {
        return editText.getText().toString();
    }
}
