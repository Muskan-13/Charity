package com.example.donate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class UserDetails extends AppCompatActivity implements PaymentResultListener {

    private EditText name,phone,email,amount;
    private Button paymentbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        //Reference to the layout components
        name= findViewById(R.id.name);
        email= findViewById(R.id.email);
        phone= findViewById(R.id.phone);
        amount= findViewById(R.id.amount);
        paymentbtn= findViewById(R.id.payment);

        //When Pay Now button is clicked this function is called
        paymentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Getting the user inputs
                String person_name= name.getText().toString();
                String person_email = email.getText().toString();
                String person_no = phone.getText().toString();
                String amt= String.valueOf(Integer.parseInt(amount.getText().toString())*100);

                //Checks if the input fields are empty
                if(person_name.equals("") || person_email.equals("") || person_no.equals("") || amt.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Fill out all the details",Toast.LENGTH_SHORT).show();
                }
                else { // otherwise invokes razorpay api
                    Checkout.preload(getApplicationContext());
                    startPayment(person_name, person_email, person_no, amt);
                }
            }
        });
    }

    //Function that invokes Razorpay Api
    public void startPayment(String p_name, String p_email, String p_phn,String amt ) {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_Aay4CrOmTNlhxq"); //Owner's Razorpay API Key
        checkout.setImage(R.drawable.images);
        final Activity activity = this;
        /**   * Pass your payment options to the Razorpay Checkout as a JSONObject   */
        try {
            JSONObject options = new JSONObject();
            options.put("name", p_name); //Name of the doner
            options.put("description", "Donation");
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png" );
            options.put("theme.color", "#9A1D47");
            options.put("currency", "INR");
            options.put("amount", amt); //Amount donated
            options.put("prefill.email", p_email); //Email of the donor
            options.put("prefill.contact",p_phn); // Contact no. of the donor
            checkout.open(activity, options);
        } catch(Exception e)
        {
            Log.e("MainActivity", "Error in starting Razorpay Checkout", e);	}
        Checkout.clearUserData(getApplicationContext()); //Clears the data on sdk
    }


    //When the payment is successful this function is called
    @Override
    public void onPaymentSuccess(String s) {

        String amt= amount.getText().toString(); //amount donated by the user
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(UserDetails.this); // Creates a dialog box

        builder.setMessage("Rs."+ amt +" was Successfully donated"); //This message is displayed on the dialog box

        builder.setTitle("Payment Successful !"); // Sets the title of the dialog box

        builder.setCancelable(false);

        builder.setPositiveButton(
                        "OK",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                finish(); //comes out of the current screen when OK button is pressed
                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //This function is called when payment is unsuccessful
    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this,"An error occured",Toast.LENGTH_SHORT).show();
    }

}