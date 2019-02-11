package de.kontranik.freebudget.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.config.Config;
import de.kontranik.freebudget.fragment.OverviewFragment;
import de.kontranik.freebudget.model.Transaction;

import static de.kontranik.freebudget.config.Config.DATE_SHORT;

public class TransactionAdapter extends ArrayAdapter<Transaction>  {

    private LayoutInflater inflater;
    private int layout;
    private List<Transaction> transactions;
    private boolean markLastEdited;

    public TransactionAdapter(Context context, int resource, List<Transaction> transactions) {
        super(context, resource, transactions);
        this.transactions = transactions;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);

        SharedPreferences settings = context.getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE);
        markLastEdited  = settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false);
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

        String amount_fact = String.format(Locale.getDefault(), "%1$,.2f", transaction.getAmount_fact());
        String amount_planned = String.format(Locale.getDefault(), "%1$,.2f",transaction.getAmount_planned());

        viewHolder.textView_amount_planned.setText(amount_planned);
        viewHolder.textView_amount_fact.setText(amount_fact);

        if (transaction.getAmount_fact() > 0 ) {
            viewHolder.textView_amount_fact.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorGreen));
         } else if ( transaction.getAmount_fact() < 0) {
            viewHolder.textView_amount_fact.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorRed));
        }

        if ( transaction.getAmount_planned() > 0) {
            viewHolder.textView_amount_planned.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorGreen));
        } else if ( transaction.getAmount_planned() < 0) {
            viewHolder.textView_amount_planned.setTextColor(
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

        // last edited hervorheben
        if ( markLastEdited && transaction.getId() == OverviewFragment.lastEditedId ) {
            viewHolder.descriptionView.setTextColor( ContextCompat.getColor(parent.getContext(), R.color.colorBackgroundListItem) );
            viewHolder.textView_amount_planned.setTextColor( ContextCompat.getColor(parent.getContext(), R.color.colorBackgroundListItem) );
            viewHolder.textView_amount_fact.setTextColor( ContextCompat.getColor(parent.getContext(), R.color.colorBackgroundListItem) );
            viewHolder.categoryView.setTextColor( ContextCompat.getColor(parent.getContext(), R.color.colorBackgroundListItem) );
            viewHolder.linearLayout_Item.setBackgroundColor( ContextCompat.getColor(parent.getContext(), R.color.colorBackgroundAccent) );
        } else {
            viewHolder.descriptionView.setTextColor( ContextCompat.getColor(parent.getContext(), R.color.colorTextListItem) );
            viewHolder.categoryView.setTextColor( ContextCompat.getColor(parent.getContext(), R.color.colorTextListItem) );
            viewHolder.textView_amount_planned.setTextColor( ContextCompat.getColor(parent.getContext(), R.color.colorTextListItem) );
            viewHolder.textView_amount_fact.setTextColor( ContextCompat.getColor(parent.getContext(), R.color.colorTextListItem) );
            viewHolder.linearLayout_Item.setBackgroundColor( ContextCompat.getColor(parent.getContext(), R.color.colorBackgroundListItem ));
        }

        return convertView;
    }

    private class ViewHolder {
        final LinearLayout linearLayout_Item;
        final TextView textView_amount_planned, textView_amount_fact, descriptionView, categoryView;
        ViewHolder(View view) {
            linearLayout_Item = view.findViewById(R.id.linearLayout_Item);
            textView_amount_planned = view.findViewById(R.id.textView_amount_planned);
            textView_amount_fact = view.findViewById(R.id.textView_amount_fact);
            descriptionView = view.findViewById(R.id.textView_description);
            categoryView = view.findViewById(R.id.textView_category);
        }
    }
}