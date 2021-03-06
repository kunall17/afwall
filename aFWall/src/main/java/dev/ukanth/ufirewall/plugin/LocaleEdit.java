package dev.ukanth.ufirewall.plugin;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import dev.ukanth.ufirewall.R;
import dev.ukanth.ufirewall.util.G;

public class LocaleEdit extends AppCompatActivity {
	//public static final String LOCALE_BRIGHTNESS = "dev.ukanth.ufirewall.plugin.LocaleEdit.ACTIVE_PROFLE";
	
	private boolean mIsCancelled = false;
	
	private int CUSTOM_PROFILE_ID = 100;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);

		BundleScrubber.scrub(getIntent());
		BundleScrubber.scrub(getIntent().getBundleExtra(
				com.twofortyfouram.locale.Intent.EXTRA_BUNDLE));

		setContentView(R.layout.tasker_profile);

		Toolbar toolbar = (Toolbar) findViewById(R.id.tasker_toolbar);

		setSupportActionBar(toolbar);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		//final int currentPosition = prefs.getInt("storedPosition", 0);
		
		RadioButton tasker_enable = (RadioButton) findViewById(R.id.tasker_enable);
		RadioButton tasker_disable = (RadioButton) findViewById(R.id.tasker_disable);
		RadioButton button1 = (RadioButton) findViewById(R.id.defaultProfile);
		RadioButton button2 = (RadioButton) findViewById(R.id.profile1);
		RadioButton button3 = (RadioButton) findViewById(R.id.profile2);
		RadioButton button4 = (RadioButton) findViewById(R.id.profile3);
		
		RadioGroup profiles = (RadioGroup)findViewById(R.id.radioProfiles);

		List<String> profilesList = G.getAdditionalProfiles();
		//int textColor = Color.parseColor("#000000");
		
		int counter = CUSTOM_PROFILE_ID;
		for(String profile : profilesList) {
			RadioButton rdbtn = new RadioButton(this);
			rdbtn.setId(counter++);
			rdbtn.setText(profile);
			profiles.addView(rdbtn);
		}
		
		
		String name = prefs.getString("default", getString(R.string.defaultProfile));
		button1.setText(name != null && name.length() == 0 ? getString(R.string.defaultProfile) : name);
		name = prefs.getString("profile1", getString(R.string.profile1));
		button2.setText(name != null && name.length() == 0 ? getString(R.string.profile1) : name);
		name = prefs.getString("profile2", getString(R.string.profile2));
		button3.setText(name != null && name.length() == 0 ? getString(R.string.profile2) : name);
		name = prefs.getString("profile3", getString(R.string.profile3));
		button4.setText(name != null && name.length() == 0 ? getString(R.string.profile3) : name);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setupTitleApi11();
		} else {
		}

		if (null == paramBundle) {
			final Bundle forwardedBundle = getIntent().getBundleExtra(
					com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
			if (PluginBundleManager.isBundleValid(forwardedBundle)) {
				String index = forwardedBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE);
				if(index.contains("::")) {
					index = index.split("::")[0];
				}
				String enable = getString(R.string.enable);
				if(index != null ){
					//int id = Integer.parseInt(index);
					switch(index){
						case "0" : tasker_enable.setChecked(true);break;
						case "1": tasker_disable.setChecked(true);break;
						case "2": button1.setChecked(true); break;
						case "3": button2.setChecked(true); break;
						case "4": button3.setChecked(true); break;
						case "5": button4.setChecked(true); break;
						default:
							int diff = CUSTOM_PROFILE_ID + (Integer.parseInt(index) - 6);
							RadioButton btn = (RadioButton) findViewById(diff);
							if(btn !=null) {
								btn.setChecked(true);
							}
							//logic here
					}

					/*if(id > 5) {
						int diff = CUSTOM_PROFILE_ID + (id - 6);
						RadioButton btn = (RadioButton) findViewById(diff);
						if(btn !=null) {
							btn.setChecked(true);	
						}
					}*/
				}
			}
		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupTitleApi11() {
		CharSequence callingApplicationLabel = null;
		try {
			callingApplicationLabel = getPackageManager().getApplicationLabel(
					getPackageManager().getApplicationInfo(getCallingPackage(),
							0));
		} catch (final NameNotFoundException e) {
		}
		if (null != callingApplicationLabel) {
			setTitle(callingApplicationLabel);
		}
	}

	protected void onPause() {
		super.onPause();
	}
	
	@Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.twofortyfouram_locale_menu_dontsave:
				mIsCancelled = true;
				finish();
				return true;
			case R.id.twofortyfouram_locale_menu_save:
				finish();
				return true;
		}
        return super.onOptionsItemSelected(item);
    }
	

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.twofortyfouram_locale_help_save_dontsave, menu);
        return true;
    }

	@Override
	public void finish() {
		if (mIsCancelled)
        {
            setResult(RESULT_CANCELED);
        } else {
        	RadioGroup group = (RadioGroup) findViewById(R.id.radioProfiles);
    		int selectedId  = group.getCheckedRadioButtonId();
    		RadioButton radioButton = (RadioButton) findViewById(selectedId);
			String action = radioButton.getText().toString();
    		int idx = group.indexOfChild(radioButton);
    		final Intent resultIntent = new Intent();
            resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, PluginBundleManager.generateBundle(getApplicationContext(), idx + "::" + action));
            resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, action);
            setResult(RESULT_OK, resultIntent);
	
        }
	  super.finish();
	}
}
