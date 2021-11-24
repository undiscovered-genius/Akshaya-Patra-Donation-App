package com.example.razorpay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {

    private Button pay;
    private TextView result, name, email, phone, amount;
    private ImageView copy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pay = findViewById(R.id.button);
        result = findViewById(R.id.textView);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        amount = findViewById(R.id.amount);
        copy = findViewById(R.id.copy);


        Checkout.preload(getApplicationContext());

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int payAmount = 0;
                final String merName = String.valueOf(name.getText());
                final String urPhone = String.valueOf(phone.getText());
                final String urEmail = String.valueOf(email.getText());
                if (String.valueOf(amount.getText()).equals("")){
                    payAmount = 0;
                }else{
                    payAmount = Integer.parseInt(String.valueOf(amount.getText()));
                }

                if (merName.isEmpty()){
                    name.setError("Enter Name");
                }else if (urPhone.isEmpty() || urPhone.length() != 10){
                    phone.setError("Enter 10 digit Number");
                }else if (urEmail.isEmpty()){
                    email.setError("Enter Email");
                }else if (payAmount == 0){
                    amount.setError("Amount should be > 0");
                }else{
                    payAmount = payAmount * 100;
                    final String pay = String.valueOf(payAmount);

                    Checkout checkout = new Checkout();
                    checkout.setKeyID("rzp_test_ngHXz5sYhFdHAH");
                    /**
                     * Instantiate Checkout
                     */

                    /**
                     * Set your logo here
                     */
                    checkout.setImage(R.drawable.icon);

                    /**
                     * Reference to current activity
                     */
                    final Activity activity = MainActivity.this;

                    /**
                     * Pass your payment options to the Razorpay Checkout as a JSONObject
                     */
                    try {
                        JSONObject options = new JSONObject();

                        options.put("name", merName);
                        options.put("description", "Reference No. #654321");
                        options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
                        options.put("theme.color", "#3399cc");
                        options.put("currency", "INR");
                        options.put("amount", pay);//pass amount in currency subunits
                        options.put("prefill.email", urEmail);
                        options.put("prefill.contact",urPhone);
                        JSONObject retryObj = new JSONObject();
                        retryObj.put("enabled", true);
                        retryObj.put("max_count", 4);
                        options.put("retry", retryObj);

                        checkout.open(activity, options);

                    } catch(Exception e) {
                        Log.e("TAG", "Error in starting Razorpay Checkout", e);
                    }
                }
            }
        });

        Context context = this;
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = (ClipData) ClipData.newPlainText("text",result.getText());
                clipboardManager.setPrimaryClip(data);
                Toast.makeText(MainActivity.this, "Transaction ID copied!",Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onPaymentSuccess(String s) {
        result.setText("Transaction ID : "+s);
        Toast.makeText(MainActivity.this, "Payment DONE Successfully!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(MainActivity.this, "ERROR : "+s,Toast.LENGTH_SHORT).show();
    }
}