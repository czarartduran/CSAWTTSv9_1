package com.patchie.csawttsv9;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jibeh on 1/15/2018.
 */

public class ContactlistAdapter extends CursorAdapter {


    public ContactlistAdapter(Context context, Cursor cursor){
        super(context, cursor, true);

    }


    @Override

    public void bindView(View view, Context context, Cursor cursor) {
        ImageView typeImage = (ImageView)view.findViewById(R.id.myimage);
        TextView name = (TextView)view.findViewById(R.id.TVname);
        TextView number = (TextView)view.findViewById(R.id.TVnumber);

        //Name ng contact
        String Strname = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        name.setText(Strname);

        //number ng contact
        String Strnumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        number.setText(Strnumber);



    }
    @Override

    //get data for layout row.xml
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row, parent, false);
        return view;
    }
}
