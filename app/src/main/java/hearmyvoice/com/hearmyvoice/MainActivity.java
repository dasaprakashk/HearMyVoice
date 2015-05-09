package hearmyvoice.com.hearmyvoice;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.util.IOUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends ActionBarActivity {

    private String URL;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSend = (Button)findViewById(R.id.SendButton);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCallDataToDB();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    void SaveCallDataToDB() {
        final EditText txtPhone = (EditText)findViewById(R.id.phoneNumber);

        String toPhoneNumber = txtPhone.getText().toString();

        InputStream stream = getResources().openRawResource(
                getResources().getIdentifier("raw/cowbell", "cowbell", getPackageName()));
        byte[] convertedStream = new byte[0];
        try {
            convertedStream = IOUtils.toByteArray(stream);
        } catch (IOException e) {
            Log.e("Error while converting to byte array", e.getMessage());
            Toast.makeText(getApplicationContext(), "Error whle uploading your message", Toast.LENGTH_SHORT).show();
            return;
        }
        final ParseFile file = new ParseFile(toPhoneNumber.replace("+91", "") +".mp3", convertedStream);
        file.saveInBackground();
        final ParseObject objCallData = new ParseObject(getString(R.string.ParseObject));
        objCallData.put("Number", txtPhone.getText().toString());
        objCallData.put("Message", file);
        objCallData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    ParseFile file = objCallData.getParseFile("Message");
                    MainActivity.this.URL = file.getUrl();
                    MainActivity.this.phoneNumber = txtPhone.getText().toString();
                    InvokeCallingService();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InvokeCallingService() {
        Thread t = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    String serverString = "";
                    HttpClient client = new DefaultHttpClient();
                    String baseUrl = "http://appusys.com/hmv/callandplay.php?";
                    baseUrl = baseUrl + "RecordingUrl=" + MainActivity.this.URL + "&number=" + MainActivity.this.phoneNumber;
                    Log.i("Url", baseUrl);
                    HttpGet httpGet = new HttpGet(baseUrl);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    serverString = client.execute(httpGet, responseHandler);
                    Log.i("InvokeCallingService", serverString);
                }
                catch (Exception e)
                {
                    Log.e("InvokeCallingService", e.getMessage());
                }
            }
        });
        t.start();
    }


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
}
