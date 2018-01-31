package fr.frodriguez.bluetoothmusic.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import fr.frodriguez.bluetoothmusic.R;
import fr.frodriguez.bluetoothmusic.defines.Preferences;
import fr.frodriguez.library.Triple;
import fr.frodriguez.library.compat.AppCompatPreferenceActivity;
import fr.frodriguez.library.utils.AppUtils;


/**
 * By FloZone on 16/11/2017.
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends AppCompatPreferenceActivity {

    // Licences list
    private final static List<Triple> licensesList = new ArrayList<>();


    // Listener when a preference is modified
    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Set the Port value as its summary
            if (key.equals(Preferences.KEY_BATTERY_LEVEL)) {
                findPreference(Preferences.KEY_BATTERY_LEVEL).setSummary(sharedPreferences.getString(key, Integer.toString(Preferences.KEY_BATTERY_LEVEL_DEFAULT)));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Register the listener
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        //TODO here alert dialog for reset button

        // Set the battery level value as summary
        findPreference(Preferences.KEY_BATTERY_LEVEL).setSummary(sharedPreferences.getString(Preferences.KEY_BATTERY_LEVEL, Integer.toString(Preferences.KEY_BATTERY_LEVEL_DEFAULT)));

        // Display tutorial popup
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_tuto, null);
        WebView webView = (WebView) dialogview.findViewById(R.id.tv_tuto);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setScrollbarFadingEnabled(false);
        webView.loadUrl("file:///android_asset/about.html");
        final AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setView(dialogview);
        findPreference(Preferences.KEY_TUTO).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                alertDialog.show();
                return true;
            }
        });

        // Set the version name as summary
        findPreference(Preferences.KEY_VERSION).setSummary(AppUtils.getAppVersion(this));

        // Read licences.json file
        InputStream inputStream = getResources().openRawResource(R.raw.licenses);
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(inputStream, writer, Charset.defaultCharset());
            JSONArray licensesJson = new JSONArray(writer.toString());
            writer.close();
            for(int i = 0; i < licensesJson.length(); ++i) {
                licensesList.add(new Triple<>(
                        licensesJson.getJSONObject(i).getString(Preferences.LICENSES_NAME),
                        licensesJson.getJSONObject(i).getString(Preferences.LICENSES_TITLE),
                        licensesJson.getJSONObject(i).getString(Preferences.LICENSES_URL)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Display licenses
        findPreference(Preferences.KEY_LICENSE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Adapter for the listview
                ArrayAdapter<Triple> adapter = new ArrayAdapter<Triple>(SettingsActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, licensesList) {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                        text1.setText((String) licensesList.get(position).first);
                        text2.setText((String) licensesList.get(position).second);
                        return view;
                    }
                };

                LayoutInflater inflater = getLayoutInflater();
                View dialogview = inflater.inflate(R.layout.dialog_license, null);
                ListView listview = (ListView) dialogview.findViewById(R.id.listview);
                listview.setAdapter(adapter);
                // Handle click on license = open browser
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String url = (String) licensesList.get(position).third;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
                alertDialog.setView(dialogview);
                alertDialog.show();

                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Back button
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener again
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(listener);
    }


}
