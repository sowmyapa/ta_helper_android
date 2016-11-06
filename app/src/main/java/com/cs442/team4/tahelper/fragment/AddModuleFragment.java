package com.cs442.team4.tahelper.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;

import com.cs442.team4.tahelper.R;
import com.cs442.team4.tahelper.contants.IntentConstants;
import com.cs442.team4.tahelper.model.ModuleEntity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sowmyaparameshwara on 10/30/16.
 */

public class AddModuleFragment extends Fragment{

    private EditText enterModuleNameFragmentView;
    private Button addModuleFragmentView;
    private DatabaseReference mDatabase;
    private AddModuleFragmentListener addModuleFragmentListener;


    public interface AddModuleFragmentListener{
        public void addModuleEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.add_module_fragment,container,false);
        enterModuleNameFragmentView = (EditText) layout.findViewById(R.id.enterModuleNameFragmentView);
        addModuleFragmentView = (Button)layout.findViewById(R.id.addModuleButtonFragmentView);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        addModuleFragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moduleName = enterModuleNameFragmentView.getText().toString();
                if(moduleName!=null && moduleName.length()>0){
                    String key; /* = mDatabase.push().getKey();
                    mDatabase.child("modules").child(key).child("name").setValue(moduleName);
                    ModuleEntity.addKeyValue("InClass Assignments",key);
                    addModuleFragmentListener.addModuleEvent();;

                     key  = mDatabase.push().getKey();
                    mDatabase.child("modules").child(key).child("name").setValue("InClass Assignments");
                    ModuleEntity.addKeyValue(moduleName,key);
                    addModuleFragmentListener.addModuleEvent();;

                     key  = mDatabase.push().getKey();
                    mDatabase.child("modules").child(key).child("name").setValue("HW Assignments");
                    ModuleEntity.addKeyValue("HW Assignments",key);
                    addModuleFragmentListener.addModuleEvent();;

                     key  = mDatabase.push().getKey();
                    mDatabase.child("modules").child(key).child("name").setValue("Project");
                    ModuleEntity.addKeyValue("Project",key);
                    addModuleFragmentListener.addModuleEvent();;

                     key  = mDatabase.push().getKey();
                    mDatabase.child("modules").child(key).child("name").setValue("Exam");
                    ModuleEntity.addKeyValue("Exam",key);
                    addModuleFragmentListener.addModuleEvent();;

                     key  = mDatabase.push().getKey();
                    mDatabase.child("modules").child(key).child("name").setValue("Final Score");
                    ModuleEntity.addKeyValue("Final Score",key);
                    addModuleFragmentListener.addModuleEvent();;
*/
               /*    mDatabase.child("modules").push().child("name").setValue("InClass Assignments");
                    mDatabase.child("modules").push().child("name").setValue("HW Assignments");
                    mDatabase.child("modules").push().child("name").setValue("Project");
                    mDatabase.child("modules").push().child("name").setValue("Exam");
                    mDatabase.child("modules").push().child("name").setValue("Final Score");*/
                    // key  = mDatabase.push().getKey();
                  //  mDatabase.child("modules").child(key).child("name").setValue(moduleName);
                    mDatabase.child("modules").child(moduleName).setValue("");
                    //ModuleEntity.addKeyValue(moduleName,key);
                    addModuleFragmentListener.addModuleEvent();;

                }

            }
        });
        return layout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            addModuleFragmentListener = (AddModuleFragmentListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnNewItemAddedListener");
        }
    }




}