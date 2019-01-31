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
import de.kontranik.freebudget.model.Transaction;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private LayoutInflater inflater;
    private int layout;
    private List<Transaction> transactions;

    public TransactionAdapter(Context context, int resource, List<Transaction> transactions) {
        super(context, resource, transactions);
        this.transactions = transactions;
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

        Transaction transaction = transactions.get(position);

        viewHolder.costView.setText(String.format(Locale.getDefault(), "%1$,.2f", transaction.getAmount_fact()));

        if (transaction.getAmount_fact() > 0) {
            viewHolder.costView.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorGreen));
         } else {
            viewHolder.costView.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorRed));
        }
        viewHolder.descView.setText(transaction.getDescription());

        viewHolder.categoryView.setText(transaction.toString());

        return convertView;
    }

    private class ViewHolder {
        final TextView costView, descView, categoryView;
        ViewHolder(View view) {
            costView = (TextView) view.findViewById(R.id.textView_amount);
            descView = (TextView) view.findViewById(R.id.textView_description);
            categoryView = (TextView) view.findViewById(R.id.textView_category);
        }
    }
}