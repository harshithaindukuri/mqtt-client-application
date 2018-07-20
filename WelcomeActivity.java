package system.smartbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class WelcomeActivity extends AppCompatActivity {
    public static final String SKIP_WELCOME = "skipWelcome";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button nextPage = (Button) findViewById(R.id.nextPage);
        final CheckBox skipAbout = (CheckBox) findViewById(R.id.skipAbout);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this);
                if (skipAbout.isChecked()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(SKIP_WELCOME, true);
                    editor.apply();
                }
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            }
        });
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SKIP_WELCOME, false)) {
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}
