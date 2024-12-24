package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.WriteNotesFragmentBinding;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class write_notes_Fragment extends Fragment  {
    private WriteNotesFragmentBinding binding;
    private EditText editText1,editText2;
 //   private SharedViewModel sharedViewModel;
    private database_helper dbhelper;


    private String filename=null;
    private boolean flag=true;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
  //      sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);


        binding = WriteNotesFragmentBinding.inflate(inflater, container, false);
        editText1=binding.topic;
        editText2=binding.editText;

        Bundle args = getArguments();
        String text1 = args.getString(("topic"));
        String text2 = args.getString("content");
        filename=args.getString("filename");
        if (!filename.isEmpty()) {


            editText1.setText(text1);
            editText2.setText(text2);

            flag=false;
            args.putString("topic","");
            args.putString("content","");
            args.putString("filename","");
        }
/*
    下面这一部分本来是打算用SharedViewModel来传递数据，后来废弃了

*/
        /*sharedViewModel.gettopic().observe(getViewLifecycleOwner(), topic -> {
            if (topic != null) {
                editText1.setText(topic);
            }
        });

        sharedViewModel.getEditTextContent().observe(getViewLifecycleOwner(), content -> {
            if (content != null) {
                editText2.setText(content);
            }
        });*/
        return binding.getRoot();

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.saveContent.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //        .setAnchorView(R.id.fab)//设置位置
                        //        .setAction("Action", null).show();//设置点击后的效果为null
                        //        NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.write_note);
                        editText1=binding.topic;
                        editText2=binding.editText;
                        String topic=editText1.getText().toString();
                        String result=editText2.getText().toString();
                        if(!result.isEmpty())
                            save(topic,result);
                    }
                }

        );



    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        binding = null;
    }

    public void save(String t,String k){
        FileOutputStream out=null;
        BufferedWriter writer=null;
        Context context = getActivity();
        try{
            Calendar calendar = Calendar.getInstance();
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH)) + 1; // 月份从0开始，所以需要+1
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)); // 24小时制
            String minute = String.valueOf(calendar.get(Calendar.MINUTE));
            String second=String.valueOf(calendar.get(Calendar.SECOND));
            String time=year+"."+month+"."+day+"."+hour+"."+minute+"."+second;
            if(filename.isEmpty()) {
                if (t.isEmpty()) {
                    out = context.openFileOutput("no_title"+time, context.MODE_PRIVATE);
                } else {
                    out = context.openFileOutput(t + time, context.MODE_PRIVATE);
                }
            }
            else {
                out = context.openFileOutput(filename, context.MODE_PRIVATE);
            }
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(k);
            if(flag==true){

                dbhelper=new database_helper(context,"record.db",null,1);
                dbhelper.getWritableDatabase();
                SQLiteDatabase db=dbhelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                if(!t.isEmpty())
                    values.put("note_name",t);
                else
                    values.put("note_name","no_title");
                values.put("note_time",time);
                int botton_id = View.generateViewId();
                values.put("button_id",botton_id);
                db.insert("note",null,values);
                db.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null)
                    writer.close();
            }catch (IOException e){
                e.printStackTrace();

            }
        }


    }


}

