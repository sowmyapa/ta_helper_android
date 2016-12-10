package com.cs442.team4.tahelper.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cs442.team4.tahelper.CourseActivity;
import com.cs442.team4.tahelper.DownloadImageTask;
import com.cs442.team4.tahelper.MainActivity;
import com.cs442.team4.tahelper.R;
import com.cs442.team4.tahelper.contants.IntentConstants;
import com.cs442.team4.tahelper.fragment.AddModuleFragment;
import com.cs442.team4.tahelper.fragment.EditDeleteModuleFragment;
import com.cs442.team4.tahelper.fragment.ModuleListFragment;
import com.cs442.team4.tahelper.model.UserEntity;
import com.cs442.team4.tahelper.preferences.MyPreferenceActivity;
import com.cs442.team4.tahelper.utils.ObjectUtils;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by sowmyaparameshwara on 10/30/16.
 */

public class EditDeleteModuleActivity extends AppCompatActivity implements  EditDeleteModuleFragment.EditDeleteButtonListner, NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener{

    private String courseCode;
/*    private ListView mDrawerList;
    private String[] drawerList;
    public DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;*/
    private static final int SHOW_PREFERENCES = 0;
    UserEntity user;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle onSavedInstance){
        super.onCreate(onSavedInstance);
        setContentView(R.layout.edit_delete_module_activity);
        android.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        EditDeleteModuleFragment editDeleteModuleFragment = new EditDeleteModuleFragment();


        if(getIntent().getStringExtra(IntentConstants.COURSE_ID)!=null){
            courseCode = getIntent().getStringExtra(IntentConstants.COURSE_ID);
            user = (UserEntity) getIntent().getSerializableExtra("USER_DETAILS");

            Bundle bundle = new Bundle();
            bundle.putString(IntentConstants.MODULE_NAME,getIntent().getStringExtra(IntentConstants.MODULE_NAME));
            bundle.putString(IntentConstants.MODULE_WEIGHTAGE,getIntent().getStringExtra(IntentConstants.MODULE_WEIGHTAGE));
            bundle.putString(IntentConstants.COURSE_ID,courseCode);
            bundle.putStringArrayList(IntentConstants.MODULE_LIST,getIntent().getStringArrayListExtra(IntentConstants.MODULE_LIST));
            editDeleteModuleFragment.setArguments(bundle);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_edit_delete_module);
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_edit_delete_module);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) hView.findViewById(R.id.profilePic);
        TextView userName = (TextView) hView.findViewById(R.id.userName);
        TextView emailIdtextView = (TextView) hView.findViewById(R.id.emailIdtextView);

        if(user!=null) {

            emailIdtextView.setText(user.getEmail());
            userName.setText(user.getDisplayName());

            if (ObjectUtils.isNotEmpty(user.getPhotoUrl()))
                new DownloadImageTask(imageView)
                        .execute(user.getPhotoUrl());
        }

        //drawerCode();
        ft.replace(R.id.editDeleteModuleActivityFrameLayout, editDeleteModuleFragment, "edit_delete_module_fragment");
        ft.commit();

    }

    @Override
    public void clickButtonEvent() {

        Intent intent = new Intent(this,ModuleListActivity.class);
        intent.putExtra(IntentConstants.COURSE_ID,courseCode);
        intent.putExtra("USER_DETAILS",user);

        startActivity(intent);
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);

        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ModuleListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(IntentConstants.COURSE_ID,courseCode);
        intent.putExtra("USER_DETAILS",user);

        startActivity(intent);
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);

        finish();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            try {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));
                sendIntent.putExtra("sms_body", "TA Helper is a one stop solution to manage work related to teaching assistant's");
                startActivity(sendIntent);
                getFragmentManager().popBackStack();
            } catch (ActivityNotFoundException e) {
            }
        }else if(id == R.id.nav_notification_settings){
            Class<?> c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?
                    MyPreferenceActivity.class : MyPreferenceActivity.class;

            Intent i = new Intent(this, c);
            startActivityForResult(i, 0);
        }else if(id == R.id.nav_notification_help){
            showCase();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_edit_delete_module);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showCase() {
        EditDeleteModuleFragment editDeleteModuleFragment = (EditDeleteModuleFragment) getFragmentManager().findFragmentById(R.id.editDeleteModuleActivityFrameLayout);
        if(editDeleteModuleFragment.editButton.getVisibility() == View.VISIBLE){
            new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setTarget(new ViewTarget(editDeleteModuleFragment.moduleName))
                    .hideOnTouchOutside()
                    .setContentTitle("The module can be renamed here.\n\n")
                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            showSecondShowCase();
                        }

                    })
                    .build();
        }else{
            new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setTarget(new ViewTarget(editDeleteModuleFragment.moduleName))
                    .hideOnTouchOutside()
                    .setContentTitle("The module name.\n\nNote : This field is disabled since atleast one sub module associated with this module has been graded for atleast one student.\n\n")
                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            showSecondShowCase();
                        }

                    })
                    .build();
        }

    }

    private void showSecondShowCase() {

        EditDeleteModuleFragment editDeleteModuleFragment = (EditDeleteModuleFragment) getFragmentManager().findFragmentById(R.id.editDeleteModuleActivityFrameLayout);
        if(editDeleteModuleFragment.editButton.getVisibility() == View.VISIBLE) {
            new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setTarget(new ViewTarget(editDeleteModuleFragment.moduleWeightage))
                    .hideOnTouchOutside()
                    .setContentTitle("Weightage can be changed here.\n\n")
                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            showThirdShowCase();
                        }

                    })
                    .build();
        }else{
            new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setTarget(new ViewTarget(editDeleteModuleFragment.moduleWeightage))
                    .hideOnTouchOutside()
                    .setContentTitle("The module weightage.\n\n" +
                            "Note : This field is disabled since atleast one sub module associated with this module has been graded for atleast one student.\n\n")
                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            showThirdShowCase();
                        }

                    })
                    .build();
        }

    }

    private void showThirdShowCase() {
        EditDeleteModuleFragment editDeleteModuleFragment = (EditDeleteModuleFragment) getFragmentManager().findFragmentById(R.id.editDeleteModuleActivityFrameLayout);
        if(editDeleteModuleFragment.editButton.getVisibility() == View.VISIBLE){
            new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setTarget(new ViewTarget(editDeleteModuleFragment.editButton))
                    .hideOnTouchOutside()
                    .setContentTitle("Click this to button to finalise the editted changes.\n\n")
                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            showFourthShowCase();
                        }

                    })
                    .build();
        }else{
            showFourthShowCase();
        }

    }

    private void showFourthShowCase() {
        EditDeleteModuleFragment editDeleteModuleFragment = (EditDeleteModuleFragment) getFragmentManager().findFragmentById(R.id.editDeleteModuleActivityFrameLayout);
        new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(editDeleteModuleFragment.deleteButton))
                .hideOnTouchOutside()
                .setContentTitle("Click this button to delete this module from the course.\n\n")
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                    }

                })
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {


            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    Log.d("team4", "onResult:Logout ");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("team4", "onConnectionFailed:" + connectionResult);
    }

 /*   public void backToModuleList(){
        Intent intent = new Intent(this,ModuleListActivity.class);
        intent.putExtra(IntentConstants.COURSE_ID,courseCode);
        startActivity(intent);
    }*/

   /* private void drawerCode() {
        mDrawerList = (ListView) findViewById(R.id.left_drawer_edit_delete_module);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_edit_delete_module);


        drawerList = new String[3];
        drawerList[0] = " Home ";
        drawerList[1] = " Settings ";
        drawerList[2] = " Sign Out ";


        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerList));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  *//* host Activity *//*
                mDrawerLayout,         *//* DrawerLayout object *//*
                R.drawable.ic_drawer,  *//* nav drawer image to replace 'Up' caret *//*
                R.string.drawer_open,  *//* "open drawer" description for accessibility *//*
                R.string.drawer_close  *//* "close drawer" description for accessibility *//*
        ) {
            public void onDrawerClosed(View view) {
                // getActionBar().setTitle("Ta-Helper");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                // getActionBar().setTitle("Ta-Helper Shortcuts");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNotification = sharedPref.getBoolean("PREF_CHECK_BOX", false);
        Log.i("", "isNotification : " + isNotification);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        if (position == 2) {
            Intent loginscreen = new Intent(this, MainActivity.class);
            loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginscreen);
            this.finish();
        } else if (position == 0) {
            Intent loginscreen = new Intent(this, CourseActivity.class);
            startActivity(loginscreen);
        } else if (position == 1) {
            Class<?> c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?
                    MyPreferenceActivity.class : MyPreferenceActivity.class;

            Intent i = new Intent(this, c);
            startActivityForResult(i, SHOW_PREFERENCES);

        }
    }*/
}
