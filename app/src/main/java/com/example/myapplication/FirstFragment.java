package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.FragmentFirstBinding;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
 //   private SharedViewModel sharedViewModel;
    private LinearLayout linearLayout;

    private Stack<Integer> stack1=new Stack<>();//存按钮id
    private Stack<String> stack2=new Stack<>();//存文件名
    private Stack<String> stack3=new Stack<>();//存标题

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        Context context=getActivity();
        database_helper dbhelper;
        SQLiteDatabase db;
        dbhelper=new database_helper(context,"record.db",null,1);
        db = dbhelper.getReadableDatabase();
        String[] projection = {"note_name","note_id","note_time"};
        Cursor cursor = db.query(
                "note",
                projection,
                null,
                null,
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            linearLayout=binding.sonlinearlayout;
            // 循环遍历Cursor中的所有记录
            do {

                String name = cursor.getString(cursor.getColumnIndexOrThrow("note_name"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("note_time"));
                int button_id=cursor.getInt(cursor.getColumnIndexOrThrow("note_id"));
                //这里将button_id读取为"note_id"，是因为一个代码缺陷
                //前面我使用int botton_id = View.generateViewId();来生成数据库的button_id
                //但是生成的时候没有赋给任何一个布局，这导致会生成重复的button_id
                //这里暂时用note_id替代
                //要更改，可将button_id的生成转到这里，每产生一个就赋值给下面的botton,再添加到数据库
                Button notebutton = new Button(context);
                notebutton.setId(button_id);

                stack1.push(button_id);//为后面循环监听按钮存储id
                stack2.push(name+time);//存储文件名，便于读取
                stack3.push(name);

                if(name.length()>17){
                    //因为在没有给日记命名的情况下，会使用时间命名，正好17个字符
                    //限制在17只是避免初始标题呈现的过长
                    String name1=name.substring(0,17)+"...";
                    notebutton.setText(name1);
                }
                else
                    notebutton.setText(name);

                notebutton.setTextSize(20);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // 宽度
                        LinearLayout.LayoutParams.WRAP_CONTENT  // 高度
                );
                notebutton.setLayoutParams(params);
                linearLayout.addView(notebutton);


            } while (cursor.moveToNext()); // 移动到下一条记录

            // 关闭Cursor
            cursor.close();
            db.close();
        }


        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      //  sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        //下面这段注释掉的，是生成项目时自动产生的一个按钮，作用是从fragment_first转入fragment_secong
        //本来想留着做个其他功能，但最后没有弄，只是简单的注释掉按钮

        /*  binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );
      */

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAnchorView(R.id.fab)//设置位置
                //        .setAction("Action", null).show();//设置点击后的效果为null

                NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.write_note);

            }
        });


        while (!stack1.isEmpty()){
            int k=stack1.peek();
            String n=stack2.peek();
            String t=stack3.peek();
            Button b=view.findViewById(k);
            Bundle args = new Bundle();
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content=load(n);
                    NavController navController = Navigation.findNavController(view);

                    args.putString("content", content);
                    args.putString("topic",t);
                    args.putString("filename",n);

                    navController.navigate(R.id.write_note, args);
                    //NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.write_note);
                }
            });

            stack1.pop();
            stack2.pop();
            stack3.pop();
        }



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }


    public String load(String fileName){
        String result="读取失败,文件不存在";
        FileInputStream fis = null;
        Context context = getActivity();
        try {
            fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }

            result = stringBuilder.toString();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        return result;
    }


}