package com.cs442.team4.tahelper.listItem;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cs442.team4.tahelper.R;
import com.cs442.team4.tahelper.activity.AddAssignmentsActivity;
import com.cs442.team4.tahelper.activity.EditDeleteAssignmentActivity;
import com.cs442.team4.tahelper.model.AssignmentSplit;

import java.util.ArrayList;

/**
 * Created by sowmyaparameshwara on 10/31/16.
 */

public class EditDeleteAssignmentListItemAdapter extends ArrayAdapter<AssignmentSplit> {

    private Context context;
    private int resource;
    private boolean graded;


    public EditDeleteAssignmentListItemAdapter(Context context, int resource, ArrayList<AssignmentSplit> addSplitList) {
        super(context,resource,addSplitList);
        this.resource = resource;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout layout= null;

        if (convertView == null) {
            layout = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, layout, true);
        } else {
            layout = (LinearLayout) convertView;
        }
        TextView splitName = (TextView) layout.findViewById(R.id.addAssignmentsSplitName);
        TextView splitScore = (TextView) layout.findViewById(R.id.addAssignmentsSplitScore);
        final AssignmentSplit split = getItem(position);
        Button splitDelete = (Button) layout.findViewById(R.id.addAssignmentsSplitDelete);

        if(graded){
            splitDelete.setFocusable(false);
            splitDelete.setBackgroundColor(Color.DKGRAY);
        }else{
            splitDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((EditDeleteAssignmentActivity)context).deleteSplit(split);
                }
            });
        }

        splitName.setText(split.getSplitName());
        splitScore.setText(String.valueOf(split.getSplitScore()));
        return layout;
    }


    public void setGraded(boolean graded) {
        this.graded = graded;
    }
}
