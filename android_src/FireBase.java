/**
 * Copyright 2017 FrogSquare. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package org.godotengine.godot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.os.Bundle;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONException;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

//Auth++
import org.godotengine.godot.auth.Auth;
//Auth--

//Storage++
import org.godotengine.godot.storage.Storage;
//Storage--

import org.godotengine.godot.Dictionary;

public class FireBase extends Godot.SingletonBase {

	static public Godot.SingletonBase initialize (Activity p_activity) {
		return new FireBase(p_activity);
	}

	public FireBase(Activity p_activity) {
		registerClass ("FireBase", new String[] {
			"init", "initWithFile", "alert",

			//Analytics++
			"setScreenName", "sendAchievement", "sendCustom", "join_group", "level_up",
			"post_score", "content_select", "earn_currency",
			"spend_currency", "tutorial_begin", "tutorial_complete",
			//Analytics--

			//AdMob++
			"show_banner_ad", "show_interstitial_ad", "show_rewarded_video",
			"request_rewarded_video_status", "set_banner_unitid", "show_rvideo",
			"request_rewarded_video_status_of", "get_banner_size",
			//AdMob--

			//Auth++
			//AuthGoogle++
			"google_sign_in", "google_sign_out",
			"is_google_connected", "get_google_user",  "google_revoke_access",
			//AuthGoogle--

			//AuthFacebook++
			"facebook_sign_out", "facebook_sign_in", "is_facebook_connected",
			"get_facebook_permissions", "facebook_has_permission",
			"revoke_facebook_permission", "facebook_revoke_access",
			"ask_facebook_read_permission", "ask_facebook_publish_permission",
			"get_facebook_user",
			//AuthFacebook--

			//AuthTwitter++
			"twitter_sign_in", "twitter_sign_out", "is_twitter_connected",
			//AuthTwitter--

			"anonymous_sign_in", "anonymous_sign_out", "is_anonymous_connected",
			"authConfig",
			//Auth--

			//Notification++
			"notifyInMins", "notifyInSecs", "subscribeToTopic", "getToken",
			"notifyOnComplete",
			//Notification--

			//Invites++
			"invite",
			//Invites--

			//RemoteConfig++
			"getRemoteValue", "setRemoteDefaults", "setRemoteDefaultsFile",
			//RemoteConfig--

			//Storage++
			"download", "upload",
			//Storage--

			//Firestore++
			"load_document", "set_document", "add_document"
			//Firestore--
		});

		activity = p_activity;
	}

	private void initFireBase(final String data) {
		Utils.d("Data From File: " + data);

		JSONObject config = null;
		mFirebaseApp = FirebaseApp.initializeApp(activity);

		if (data.length() <= 0) {
			Utils.d("FireBase initialized.");
			return;
		}

		try { 
			config = new JSONObject(data);
			firebaseConfig = config;
		} catch (JSONException e) { Utils.d("JSON Parse error: " + e.toString()); }

		//Analytics++
		if (config.optBoolean("Analytics", true)) {
			Utils.d("Initializing Firebase Analytics.");
			Analytics.getInstance(activity).init(mFirebaseApp);
		}
		//Analytics--

		//AdMob++
		if (config.optBoolean("AdMob", false)) {
			Utils.d("Initializing Firebase AdMob.");
			AdMob.getInstance(activity).init(mFirebaseApp);
		}
		//AdMob--

		//Auth++
		if (config.optBoolean("Authentication", false)) {
			Utils.d("Initializing Firebase Authentication.");
			Auth.getInstance(activity).init(mFirebaseApp);
			Auth.getInstance(activity).configure(config.optString("AuthConf"));
		}
		//Auth--

		//Notification++
		if (config.optBoolean("Notification", false)) {
			Utils.d("Initializing Firebase Notification.");
			Notification.getInstance(activity).init(mFirebaseApp);
		}
		//Notification--

		//Invites++
		if (config.optBoolean("Invites", false)) {
			Utils.d("Initializing Firebase Invites.");
			Invites.getInstance(activity).init(mFirebaseApp);
		}
		//Invites--

		//RemoteConfig++
		if (config.optBoolean("RemoteConfig", false)) {
			Utils.d("Initializing Firebase RemoteConfig.");
			RemoteConfig.getInstance(activity).init(mFirebaseApp);
		}
		//RemoteConfig--

		//Storage++
		if (config.optBoolean("Storage", false)) {
			if (!config.optBoolean("Authentication", false)) {
				Utils.d("Firebase Storage needs Authentication.");
			}

			Utils.d("Initializing Firebase Storage.");
			Storage.getInstance(activity).init(mFirebaseApp);
		}
		//Storage--

		//Firestore++
		if (config.optBoolean("Firestore", false)) {
			Utils.d("Initializing Firestore.");
			Firestore.getInstance(activity).init(mFirebaseApp);
		}
		//Firestore--

		Utils.d("FireBase initialized.");
	}

	public void alertMsg(String message) {
		alertMsg("FireBase", message);
	}

	public void alertMsg(String title, String message) {
		AlertDialog.Builder bld;

		bld = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT);
		bld.setIcon (com.godot.game.R.drawable.icon);
		bld.setTitle(title);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		bld.create().show();
	}

	public void init(final String data, final int script_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Utils.setScriptInstance(script_id);
				initFireBase(data);
			}
		});
	}

	public void initWithFile(final String fileName, final int script_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				String data = Utils.readFromFile(fileName, activity);
				data = data.replaceAll("\\s+", "");

				Utils.setScriptInstance(script_id);
				initFireBase(data);
			}
		});
	}

	//Analytics++
	public void setScreenName (final String screen_name) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (screen_name.length() <= 0) {
					Utils.d("Screen name is empty defaults to main");
					Analytics.getInstance(activity).set_screen_name("Main Screen");
				} else {
					Analytics.getInstance(activity).set_screen_name(screen_name);
				}
			}
		});
	}

	public void sendAchievement(final String a_id) {
		if (a_id.length() <= 0) {
			Utils.d("Achievement id not provided");
			return;
		}

		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity).send_achievement(a_id);
			}
		});
	}

	public void join_group(final String id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity).send_group(id);
			}
		});
	}

	public void level_up(final String character, final int level) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity).send_level_up(character, level);
			}
		});
	}

	public void post_score(final String character, final int level, final int score) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity).send_score(character, level, score);
			}
		});
	}

	public void content_select(final String content, final String item_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity).send_content(content, item_id);
			}
		});
	}

	public void earn_currency(final String currency_name, final int value) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity).earn_currency(currency_name, value);
			}
		});
	}

	public void spend_currency(final String item_name, final String currency, final int value) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity)
				.spend_currency(item_name, currency, value);
			}
		});
	}

	public void tutorial_begin() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity).send_tutorial_begin();
			}
		});
	}

	public void tutorial_complete() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity).send_tutorial_complete();
			}
		});
	}

	public void send_events(final String key, final Dictionary data) {
		if (key.length() <= 0 || data.size() <= 0) {
			Utils.d("Key or Data is null.");
			return;
		}

		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity).send_events(key, data);
			}
		});
	}

	public void send_custom(final String key, final String value) {
		if (key.length() <= 0 || value.length() <= 0) {
			Utils.d("Key or Value is null.");
			return;
		}

		activity.runOnUiThread(new Runnable() {
			public void run() {
				Analytics.getInstance(activity).send_custom(key, value);
			}
		});
	}
	//Analytics--

	//AdMob++
	public void set_banner_unitid(final String unit_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				AdMob.getInstance(activity).setBannerUnitId(unit_id);
			}
		});
	}

	public void show_banner_ad(final boolean show) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				AdMob.getInstance(activity).show_banner_ad(show);
			}
		});
	}

	public Dictionary get_banner_size() {
		return AdMob.getInstance(activity).getBannerSize();
	}

	public void show_interstitial_ad() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				AdMob.getInstance(activity).show_interstitial_ad();
			}
		});
	}

	public void show_rvideo(final String unit_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				AdMob.getInstance(activity).show_rewarded_video(unit_id);
			}
		});
	}

	public void request_rewarded_video_status_of(final String unit_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				AdMob.getInstance(activity).requestRewardedVideoStatus(unit_id);
			}
		});
	}

	public void request_rewarded_video_status() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				AdMob.getInstance(activity).requestRewardedVideoStatus();
			}
		});
	}

	public void show_rewarded_video() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				AdMob.getInstance(activity).show_rewarded_video();
			}
		});
	}
	//AdMob--

	//Auth++
	public void authConfig(final String conf) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).configure(conf);
			}
		});
	}

	//AuthGoogle++
	public void google_sign_in () {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).sign_in(Auth.GOOGLE_AUTH);
			}
		});
	}

	public void google_sign_out() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).sign_out(Auth.GOOGLE_AUTH);
			}
		});
	}

	public void revoke_google_access() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).revoke(Auth.GOOGLE_AUTH);
			}
		});
	}

	public boolean is_google_connected() {
		return Auth.getInstance(activity).isConnected(Auth.GOOGLE_AUTH);
	}

	public String get_google_user() {
		return Auth.getInstance(activity).getUserDetails(Auth.GOOGLE_AUTH);
	}
	//AuthGoogle--

	//AuthTwitter++
	public void twitter_sign_in () {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).sign_in(Auth.TWITTER_AUTH);
			}
		});
	}

	public void twitter_sign_out() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).sign_out(Auth.TWITTER_AUTH);
			}
		});
	}
	//AuthTwitter--

	//AuthFacebook++
	public void facebook_sign_in() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).sign_in(Auth.FACEBOOK_AUTH);
			}
		});
	}

	public void facebook_sign_out() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).sign_out(Auth.FACEBOOK_AUTH);
			}
		});
	}

	public void revoke_facebook_access() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).revoke(Auth.FACEBOOK_AUTH);
			}
		});
	}

	public boolean is_facebook_connected() {
		return Auth.getInstance(activity).isConnected(Auth.FACEBOOK_AUTH);
	}

	public String get_facebook_user() {
		return Auth.getInstance(activity).getUserDetails(Auth.FACEBOOK_AUTH);
	}

	public String get_facebook_permissions() {
		return Auth.getInstance(activity).getFacebookPermissions();
	}

	public boolean facebook_has_permission(final String permission) {
		return Auth.getInstance(activity).isPermissionGiven (permission);
	}

	public void revoke_facebook_permission(final String permission) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).revokeFacebookPermission(permission);
			}
		});
	}

	public void ask_facebook_read_permission(
	final String title, final String message, final String permission) {

		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity)
				.askForPermission(title, message, permission, true);
			}
		});
	}

	public void ask_facebook_publish_permission(
	final String title, final String message, final String permission) {

		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity)
				.askForPermission(title, message, permission, false);
			}
		});
	}
	//AuthFacebook--

	public void anonymous_sign_in() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).sign_in(Auth.ANONYMOUS_AUTH);
			}
		});
	}

	public void anonymous_sign_out() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Auth.getInstance(activity).sign_out(Auth.ANONYMOUS_AUTH);
			}
		});
	}

	public boolean is_anonymous_connected() {
		return Auth.getInstance(activity).isConnected(Auth.ANONYMOUS_AUTH);
	}
	//Auth--

	/** Extra **/
	public void alert(final String message) {
		if (message.length() <= 0) {
			Utils.d("Message is empty.");
			return;
		}

		activity.runOnUiThread(new Runnable() {
			public void run() {
				alertMsg(message);
			}
		});
	}
	/** Extra **/

	//Notification++
	public void notifyOnComplete(final Dictionary data) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Notification.getInstance(activity).notifyOnComplete(data);
			}
		});
	}

	public void notifyInSecs (final String message, final int secs) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Notification.getInstance(activity).notifyInSecs(message, secs);
			}
		});
	}

	public void notifyInMins (final String message, final int mins) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Notification.getInstance(activity).notifyInMins(message, mins);
			}
		});
	}

	public void subscribeToTopic (final String topic) {
		if (topic.length() <= 0) {
			Utils.d("Topic id not provided.");
			return;
		}

		activity.runOnUiThread(new Runnable() {
			public void run() {
				Notification.getInstance(activity).subscribe(topic);
			}
		});
	}

	public String getToken () {
		return Notification.getInstance(activity).getFirebaseMessagingToken();
	}

	public void sendTokenToServer () {
		activity.runOnUiThread(new Runnable() {
			public void run() {

			}
		});
	}
	//Notification--

	//Invites++
	public void invite (final String message, final String deepLink) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (deepLink.length() <= 0) {
					Utils.d("DeepLink not provided fall back to simple share");
					Invites.getInstance(activity).invite(message);
				} else {
					Utils.d("Using Firebase DeepLink");
					Invites.getInstance(activity).invite(message, deepLink);
				}
			}
		});
	}
	//Invites--

	//RemoteConfig++
	public String getRemoteValue (final String key) {
		if (key.length() <= 0) {
			Utils.d("getting remote config: key not provided, returning null");
			return "NULL";
		}

		return RemoteConfig.getInstance(activity).getValue(key);
	}

	public void setRemoteDefaultsFile (final String path) {
		if (path.length() <= 0) {
			Utils.d("File not provided for remote config");
			return;
		}

		activity.runOnUiThread(new Runnable() {
			public void run() {
				RemoteConfig.getInstance(activity).setDefaultsFile(path);
			}
		});
	}

	public void setRemoteDefaults (final String jsonData) {
		if (jsonData.length() <= 0) {
			Utils.d("No defaults were provided.");
			return;
		}

		activity.runOnUiThread(new Runnable() {
			public void run() {
				RemoteConfig.getInstance(activity).setDefaults(jsonData);
			}
		});
	}
	//RemoteConfig--

	//Storage++
	public void download(final String file, final String path) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Storage.getInstance(activity).download(file, path);
			}
		});
	}

	public void upload(final String file, final String path) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Storage.getInstance(activity).upload(file, path);
			}
		});
	}
	//Storage--

	//Firestore++
	public void set_document(final String p_col_name, final String p_doc_name, final Dictionary p_dict) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Firestore.getInstance(activity).setData(p_col_name, p_doc_name, p_dict);
			}
		});
	}

	public void add_document(final String p_name, final Dictionary p_data) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Firestore.getInstance(activity).addDocument(p_name, p_data);
			}
		});
	}

	public void load_document(final String p_name) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Firestore.getInstance(activity).loadDocuments(p_name);
			}
		});
	}
	//Firestore--

	/** Main Funcs **/
	public static JSONObject getConfig() {
		return firebaseConfig;
	}

	protected void onMainActivityResult (int requestCode, int resultCode, Intent data) {
		//Analytics++
		// Analytics.getInstance(activity).onActivityResult(requestCode, resultCode, data);
		//Analytics--

		//Invites++
		Invites.getInstance(activity).onActivityResult(requestCode, resultCode, data);
		//Invites--

		//Auth++
		Auth.getInstance(activity).onActivityResult(requestCode, resultCode, data);
		//Auth--
	}

	protected void onMainPause () {
		//Analytics++
		// Analytics.getInstance(activity).onPause();
		//Analytics--

		//RemoteConfig++
		// RemoteConfig.getInstance(activity).onPause();
		//RemoteConfig--

		//Auth++
		Auth.getInstance(activity).onPause();
		//Auth--

	
		//AdMob++
		AdMob.getInstance(activity).onPause();
		//AdMob--
	}

	protected void onMainResume () {
		//Analytics++
		// Analytics.getInstance(activity).onResume();
		//Analytics--

		//RemoteConfig++
		// RemoteConfig.getInstance(activity).onResume();
		//RemoteConfig++

		//Auth++
		Auth.getInstance(activity).onResume();
		//Auth--


		//AdMob++
		AdMob.getInstance(activity).onResume();
		//AdMob--
	}

	protected void onMainDestroy () {
		//Analytics++
		// Analytics.getInstance(activity).onStop();
		//Analytics--

		//RemoteConfig++
		// RemoteConfig.getInstance(activity).onStop();
		//RemoteConfig--

		//Auth++
		Auth.getInstance(activity).onStop();
		//Auth--

		//AdMob++
		AdMob.getInstance(activity).onStop();
		//AdMob--

		//Storage++
		Storage.getInstance(activity).onStop();
		//Storage--
	}

	private static Context context = null;
	private static Activity activity = null;
	protected static String currentScreen = "None";

	private static JSONObject firebaseConfig = new JSONObject();

	private FirebaseApp mFirebaseApp = null;
}
