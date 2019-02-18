package de.kontranik.freebudget.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
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

    public void updateTransactionsList(List<RegularTransaction> newlist) {
        regularTransactions.clear();
        regularTransactions.addAll(newlist);
        this.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.list_view_item_regular_transaction_item, parent,false);
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

        String text = parent.getResources().getString(
                R.string.subTitleTransaction, String.valueOf(regularTransaction.getDay()), regularTransaction.getCategory());

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        if ( regularTransaction.getDate_start() > 0)
            text += ", start: " + df.format(regularTransaction.getDate_start());

        if ( regularTransaction.getDate_end() > 0)
            text += ", end: " + df.format(regularTransaction.getDate_end());

        viewHolder.categoryView.setText( text );

        return convertView;
    }

    private class ViewHolder {
        final TextView amountView, descriptionView, categoryView;
        ViewHolder(View view) {
            descriptionView = view.findViewById(R.id.textView_description_regular);
            amountView = view.findViewById(R.id.textView_amount_regular);
            categoryView = view.findViewById(R.id.textView_category_regular);
        }
    }
}