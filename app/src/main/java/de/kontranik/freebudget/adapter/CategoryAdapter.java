package de.kontranik.freebudget.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.fragment.OverviewFragment;
import de.kontranik.freebudget.model.Category;

public class CategoryAdapter extends ArrayAdapter<Category>  {

    private LayoutInflater inflater;
    private int layout;
    private List<Category> categoryList;

    public CategoryAdapter(Context context, int resource, List<Category> categories) {
        super(context, resource, categories);
        this.categoryList = categories;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Category category = categoryList.get(position);

        viewHolder.textView_CategoryName.setText(category.getName());
        viewHolder.textView_CategoryWeight.setText(String.format("%.2f", category.getWeight()));
        viewHolder.textView_CategoryWeightBackground.setText("");

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) viewHolder.textView_CategoryWeightBackground.getLayoutParams();

        layoutParams.width =  (int) ( category.getWeight() * OverviewFragment.maxWidth / OverviewFragment.maxCategoryWeight );

        viewHolder.textView_CategoryWeightBackground.setLayoutParams(layoutParams);

        return convertView;
    }

    private class ViewHolder {
        final TextView textView_CategoryName, textView_CategoryWeight, textView_CategoryWeightBackground;
        ViewHolder(View view) {
            textView_CategoryName = view.findViewById(R.id.textView_CategoryName);
            textView_CategoryWeight = view.findViewById(R.id.textView_CategoryWeight);
            textView_CategoryWeightBackground = view.findViewById(R.id.textView_CategoryWeightBackground);
        }
    }
}