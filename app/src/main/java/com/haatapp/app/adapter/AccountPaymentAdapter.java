package com.haatapp.app.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.haatapp.app.R;
import com.haatapp.app.activities.AccountPaymentActivity;
import com.haatapp.app.activities.AddMoneyActivity;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.Card;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.haatapp.app.helper.GlobalData.cardArrayList;


/**
 * Created by santhosh@appoets.com on 30-08-2017.
 */

public class AccountPaymentAdapter extends BaseAdapter {

    private Context context_;
    private List<Card> list;
    private boolean isDeleteAvailable;

    public AccountPaymentAdapter(Context context, List<Card> list, boolean isDeleteAvailable) {
        this.context_ = context;
        this.list = list;
        this.isDeleteAvailable = isDeleteAvailable;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Card obj = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater mInflater = (LayoutInflater) context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.payment_method_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if (isDeleteAvailable) {
            holder.paymentLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.deleteTxt.setVisibility(View.VISIBLE);
        } else {
            holder.deleteTxt.setVisibility(View.GONE);
        }

        holder.paymentLabel.setChecked(obj.isChecked());
        holder.paymentLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDeleteAvailable && holder.paymentLabel.isChecked()) {
                    for (int i = 0; i < cardArrayList.size(); i++) {
                        if (cardArrayList.get(i).getCardId().equals(obj.getCardId()))
                            cardArrayList.get(i).setChecked(true);
                        else
                            cardArrayList.get(i).setChecked(false);
                    }

                    if(AccountPaymentActivity.accountPaymentAdapter!=null){
                        AccountPaymentActivity.proceedToPayBtn.setVisibility(View.VISIBLE);
                        AccountPaymentActivity.cashCheckBox.setChecked(false);
                        GlobalData.isCardChecked=true;
                        AccountPaymentActivity.accountPaymentAdapter.notifyDataSetChanged();
                    }

                    if(AddMoneyActivity.accountPaymentAdapter!=null)
                    AddMoneyActivity.accountPaymentAdapter.notifyDataSetChanged();
                }
            }
        });

        holder.deleteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context_);
                builder.setMessage("Are you sure you want to delete?")
                        .setPositiveButton(context_.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                AccountPaymentActivity.deleteCard(obj.getId());

                            }
                        })
                        .setNegativeButton(context_.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(ContextCompat.getColor(context_,R.color.theme));
                nbutton.setTypeface(nbutton.getTypeface(), Typeface.BOLD);
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(ContextCompat.getColor(context_,R.color.theme));
                pbutton.setTypeface(pbutton.getTypeface(), Typeface.BOLD);

            }
        });

        holder.paymentLabel.setText("XXXX-XXXX-XXXX" + obj.getLastFour());
//        setIcon(holder.icon, obj.icon_id);
        holder.icon.setImageResource(R.drawable.ic_credit_card);
        return convertView;
    }


    private void setIcon(ImageView imgView, Integer id) {
        switch (id) {
            case 0:
                imgView.setImageResource(R.drawable.ic_debit_card);
                break;
            case 1:
                imgView.setImageResource(R.drawable.ic_cash);
                break;
            default:
                imgView.setImageResource(R.drawable.ic_cash);
                break;
        }
    }

    static class ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.payment_label)
        RadioButton paymentLabel;
        @BindView(R.id.delete_txt)
        TextView deleteTxt;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
