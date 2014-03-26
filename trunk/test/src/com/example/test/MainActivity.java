package com.example.test;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private static int sectionNo;
	static String sQuery;

	static SQLiteDatabase mDatabase;
	static String db_path = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/db.sqlite";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section_main);
			sectionNo = 1;
			break;
		case 2:
			mTitle = getString(R.string.title_section1);
			sectionNo = 2;
			break;
		case 3:
			mTitle = getString(R.string.title_section2);
			sectionNo = 3;
			break;
		case 4:
			mTitle = getString(R.string.title_section3);
			sectionNo = 4;
			break;
		case 5:
			mTitle = getString(R.string.title_section4);
			sectionNo = 5;
			break;
		case 6:
			mTitle = getString(R.string.title_section5);
			sectionNo = 6;
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			final View mainView = inflater.inflate(R.layout.fragment_main,
					container, false);
			View oneView = inflater.inflate(R.layout.fragment_1, container,
					false);
			View twoView = inflater.inflate(R.layout.fragment_2, container,
					false);
			View threeView = inflater.inflate(R.layout.fragment_3, container,
					false);
			View fourView = inflater.inflate(R.layout.fragment_4, container,
					false);
			View fiveView = inflater.inflate(R.layout.fragment_5, container,
					false);

			switch (sectionNo) {
			case 1:
				final EditText mainText = (EditText) mainView
						.findViewById(R.id.section_mainText);
				Button mainBtn = (Button) mainView
						.findViewById(R.id.section_mainBtn);
				ListView mainList = (ListView) mainView
						.findViewById(R.id.section_mainList);

				final ArrayList<String> mainArray = new ArrayList<String>();
				final ArrayAdapter<String> mainAdapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						mainArray);

				mainList.setAdapter(mainAdapter);
				mainList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				mDatabase = SQLiteDatabase.openDatabase(db_path, null,
						SQLiteDatabase.NO_LOCALIZED_COLLATORS);

				mainBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						mainArray.removeAll(mainArray);
						mainAdapter.notifyDataSetChanged();

						String textCheck = mainText.getText().toString();
						textCheck = textCheck.trim();
						String[] textArray = textCheck.split(",");
						Log.e("Text", textArray + "");

						if (textCheck.getBytes().length <= 0) {
							Toast.makeText(getActivity(), "공백을 입력하였습니다.",
									Toast.LENGTH_SHORT).show();
						} else {
							InputMethodManager imm = (InputMethodManager) getActivity()
									.getSystemService(
											Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									mainText.getWindowToken(), 0);

							String[] sUniv = { "univ_name", "univ_major",
									"univ_percent" };

							if (textArray.length == 1) {
								for (int i = 0; i < 3; i++) {
									sQuery = "select * from univ where "
											+ sUniv[i] + " like " + "'%"
											+ textCheck + "%'";
								}
							}

							Cursor cursor = mDatabase.rawQuery(sQuery, null);
							Log.e("Query", sQuery);
							if (cursor != null && cursor.moveToFirst()) {
								do {
									String str = cursor.getString(0) + ","
											+ cursor.getString(1) + ","
											+ cursor.getString(6);
									mainArray.add(str);
								} while (cursor.moveToNext());
							}
							mainAdapter.notifyDataSetChanged();
						}
						mainText.setText(null);
					}

				});

				mainList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						InputMethodManager imm = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mainText.getWindowToken(),
								0);
						String str = (String) mainAdapter.getItem(position);
						Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT)
								.show();
					}
				});

				return mainView;

			case 2:
				TextView oneText = (TextView) oneView
						.findViewById(R.id.section_1label);
				oneText.setText("자유게시판");
				return oneView;

			case 3:
				TextView twoText = (TextView) twoView
						.findViewById(R.id.section_2label);
				twoText.setText("선배추천 강사정보");
				return twoView;
			case 4:

				TextView threeText = (TextView) threeView
						.findViewById(R.id.section_3label);
				threeText.setText("대학별입시요강");
				return threeView;

			case 5:
				WebView infoSite = (WebView) fourView
						.findViewById(R.id.section_4webview);
				infoSite.getSettings().setJavaScriptEnabled(true);
				infoSite.loadUrl("http://www.google.com");
				infoSite.setWebViewClient(new WebViewClientClass());
				return fourView;

			case 6:
				WebView blog = (WebView) fiveView
						.findViewById(R.id.section_5webview);
				blog.getSettings().setJavaScriptEnabled(true);
				blog.loadUrl("http://www.google.com");
				blog.setWebViewClient(new WebViewClientClass());
				return fiveView;

			}
			return null;
		}

		private class WebViewClientClass extends WebViewClient {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

}
