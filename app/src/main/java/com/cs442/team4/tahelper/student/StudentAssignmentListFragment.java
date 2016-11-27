package com.cs442.team4.tahelper.student;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs442.team4.tahelper.R;
import com.cs442.team4.tahelper.contants.IntentConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Mohammed on 11/3/2016.
 */

public class StudentAssignmentListFragment extends ListFragment {

    String studentId, courseName, moduleName;

    private DatabaseReference mDatabase;
    View myFragmentView;
    //StudentAssignmentListFragment.OnAssignmentClickListener onAssignmentClickListener;

    private ArrayList<Split> checkForSplitsArray;

    TextView assignmentName;

    public static ArrayList<Assignment> assignmentsArraylist;
    public static StudentAssignmentListAdapter studentAssignmentListAdapter;

    /*
    public interface OnAssignmentClickListener {
        public void showAssignmentSplits(String courseName, String studentId, String moduleName, String moduleItem);
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Later, will have to check if bundle is there or not and then only assign the value...
        final String idFromActivity = getArguments().getString(IntentConstants.STUDENT_ID);
        final String courseNameFromActivity = getArguments().getString(IntentConstants.COURSE_NAME);
        final String moduleNameFromActivity = getArguments().getString(IntentConstants.MODULE_NAME);
        studentId = idFromActivity;
        courseName = courseNameFromActivity;
        moduleName = moduleNameFromActivity;

        myFragmentView = inflater.inflate(R.layout.student_assignment_list_fragment, container, false);
        assignmentName = (TextView) myFragmentView.findViewById(R.id.moduleNameTextView);

        assignmentName.setText(moduleName + " | " + studentId);

        int resID = R.layout.assignment_score_item_layout;

        assignmentsArraylist = new ArrayList<Assignment>();

        studentAssignmentListAdapter = new StudentAssignmentListAdapter(getContext(), resID, assignmentsArraylist);
        setListAdapter(studentAssignmentListAdapter);

        loadFromDatabase();
        //getMaxPointsFromDatabase();

        return myFragmentView;
    }

    //Not in use now. Old logic for fetching maxPoints
    private void getMaxPointsFromDatabase()
    {
        mDatabase.child("modulesNew").child(courseName).child(moduleName).push();

        mDatabase.child("modulesNew").child(courseName).child(moduleName).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int i = assignmentsArraylist.size();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    if(moduleName.equals("Assignments"))
                    {
                        Double score = 0.0;
                        score = (Double) postSnapshot.child("score").getValue();

                        assignmentsArraylist.get(i).setMaximumPoints(score);
                        i++;
                    }

                }

                studentAssignmentListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ModuleListFragment : "," Read cancelled due to "+databaseError.getMessage());
            }
        });

    }

    private void loadFromDatabase() {

        //getMaxPointsFromDatabase();

        mDatabase.child("students").child(courseName).child(studentId).child(moduleName).push();

        mDatabase.child("students").child(courseName).child(studentId).child(moduleName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //int i=0;

                assignmentsArraylist.removeAll(assignmentsArraylist);
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    Long childrenCount = 0L;
                    childrenCount = (Long) postSnapshot.child("Splits").getChildrenCount();

                    Double maxPoints = 0.0;

                    if(!assignmentsArraylist.contains(postSnapshot.getKey()) && !postSnapshot.getKey().equals("weightage"))
                    {
                        String score = (String) postSnapshot.child("Total").getValue();

                        Assignment assignment = new Assignment();
                        assignment.setName((String)postSnapshot.getKey());
                        assignment.setGainedPoints(Double.parseDouble(score));

                        //This value we will have to fetch from other root database from firebase
                        //Or include in the 'students' root database
                        //assignment.setMaximumPoints(maxPoints);

                        if(childrenCount==0)
                        {
                            assignment.setHasSplits(false);
                        }

                        assignmentsArraylist.add(assignment);

                        //For fetching maximum points
                        //mDatabase.child("modules").child(courseName).child(moduleName).push();
                        mDatabase.child("modules").child(courseName).child(moduleName).push();
                        mDatabase.child("modules").child(courseName).child(moduleName).addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                int i = 0;

                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                                {
                                    if(i<assignmentsArraylist.size())
                                    {
                                        String score = "";
                                        score = (String) postSnapshot.child("Total").getValue();

                                        assignmentsArraylist.get(i).setMaximumPoints(Double.parseDouble(score));
                                        i++;
                                    }

                                }

                                studentAssignmentListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("ModuleListFragment : "," Read cancelled due to "+databaseError.getMessage());
                            }
                        });

                        //Old code for fetching module items and their marks. Above code is dynamic
                        //But will not work for stuff like 'Final Grade' where value is a String for e.g: 'B'

                    }
                }
                studentAssignmentListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ModuleListFragment : "," Read cancelled due to "+databaseError.getMessage());
            }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Assignment assignment =  (Assignment) getListView().getItemAtPosition(position);
        String moduleItem = assignment.getName();

        if(assignment.getHasSplits()==true)
        {
            //onAssignmentClickListener.showAssignmentSplits(courseName, studentId, moduleName, moduleItem);

            /*
            Intent intent = new Intent(getActivity(),StudentAssignmentSplitsListActivity.class);
            intent.putExtra(IntentConstants.STUDENT_ID, studentId);
            intent.putExtra(IntentConstants.COURSE_NAME, courseName);
            intent.putExtra(IntentConstants.MODULE_NAME, moduleName);
            intent.putExtra(IntentConstants.MODULE_ITEM, moduleItem);
            startActivity(intent);
            */

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            StudentAssignmentSplitsListFragment studentAssignmentSplitsListFragment = new StudentAssignmentSplitsListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(IntentConstants.STUDENT_ID, studentId);
            bundle.putString(IntentConstants.COURSE_NAME, courseName);
            bundle.putString(IntentConstants.MODULE_NAME, moduleName);
            bundle.putString(IntentConstants.MODULE_ITEM, moduleItem);
            studentAssignmentSplitsListFragment.setArguments(bundle);
            ft.replace(R.id.course_activity_frame_layout,studentAssignmentSplitsListFragment,"student_assignment_splits_list_fragment");
            ft.addToBackStack("student_assignment_list_fragment");
            ft.commit();

        }
        else
        {
            Toast.makeText(getActivity(), "No Splits for "+moduleItem, Toast.LENGTH_SHORT).show();
        }

    }



    /*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onAssignmentClickListener = (StudentAssignmentListFragment.OnAssignmentClickListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnStudentClickListener");
        }
    }
    */

}
