package de.kontranik.freebudget.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.List;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Category;

public class CategoryListActivity extends AppCompatActivity {

    final static String RESULT_CATEGORY = "RESULT_MONTH";

    private ListView categoryListView;
    private EditText categoryNameEditText;
    private Button btn_Save, btn_Close;

    private Category category;

    private DatabaseAdapter dbAdapter;

    private List<Category> categoryList;

    private ArrayAdapter<Category> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        categoryListView = findViewById(R.id.listView_categoryList);
        categoryNameEditText = findViewById(R.id.editText_categoryName);
        btn_Save = findViewById(R.id.btn_categorySave);
        btn_Close = findViewById(R.id.btn_categoryClose);

        dbAdapter = new DatabaseAdapter(this);

        getCategory();
        arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, categoryList);
        // устанавливаем для списка адаптер
        categoryListView.setAdapter(arrayAdapter);

        categoryListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                // по позиции получаем выбранный элемент
                String selectedItem = categoryList.get(position).getName();
                // установка текста элемента TextView
                categoryNameEditText.setText(selectedItem);
                dbAdapter.open();
                category = dbAdapter.getCategory(selectedItem);
                dbAdapter.close();
            }
        });
    }

    public void getCategory() {
        dbAdapter.open();
        categoryList = dbAdapter.getAllCategory();
        dbAdapter.close();
    }

    public void onSave(View view) {
        // TODO: save category
        dbAdapter.open();
        if ( category == null ) {
            category = new Category( 0, categoryNameEditText.getText().toString());
        } else {
            category.setName( categoryNameEditText.getText().toString());
        }

        if ( category.getId() > 0 )
            dbAdapter.update( category );
        else
            dbAdapter.insert( category );

        dbAdapter.close();

        getCategory();

        arrayAdapter.notifyDataSetChanged();
    }

    public void onClose(View view) {
        Intent returnIntent = new Intent();
        if ( category != null) {
            returnIntent.putExtra(RESULT_CATEGORY, category.getName());
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
