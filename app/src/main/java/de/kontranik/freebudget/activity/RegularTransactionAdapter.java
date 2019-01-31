package de.kontranik.freebudget.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.model.RegularTransaction;

public class RegularTransactionAdapter extends ArrayAdapter<RegularTransaction> {

    private LayoutInflater inflater;
    private int layout;
    private List<RegularTransaction> regularTransactions;

    public RegularTransactionAdapter(Context context, int resource, List<RegularTransaction> regularTransactions) {
        super(context, resource, regularTransactions);
        this.regularTransactions = regularTransactions;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.layout_regular_transaction_item, parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RegularTransaction regularTransaction = regularTransactions.get(position);

        viewHolder.descriptionView.setText(regularTransaction.getDescription());
        viewHolder.amountView.setText(String.format(Locale.getDefault(), "%1$,.2f", regularTransaction.getAmount()));

        if (regularTransaction.getAmount() > 0) {
            viewHolder.amountView.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorGreen));
         } else {
            viewHolder.amountView.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorRed));
        }
        viewHolder.categoryView.setText(regularTransaction.toString());

        return convertView;
    }

    private class ViewHolder {
        final TextView amountView, descriptionView, categoryView;
        ViewHolder(View view) {
            descriptionView = (TextView) view.findViewById(R.id.textView_description_regular);
            amountView = (TextView) view.findViewById(R.id.textView_amount_regular);
            categoryView = (TextView) view.findViewById(R.id.textView_category_regular);
        }
    }
}