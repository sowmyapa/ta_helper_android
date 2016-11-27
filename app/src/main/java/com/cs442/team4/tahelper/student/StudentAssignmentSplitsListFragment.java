package com.cs442.team4.tahelper.student;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class StudentAssignmentSplitsListFragment extends ListFragment {

    String studentId, courseName, moduleName, moduleItem;

    View myFragmentView;
    private DatabaseReference mDatabase;

    TextView assignmentName;

    public static ArrayList<Split> splitsArrayList;
    public static StudentAssignmentSplitsListAdapter studentAssignmentSplitsListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Later, will have to check if bundle is there or not and then only assign the value...
        final String idFromActivity = getArguments().getString(IntentConstants.STUDENT_ID);
        final String courseNameFromActivity = getArguments().getString(IntentConstants.COURSE_NAME);
        final String moduleNameFromActivity = getArguments().getString(IntentConstants.MODULE_NAME);
        final String moduleItemFromActivity = getArguments().getString(IntentConstants.MODULE_ITEM);
        studentId = idFromActivity;
        courseName = courseNameFromActivity;
        moduleName = moduleNameFromActivity;
        moduleItem = moduleItemFromActivity;

        myFragmentView = inflater.inflate(R.layout.student_assignment_splits_list_fragment, container, false);
        assignmentName = (TextView) myFragmentView.findViewById(R.id.assignmentNameTextView);

        assignmentName.setText(moduleItem + " Splits"+" | "+studentId);

        int resID = R.layout.assignment_score_item_layout;

        splitsArrayList = new ArrayList<Split>();

        studentAssignmentSplitsListAdapter = new StudentAssignmentSplitsListAdapter(getContext(), resID, splitsArrayList);
        setListAdapter(studentAssignmentSplitsListAdapter);

        loadFromDatabase();

        return myFragmentView;
    }

    private void loadFromDatabase() {
        mDatabase.child("students").child(courseName).child(studentId).child(moduleName).child(moduleItem).child("Splits").push();

        mDatabase.child("students").child(courseName).child(studentId).child(moduleName).child(moduleItem).child("Splits").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                splitsArrayList.removeAll(splitsArrayList);
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    if(!splitsArrayList.contains(postSnapshot.getKey()))
                    {
                        String splitName = (String) postSnapshot.getKey();
                        String gainedPoints = (String) postSnapshot.getValue();

                        Split split = new Split(moduleName, splitName, 10.0, Double.parseDouble(gainedPoints));
                        splitsArrayList.add(split);

                        //For fetching maximum points
                        //mDatabase.child("modules").child(courseName).child(moduleName).child(moduleItem).child("Splits").push();
                        mDatabase.child("modules").child(courseName).child(moduleName).child(moduleItem).child("Splits").push();
                        mDatabase.child("modules").child(courseName).child(moduleName).child(moduleItem).child("Splits").addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                int i = 0;

                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                                {
                                    String score = "";
                                    score = (String) postSnapshot.getValue();

                                    splitsArrayList.get(i).setSplitMaximumPoints(Double.parseDouble(score));
                                    i++;
                                }

                                studentAssignmentSplitsListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("ModuleListFragment : "," Read cancelled due to "+databaseError.getMessage());
                            }
                        });

                    }
                }
                studentAssignmentSplitsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ModuleListFragment : "," Read cancelled due to "+databaseError.getMessage());
            }
        });

        Log.d("checkForSplitsArray : "," for module:"+moduleItem+" : "+splitsArrayList);

    }

}
