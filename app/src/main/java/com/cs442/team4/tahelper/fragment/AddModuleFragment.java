package com.cs442.team4.tahelper.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

import com.cs442.team4.tahelper.R;
import com.cs442.team4.tahelper.activity.AddModuleActivity;
import com.cs442.team4.tahelper.activity.ModuleListActivity;
import com.cs442.team4.tahelper.contants.IntentConstants;
import com.cs442.team4.tahelper.model.ModuleEntity;
import com.cs442.team4.tahelper.services.ModuleDatabaseUpdationIntentService;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by sowmyaparameshwara on 10/30/16.
 */

public class AddModuleFragment extends Fragment{

    public EditText enterModuleNameFragmentView;
    public EditText enterModuleWeightageFragmentView;
    public Button addModuleFragmentView;
   // private Button backButton;
    private DatabaseReference mDatabase;
    private AddModuleFragmentListener addModuleFragmentListener;
    private String courseName;
    private ArrayList<String> moduleList;
    public static final double MAX_WEIGHTAGE = 100.0;


    public interface AddModuleFragmentListener{
        public void addModuleEvent();
       // public void backToModuleList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.add_module_fragment,container,false);
        enterModuleNameFragmentView = (EditText) layout.findViewById(R.id.enterModuleNameFragmentView);
        enterModuleWeightageFragmentView = (EditText) layout.findViewById(R.id.enterModuleWeightage);
        addModuleFragmentView = (Button)layout.findViewById(R.id.addModuleButtonFragmentView);
       // backButton = (Button) layout.findViewById(R.id.addModuleButtonFragmentViewBackButton);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        courseName = getArguments().getString(IntentConstants.COURSE_ID);
        moduleList = getArguments().getStringArrayList(IntentConstants.MODULE_LIST);

      /*  backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addModuleFragmentListener.backToModuleList();
            }
        });*/

        addModuleFragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moduleName = enterModuleNameFragmentView.getText().toString();
                String moduleWeightage = enterModuleWeightageFragmentView.getText().toString();

                if(moduleName!=null && moduleName.length()>0 && moduleWeightage!=null && moduleWeightage.length()>0){
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
             /*    mDatabase.child("modules").child("InClass Assignments").setValue("");
                    mDatabase.child("modules").child("HW Assignments").setValue("");
                    mDatabase.child("modules").child("Project").setValue("");
                    mDatabase.child("modules").child("Exam").setValue("");
                    mDatabase.child("modules").child("Final Score").setValue("");*/
                    // key  = mDatabase.push().getKey();
                  //  mDatabase.child("modules").child(key).child("name").setValue(moduleName);
                    if(isValidName(moduleName,moduleWeightage)){
                        mDatabase.child("modules").child(courseName).child(moduleName).child("weightage").setValue(moduleWeightage);
                        ModuleEntity.addModule(moduleName,moduleWeightage);


                        Intent serviceIntent = new Intent(getActivity(), ModuleDatabaseUpdationIntentService.class);
                        serviceIntent.putExtra(IntentConstants.MODULE_NAME,moduleName);
                        serviceIntent.putExtra(IntentConstants.MODE,"Add");
                        serviceIntent.putExtra(IntentConstants.COURSE_ID,courseName);
                        serviceIntent.putExtra(IntentConstants.MODULE_WEIGHTAGE,moduleWeightage);

                        getActivity().startService(serviceIntent);

                        //ModuleEntity.addKeyValue(moduleName,key);
                        addModuleFragmentListener.addModuleEvent();;
                    }


                }else{
                    Toast.makeText(getActivity()," Please correct the errors and try again.",Toast.LENGTH_LONG).show();
                    if(moduleName.trim().length() <=0)
                    {
                        enterModuleNameFragmentView.setError("Module Name cannot be empty");
                    }
                    if(moduleWeightage.trim().length() <=0 ){
                        enterModuleWeightageFragmentView.setError("Module weightage cannot be empty");
                    }
                }

            }
        });
        return layout;
    }

    private boolean isValidName(String moduleName,String moduleWeightage) {
        boolean isValid = true;
        for(String existingName : moduleList){
            if(existingName.equals(moduleName)){
                enterModuleNameFragmentView.setError("Duplicate Module Name");
                Toast.makeText(getActivity(),"Please correct the errors and try again.",Toast.LENGTH_LONG).show();

                isValid =  false;
            }
        }
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(moduleName);
        boolean b = m.find();
        if (isValid && b == true){
            enterModuleNameFragmentView.setError("Module Name should not contain special characters");
            Toast.makeText(getActivity(),"Please correct the errors and try again.",Toast.LENGTH_LONG).show();

            isValid =  false;
        }

        if(!Character.isDigit(moduleWeightage.charAt(0))){
            enterModuleWeightageFragmentView.setError("Module Weightage should begin with a number");
            Toast.makeText(getActivity(),"Please correct the errors and try again.",Toast.LENGTH_LONG).show();

            isValid =  false;
        }else {
            double weigthage = Double.parseDouble(moduleWeightage);
            if (isValid && weigthage > MAX_WEIGHTAGE) {
                enterModuleWeightageFragmentView.setError("Module Weightage should be less than 100.0");
                Toast.makeText(getActivity(), "Please correct the errors and try again.", Toast.LENGTH_LONG).show();

                isValid = false;
            }
        }

        return isValid;
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


/*    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences("com.cs442.team4.tahelper.AddModuleFragment", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("firstrun", true);
        if (isFirstRun)
        {
            prefs.edit().putBoolean("firstrun", false).commit();
            showFirstShowCase();
        }


    }*/

    private void showFirstShowCase(){
        new ShowcaseView.Builder(getActivity())
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(enterModuleNameFragmentView))
                .hideOnTouchOutside()
                .setContentTitle("Enter name for the new module to be added.")
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showSecondShowCase();
                    }

                })
                .build();
    }

    private void showSecondShowCase() {
        new ShowcaseView.Builder(getActivity())
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(addModuleFragmentView))
                .hideOnTouchOutside()
                .setContentTitle("Click the button to add the new module.")
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                       // showFourthShowCase();
                    }

                })
                .build();
    }

 /*   private void showThirdShowCase() {
        new ShowcaseView.Builder(getActivity())
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(backButton))
                .hideOnTouchOutside()
                .setContentTitle("Click to go back to module list without adding.")
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showFourthShowCase();
                    }

                })
                .build();
    }*/



   /* private void showFourthShowCase() {
        new ShowcaseView.Builder(getActivity())
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(((AddModuleActivity)getActivity()).mDrawerLayout))
                .hideOnTouchOutside()
                .setContentTitle("Swipe from left to launch drawer with navigation options.")
                .build();
    }*/

 /*   public void initialise(Intent intent) {
        if(intent!=null && intent.getStringExtra(IntentConstants.COURSE_ID)!=null){
            courseName = intent.getStringExtra(IntentConstants.COURSE_ID);
        }
    }*/





}
