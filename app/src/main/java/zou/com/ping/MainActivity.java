package zou.com.ping;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView networkText = (TextView) findViewById(R.id.networkType);
        showNetwork(getApplicationContext(), networkText);

        Button btn = (Button) findViewById(R.id.button);
        final EditText text = (EditText) findViewById(R.id.target);
        final EditText resultText = (EditText) findViewById(R.id.editText);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    resultText.setText("");
                    String result = doPing(4, text.getText().toString());
//                    String result = ping(text.getText().toString());
                    resultText.setText(result);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
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

    public String doPing(int pingNum, String target) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        String pingText = "/system/bin/ping -c " + pingNum + " " + target;
        Log.d(TAG, pingText);
        Process p = Runtime.getRuntime().exec(pingText);
        int status = p.waitFor();

        if (status == 0) {
            sb.append("主机连接正常\n");
        } else {
            sb.append("主机连接失败\n");
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String str;
        //读出所有信息并显示
        while ((str = buf.readLine()) != null) {
            sb.append(str);
        }
        buf.close();

        return sb.toString();
    }

    public String ping(String url) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "/system/bin/ping -c 4 " + url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);
            reader.close();

            // body.append(output.toString()+"\n");
            str = output.toString();
            // Log.d(TAG, str);
        } catch (IOException e) {
            // body.append("Error\n");
            e.printStackTrace();
        }
        return str;
    }

    private void showNetwork(Context context, TextView networkText) {
        NetState state = new NetState();
        if (!state.hasNetWorkConnection(context)) {
            networkText.setText("没有网络连接");
        } else if (state.hasWifiConnection(context)) {
            networkText.setText("Wifi网络");
        } else {
            networkText.setText("移动数据网络");
        }
    }
}
