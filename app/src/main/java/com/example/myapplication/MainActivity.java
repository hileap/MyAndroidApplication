package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private LocalDB localDB;
    private ArrayList<String[]> dataList;
    private ScrollView dbRecordsHolder;
    private LinearLayout dbRecords;
    private Drawable drawableBorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        drawableBorder = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.rgb(240, 240, 240));
                canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(5);
                canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };

        localDB = new LocalDB();
        localDB.setCurrentTable(Table.Persons);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        GridLayout headerLayout = new GridLayout(this);
        headerLayout.setColumnCount(3);
        headerLayout.setBackgroundColor(Color.LTGRAY);

        TextView headerText = new TextView(this);
        headerText.setText("SQLite DB");
        headerText.setTextSize(20);
        headerText.setPadding(15, 0, 0, 0);

        Button headerAddValueButton = new Button(this);
        headerAddValueButton.setText("Добавить");
        headerAddValueButton.setTextSize(20);
        headerAddValueButton.setOnClickListener(new AddListener());

        GridLayout.LayoutParams headerTextParams = new GridLayout.LayoutParams();
        headerTextParams.columnSpec = GridLayout.spec(0, 2);
        GridLayout.LayoutParams headerAddValueButtonParams = new GridLayout.LayoutParams();
        headerAddValueButtonParams.columnSpec = GridLayout.spec(2, 1);
        headerAddValueButtonParams.setGravity(Gravity.RIGHT);
        headerLayout.addView(headerText, headerTextParams);
        headerLayout.addView(headerAddValueButton, headerAddValueButtonParams);

        linearLayout.setWeightSum(14);
        linearLayout.addView(headerLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1));

        dbRecordsHolder = new ScrollView(this);
        dbRecords = new LinearLayout(this);
        dbRecords.setOrientation(LinearLayout.VERTICAL);
        dbRecordsHolder.addView(dbRecords);

        LinkedList<String[]> items = localDB.getValuesFromCurrentDb();
        for (String[] item:
             items)
            addItemToRecordsList(item);

        linearLayout.addView(dbRecordsHolder, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 13));

        setContentView(linearLayout);
        //setContentView(R.layout.activity_main);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void addItemToRecordsList(String[] items)
    {
        LinearLayout itemLayout = new LinearLayout(instance);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setWeightSum(items.length);
        for (int i = 0; i < items.length; i++)
        {
            TextView viewItem = new TextView(instance);
            viewItem.setText(items[i].toString());
            viewItem.setTextSize(15);
            itemLayout.addView(viewItem, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        }
        itemLayout.setBackground(drawableBorder);
        dbRecords.addView(itemLayout);
    }
    private class AddListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Dialog addRecordDialog = new Dialog(instance);

            GridLayout layout = new GridLayout(instance);
            layout.setOrientation(GridLayout.VERTICAL);
            layout.setRowCount(15);

            TextView nameView = new TextView(instance);
            nameView.setText("Имя");
            GridLayout.LayoutParams nameViewParams = new GridLayout.LayoutParams();
            nameViewParams.rowSpec = GridLayout.spec(0, 2);
            nameViewParams.setGravity(Gravity.LEFT);
            EditText nameField = new EditText(instance);
            GridLayout.LayoutParams nameFieldParams = new GridLayout.LayoutParams();
            nameFieldParams.rowSpec = GridLayout.spec(2, 2);
            nameFieldParams.setGravity(Gravity.FILL_HORIZONTAL);
            layout.addView(nameView, nameViewParams);
            layout.addView(nameField, nameFieldParams);

            TextView ageView = new TextView(instance);
            ageView.setText("Возраст");
            GridLayout.LayoutParams ageViewParams = new GridLayout.LayoutParams();
            ageViewParams.rowSpec = GridLayout.spec(5, 2);
            ageViewParams.setGravity(Gravity.LEFT);
            EditText ageField = new EditText(instance);
            GridLayout.LayoutParams ageFieldParams = new GridLayout.LayoutParams();
            ageFieldParams.rowSpec = GridLayout.spec(7, 2);
            ageFieldParams.setGravity(Gravity.FILL_HORIZONTAL);
            layout.addView(ageView, ageViewParams);
            layout.addView(ageField, ageFieldParams);

            Button addButton = new Button(instance);
            addButton.setText("Добавить");
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        int value = Integer.parseInt(ageField.getText().toString());
                        if (value < 0 || value > 150)
                            throw new Exception();
                        String[] values = new String[localDB.getCurrentTable().getColumnsCount()];
                        //for (int i = 0; i < localDB.getCurrentDb().getColumnsCount(); i++)
                        //values
                        values[0] = nameField.getText().toString();
                        values[1] = ageField.getText().toString();
                        if (localDB.insertIntoCurrentTable(values))
                            addItemToRecordsList(values);
                        addRecordDialog.hide();
                    }
                    catch (Exception exc)
                    {
                        Toast.makeText(instance, "Некорректное значение в поле 'Возраст'", Toast.LENGTH_LONG).show();
                    }
                }
            });
            GridLayout.LayoutParams addButtonParams = new GridLayout.LayoutParams();
            addButtonParams.rowSpec = GridLayout.spec(10, 4);
            layout.addView(addButton, addButtonParams);

            addRecordDialog.setContentView(layout);

            addRecordDialog.show();
        }
    }
}