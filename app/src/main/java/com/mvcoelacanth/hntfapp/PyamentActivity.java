package com.mvcoelacanth.hntfapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.Toolbar;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import java.text.ParseException;
import java.util.HashMap;

public class PyamentActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView buttonPay;
    private EditText editTextAmount;
    private String paymentAmount = "10";
    public static final int PAYPAL_REQUEST_CODE = 123;
    private LinearLayout linearLayout;


    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId("Adx85Bgaq8K2oAWO8yC_unqRHnmsQNJw61Ol3f-yFEbumJPrm6AMpyhSVT4yL0x52Bv8cvX6BATuK_8v");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payement_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        buttonPay =  findViewById(R.id.buttonPay);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        buttonPay.setOnClickListener(this);
        linearLayout = findViewById(R.id.linear1);

        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PAYPAL_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);
                        SharedPreferences sharedPref = PyamentActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("purchased", true);
                        editor.commit();

                        Toast.makeText(PyamentActivity.this, "Thank! You can download now.", Toast.LENGTH_LONG).show();
                        //Starting a new activity for the payment details and also putting the payment details with intent

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }


    @Override
    public void onClick(View v) {
        getPayment();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        hideK();

        super.onDestroy();
    }

    private void getPayment() {
        if (linearLayout.getVisibility() == View.VISIBLE) {
            if (editTextAmount.getText().toString().equals("")) {
                editTextAmount.setError("field can't be empty");
                return;
            }
            try {
                float ab = Float.valueOf(editTextAmount.getText().toString());
                if (ab < 10) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Sorry! $ " + ab + " is too low. Try 10 $?")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    editTextAmount.setText("10");
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                } else {
                    hideK();
                    paymentAmount = editTextAmount.getText().toString();
                }
            } catch (Exception par) {
                editTextAmount.setError("not a valid amount");
                return;
            }
        }
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "USD", "Donate (in US Dollars)",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);


        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);


        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);


        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();


        switch (view.getId()) {
            case R.id.d10:
                if (checked) {
                    paymentAmount = "10";
                    linearLayout.setVisibility(View.GONE);
                    hideK();
                }
                break;
            case R.id.d15:
                if (checked) {
                    paymentAmount = "15";
                    linearLayout.setVisibility(View.GONE);
                    hideK();
                }
                break;
            case R.id.d20:
                if (checked) {
                    paymentAmount = "20";
                    linearLayout.setVisibility(View.GONE);
                    hideK();
                }
                break;
            case R.id.d50:
                if (checked) {
                    paymentAmount = "50";
                    linearLayout.setVisibility(View.GONE);
                    hideK();
                }
                break;
            case R.id.d100:
                if (checked) {
                    paymentAmount = "100";
                    linearLayout.setVisibility(View.GONE);
                    hideK();
                }
                break;
            case R.id.dcus:
                if (checked) {
                    paymentAmount = "";
                    editTextAmount.setText("");
                    linearLayout.setVisibility(View.VISIBLE);
                    editTextAmount.requestFocus();
                    showK();
                }
                break;
        }
    }

    private void showK() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideK() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextAmount.getWindowToken(), 0);
    }

}
