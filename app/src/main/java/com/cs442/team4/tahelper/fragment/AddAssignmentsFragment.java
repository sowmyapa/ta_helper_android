package com.cs442.team4.tahelper.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cs442.team4.tahelper.R;
import com.cs442.team4.tahelper.contants.IntentConstants;
import com.cs442.team4.tahelper.listItem.AddAssignmentListItemAdapter;
import com.cs442.team4.tahelper.model.AssignmentSplit;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by sowmyaparameshwara on 10/31/16.
 */

public class AddAssignmentsFragment extends Fragment {

    private Button addSplitButton;
    private EditText assignmentName;
    private EditText assignmentTotalScore;
    private EditText splitName;
    private EditText splitScore;
    private ListView splitList;
    private ArrayList<AssignmentSplit> assignmentSplitsList;
    private AddAssignmentListItemAdapter assignmentAdapter;
    private Button addAssignment;
    private DatabaseReference mDatabase;
    private String moduleName;
    private AddAssignmentsFragmentListener addAssignmentFragmentListener;

    public interface AddAssignmentsFragmentListener{
        public void notifyAddAssignmentEvent(String moduleName);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.add_assignment_fragment,container,false);
        addSplitButton = (Button) layout.findViewById(R.id.addAssignmentsFragmentButton);
        splitName = (EditText) layout.findViewById(R.id.addAssignmentFragmentSplitName);
        splitScore = (EditText) layout.findViewById(R.id.addAssignmentFragmentSplitScore);
        splitList = (ListView) layout.findViewById(R.id.addAssignmentsFragmentListView);
        addAssignment = (Button) layout.findViewById(R.id.addAssignmentsFragmentAddButton);
        assignmentName = (EditText) layout.findViewById(R.id.addAssignmentsFragmentTextView);
        assignmentTotalScore = (EditText) layout.findViewById(R.id.addAssignmentFragmentTotalScore);
        assignmentSplitsList = new ArrayList<AssignmentSplit>();
        assignmentAdapter = new AddAssignmentListItemAdapter(getActivity(),R.layout.add_assignments_item_layout,assignmentSplitsList);
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

        addAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               handleAddAssignment();
            }
        });
        return layout;
    }

    private void handleAddAssignment() {
        if(assignmentName.getText()!=null && assignmentName.getText().length()>0 && assignmentTotalScore.getText()!=null && assignmentTotalScore.getText().length()>0){
           mDatabase.child(moduleName).child(assignmentName.getText().toString()).child("Total").setValue(assignmentTotalScore.getText().toString());
           for(int i = 0 ; i <assignmentSplitsList.size();i++){
               AssignmentSplit split = assignmentSplitsList.get(i);
               mDatabase.child(moduleName).child(assignmentName.getText().toString()).child("Splits").child(split.getSplitName()).setValue(String.valueOf(split.getSplitScore()));
           }
            addAssignmentFragmentListener.notifyAddAssignmentEvent(moduleName);
        }else{
            Toast.makeText(getActivity(),"Please enter both assignment name, total score and try again.",Toast.LENGTH_LONG).show();
        }
    }

    public void deleteSplit(AssignmentSplit split) {
        assignmentSplitsList.remove(split);
        assignmentAdapter.notifyDataSetChanged();
    }

    public void initialise(Intent intent) {
        if(intent!=null && intent.getStringExtra(IntentConstants.MODULE_NAME)!=null){
            moduleName = intent.getStringExtra(IntentConstants.MODULE_NAME);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            addAssignmentFragmentListener = (AddAssignmentsFragmentListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnNewItemAddedListener");
        }
    }

}