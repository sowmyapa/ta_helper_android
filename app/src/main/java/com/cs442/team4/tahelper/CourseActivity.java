package com.cs442.team4.tahelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;

import com.cs442.team4.tahelper.fragment.ManageCourseFragment;

public class CourseActivity extends AppCompatActivity implements add_course_fragment.OnFinishAddCourseInterface {

    String mode = "null";
    String courseId = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);




        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft =fm.beginTransaction();
        course_list_fragment cl = new course_list_fragment();
            cl.setInterface(new course_list_fragment.OnActionButtonClickListener() {

            @Override
            public void callAddCourseFragment(String mode_from_fragment) {
                mode = mode_from_fragment ;
                Bundle bundle = new Bundle();
                bundle.putString("mode", mode);

                add_course_fragment course_add_or_edit = new add_course_fragment();
                course_add_or_edit.setArguments(bundle);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft =fm.beginTransaction();

                ft.replace(R.id.course_activity_frame_layout,course_add_or_edit,"add_course_fragment_tag");
                ft.addToBackStack("course_list_fragment");
                ft.commit();
            }
                @Override
                public void callManageCourseFragment_to_activity(String courseCode)
                {
                   // courseId = courseCode;
                    Bundle bundle = new Bundle();
                    bundle.putString("course_code", courseCode);
                    Log.i("Code in activity : ", courseCode);
                    ManageCourseFragment newManageCourseFragment = new ManageCourseFragment();
                    newManageCourseFragment.setArguments(bundle);

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft =fm.beginTransaction();

                    ft.replace(R.id.course_activity_frame_layout,newManageCourseFragment,"add_course_fragment_tag");
                    ft.addToBackStack("course_list_fragment");
                    ft.commit();
                }
        });
        ft.replace(R.id.course_activity_frame_layout,cl,"course_list_fragment");
     //   ft.replace(R.id.course_activity_frame_layout,course_list,"course_list_fragment");
        ft.commit();


    }

    @Override
    public void closeAddCourseFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft =fm.beginTransaction();
        Fragment id = fm.findFragmentByTag("add_course_fragment_tag");
        ft.remove(id);
        fm.popBackStack();
        ft.commit();
    }



}