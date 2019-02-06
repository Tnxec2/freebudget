package de.kontranik.freebudget.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.model.Transaction;

import static de.kontranik.freebudget.config.Config.DATE_SHORT;

public class TransactionAdapter extends ArrayAdapter<Transaction>  {

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

        String amount = String.format(Locale.getDefault(), "%1$,.2f", transaction.getAmount_fact());
        if ( transaction.getAmount_planned() != 0 ) amount += " (" + String.format(Locale.getDefault(), "%1$,.2f",transaction.getAmount_planned()) + ")";

        viewHolder.amountView.setText(amount);

        if (transaction.getAmount_fact() > 0 ) {
            viewHolder.amountView.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorGreen));
         } else if ( transaction.getAmount_fact() < 0) {
            viewHolder.amountView.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorRed));
        } else if ( transaction.getAmount_planned() > 0) {
            viewHolder.amountView.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorGreen));
        } else if ( transaction.getAmount_planned() < 0) {
            viewHolder.amountView.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorRed));
        }

        viewHolder.descriptionView.setText(transaction.getDescription());

        DateFormat df = new SimpleDateFormat(DATE_SHORT, Locale.getDefault());
        String dateString = "not set";
        if ( transaction.getDate() > 0 ) {
            dateString = df.format(transaction.getDate());
        }

        viewHolder.categoryView.setText(
                parent.getResources().getString(
                        R.string.subTitleTransaction, dateString, transaction.getCategory()) );

        return convertView;
    }

    private class ViewHolder {
        final TextView amountView, descriptionView, categoryView;
        ViewHolder(View view) {
            amountView = view.findViewById(R.id.textView_amount);
            descriptionView = view.findViewById(R.id.textView_description);
            categoryView = view.findViewById(R.id.textView_category);
        }
    }
}