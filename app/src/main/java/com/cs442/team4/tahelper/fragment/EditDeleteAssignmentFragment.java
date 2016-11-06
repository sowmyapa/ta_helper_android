package com.cs442.team4.tahelper.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cs442.team4.tahelper.R;
import com.cs442.team4.tahelper.contants.IntentConstants;
import com.cs442.team4.tahelper.listItem.AddAssignmentListItemAdapter;
import com.cs442.team4.tahelper.listItem.EditDeleteAssignmentListItemAdapter;
import com.cs442.team4.tahelper.model.AssignmentEntity;
import com.cs442.team4.tahelper.model.AssignmentSplit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by sowmyaparameshwara on 11/1/16.
 */

public class EditDeleteAssignmentFragment extends Fragment {


    private Button addSplitButton;
    private EditText assignmentName;
    private EditText assignmentTotalScore;
    private EditText splitName;
    private EditText splitScore;
    private ListView splitList;
    private ArrayList<AssignmentSplit> assignmentSplitsList;
    private EditDeleteAssignmentListItemAdapter assignmentAdapter;
    private Button editAssignment;
    private Button deleteAssignment;
    private DatabaseReference mDatabase;
    private String moduleName;
    private EditDeleteAssignmentsFragmentListener editDeleteAssignmentFragmentListener;
    private String originalAssignmentName;

    public interface EditDeleteAssignmentsFragmentListener{
        public void notifyEditDeleteAssignmentEvent(String moduleName);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.editdelete_assignment_fragment,container,false);
        addSplitButton = (Button) layout.findViewById(R.id.editDeleteAssignmentsFragmentButton);
        splitName = (EditText) layout.findViewById(R.id.editDeleteAssignmentFragmentSplitName);
        splitScore = (EditText) layout.findViewById(R.id.editDeleteAssignmentFragmentSplitScore);
        splitList = (ListView) layout.findViewById(R.id.editDeleteAssignmentsFragmentListView);
        editAssignment = (Button) layout.findViewById(R.id.editDeleteAssignmentsFragmentEditButton);
        deleteAssignment = (Button) layout.findViewById(R.id.editDeleteAssignmentsFragmentDeleteButton);
        assignmentName = (EditText) layout.findViewById(R.id.editDeleteAssignmentsFragmentTextView);
        assignmentTotalScore = (EditText) layout.findViewById(R.id.editDeleteAssignmentFragmentTotalScore);
        assignmentSplitsList = new ArrayList<AssignmentSplit>();
        assignmentAdapter = new EditDeleteAssignmentListItemAdapter(getActivity(),R.layout.add_assignments_item_layout,assignmentSplitsList);
        splitList.setAdapter(assignmentAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference("modules");

        addSplitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(splitName.getText()!=null && splitName.getText().length()>0 && splitScore.getText()!=null && splitScore.getText().length()>0){
                    assignmentSplitsList.add(new AssignmentSplit(splitName.getText().toString(),Integer.parseInt(splitScore.getText().toString())));
                    splitName.setText("");
                    splitScore.setText("");
                    assignmentAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(),"Please enter both split name, score and try again.",Toast.LENGTH_LONG).show();
                }
            }
        });

        editAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEditAssignment();
            }
        });

        deleteAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteAssignment();
            }
        });
        return layout;
    }

    private void handleEditAssignment() {
        if(assignmentName.getText()!=null && assignmentName.getText().length()>0 && assignmentTotalScore.getText()!=null && assignmentTotalScore.getText().length()>0){
            mDatabase.child(moduleName).child(originalAssignmentName).setValue(null);

            mDatabase.child(moduleName).child(assignmentName.getText().toString()).child("Total").setValue(assignmentTotalScore.getText().toString());
            assignmentSplitsList.remove(assignmentSplitsList);
            for(int i = 0 ; i <assignmentSplitsList.size();i++){
                AssignmentSplit split = assignmentSplitsList.get(i);
                mDatabase.child(moduleName).child(assignmentName.getText().toString()).child("Splits").child(split.getSplitName()).setValue(String.valueOf(split.getSplitScore()));
            }
            assignmentAdapter.notifyDataSetChanged();

            editDeleteAssignmentFragmentListener.notifyEditDeleteAssignmentEvent(moduleName);
        }else{
            Toast.makeText(getActivity(),"Please enter both assignment name, total score and try again.",Toast.LENGTH_LONG).show();
        }
    }

    private void handleDeleteAssignment() {
        mDatabase.child(moduleName).child(originalAssignmentName).setValue(null);
        editDeleteAssignmentFragmentListener.notifyEditDeleteAssignmentEvent(moduleName);
    }


    public void deleteSplit(AssignmentSplit split) {
        assignmentSplitsList.remove(split);
        assignmentAdapter.notifyDataSetChanged();
    }

    public void initialise(Intent intent) {
        if(intent!=null && intent.getStringExtra(IntentConstants.MODULE_NAME)!=null){
            moduleName = intent.getStringExtra(IntentConstants.MODULE_NAME);
            originalAssignmentName = intent.getStringExtra(IntentConstants.ASSIGNMENT_NAME);
            assignmentName.setText(originalAssignmentName);
            assignmentName.setSelection(assignmentName.getText().length());
            loadFromDatabase();
        }
    }

    private void loadFromDatabase() {
        mDatabase.child(moduleName).child(originalAssignmentName).push();
        mDatabase.child(moduleName+"/"+originalAssignmentName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("EditDelete"," dataSnapshot : "+dataSnapshot.getValue(AssignmentEntity.class));
                Log.i("EditDelete"," children : "+dataSnapshot.getChildren()+"  "+dataSnapshot.getChildrenCount());


                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if(postSnapshot.getKey().equals("Total")){
                        assignmentTotalScore.setText(postSnapshot.getValue(String.class));
                        assignmentTotalScore.setSelection(assignmentTotalScore.length());
                    }
                    if(postSnapshot.getKey().equals("Splits")){
                       assignmentSplitsList.removeAll(assignmentSplitsList);
                       for (DataSnapshot splits: postSnapshot.getChildren()) {
                          assignmentSplitsList.add(new AssignmentSplit(splits.getKey(),Integer.parseInt(splits.getValue().toString())));
                       }
                       assignmentAdapter.notifyDataSetChanged();
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ModuleListFragment : "," Read cancelled due to "+databaseError.getMessage());
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            editDeleteAssignmentFragmentListener = (EditDeleteAssignmentsFragmentListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnNewItemAddedListener");
        }
    }
}