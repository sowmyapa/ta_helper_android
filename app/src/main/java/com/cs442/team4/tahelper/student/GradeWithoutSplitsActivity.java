package com.cs442.team4.tahelper.student;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs442.team4.tahelper.DownloadImageTask;
import com.cs442.team4.tahelper.MainActivity;
import com.cs442.team4.tahelper.R;
import com.cs442.team4.tahelper.contants.IntentConstants;
import com.cs442.team4.tahelper.model.UserEntity;
import com.cs442.team4.tahelper.preferences.MyPreferenceActivity;
import com.cs442.team4.tahelper.utils.ObjectUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by Mohammed on 11/9/2016.
 */

public class GradeWithoutSplitsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    String studentId = "nothing";
    String courseName = "nothing";
    String moduleName = "nothing";
    String moduleItem = "nothing";

    UserEntity user;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grade_without_splits_activity);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft =fm.beginTransaction();

        Intent intent = getIntent();
        if(intent.getStringExtra(IntentConstants.STUDENT_ID)!=null){
            studentId = intent.getStringExtra(IntentConstants.STUDENT_ID);
        }
        if(intent.getStringExtra(IntentConstants.COURSE_NAME)!=null){
            courseName = intent.getStringExtra(IntentConstants.COURSE_NAME);
        }
        if(intent.getStringExtra(IntentConstants.MODULE_NAME)!=null){
            moduleName = intent.getStringExtra(IntentConstants.MODULE_NAME);
        }
        if(intent.getStringExtra(IntentConstants.MODULE_ITEM)!=null){
            moduleItem = intent.getStringExtra(IntentConstants.MODULE_ITEM);
        }
        if(getIntent().getSerializableExtra("USER_DETAILS")!=null){
            user = (UserEntity) getIntent().getSerializableExtra("USER_DETAILS");
        }

        GradeWithoutSplitsFragment gradeWithoutSplitsFragment = new GradeWithoutSplitsFragment();

        Bundle bundle = new Bundle();
        bundle.putString(IntentConstants.STUDENT_ID, studentId);
        bundle.putString(IntentConstants.COURSE_NAME, courseName);
        bundle.putString(IntentConstants.MODULE_NAME, moduleName);
        bundle.putString(IntentConstants.MODULE_ITEM, moduleItem);

        gradeWithoutSplitsFragment.setArguments(bundle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_grade_without_splits);
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_grade_without_splits);
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

        ft.replace(R.id.grade_without_splits_activity_frame_layout,gradeWithoutSplitsFragment,"grade_without_splits_fragment");
        ft.commit();

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
            //showCase();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_grade_student_list);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

}
