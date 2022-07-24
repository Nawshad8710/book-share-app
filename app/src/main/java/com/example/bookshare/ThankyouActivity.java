package com.example.bookshare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bookshare.model.PaymentMethod;

public class ThankyouActivity extends AppCompatActivity {
    private Button btnViewPaymentInfo, btnConfirmPayment, btnBackHome;
    private TextView txtPaymentMethod, txtTotalAmount;

    private LinearLayout afterPaymentConfirmSection;

    private PaymentMethod pm = new PaymentMethod();
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);

        Intent i = getIntent();
        pm = (PaymentMethod) i.getSerializableExtra("paymentMethodObj");
        totalAmount = (Double) i.getDoubleExtra("totalAmount", 0.00);

        txtPaymentMethod = (TextView) findViewById(R.id.txtPaymentMethod);
        txtTotalAmount = (TextView) findViewById(R.id.txtTotalAmount);
        btnViewPaymentInfo = (Button) findViewById(R.id.btn_view_paymentInfo);
        btnConfirmPayment = (Button) findViewById(R.id.btnConfirmPayment);
        btnBackHome = (Button) findViewById(R.id.btnBackHome);
        afterPaymentConfirmSection = (LinearLayout) findViewById(R.id.after_payment_confirm);

        txtPaymentMethod.setText(pm.methodName+"");
        txtTotalAmount.setText(totalAmount+"");

        btnViewPaymentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentInfoDialog();
            }
        });

        btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentConfirmDialog();
            }
        });

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }

    private void showPaymentInfoDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.payment_info_img_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();

        ImageButton btnDialogClose = dialog.findViewById(R.id.btn_dialog_close);
        btnDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void showPaymentConfirmDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.payment_confirm_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setCancelable(true);
        dialog.show();

        ImageButton btnDialogClose = dialog.findViewById(R.id.btn_dialog_close);
        btnDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        final EditText etTxnID = (EditText) dialog.findViewById(R.id.etTxnID);

        Button btnConfirmTxnId = (Button) dialog.findViewById(R.id.btnConfirm);
        btnConfirmTxnId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etTxnID.getText().toString().length()>0){
                    afterPaymentConfirmSection.setVisibility(View.VISIBLE);
                    dialog.cancel();
                    btnConfirmPayment.setVisibility(View.GONE);
                }else{
                    etTxnID.setError("Enter TrxnId!");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}