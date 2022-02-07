package com.example.financialsuite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {
    private boolean intMonthly = false;
    private float intRate = 0;
    private int duration = 0;
    private int pennies;
    private int penniesDeposited;
    private String durationFormat;

    // Declaring Java object fields for the interactive view elements in this activity
    EditText etBal, etRate, etDuration, etDeposits;
    Spinner mySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creates Spinner instance and associates it with spDur - the spinner on the actual GUI xml
        mySpinner = (Spinner) findViewById(R.id.spDur);

        // Creates an ArrayAdapter of Strings, to store the actual Strings to be displayed in the spinner.
        ArrayAdapter<String> myAdaptor = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.durChoices));
        // Parameters above are 1. an instance, 2. a resource file to use (formatting...?), 3. get the actual Strings to populate my spinner

        // Do a final thing to prepare the Spinner
        myAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Actually set the Spinner object's resource to the Strings list we've taken
        mySpinner.setAdapter(myAdaptor);
    }

    // Set the boolean to track how interest is compounded to true (meaning monthly interest)
    public void checkMonth(View view) {
        intMonthly = true;
    }

    // Do the opposite of above (yearly interest)
    public void checkYear(View view) {
        intMonthly = false;
    }



    public void calculate(View view) {
        // Create Java EditText objects to hold references to text inputs on activity
        etBal = (EditText) findViewById(R.id.etStartBal);
        etRate = (EditText) findViewById(R.id.etIntRate);
        etDuration = (EditText) findViewById(R.id.etDur);
        etDeposits = (EditText) findViewById(R.id.etMonthlyDeposit);
        durationFormat = mySpinner.getSelectedItem().toString().toLowerCase();

        // Get the text from the relevant UI textbox and parse it as an int, then assign to pennies
        pennies = validateEntry(etBal);

        //  Check if user has entered a monthly deposit amount
        penniesDeposited = validateEntry(etDeposits);

        // Get the interest rate and store it as a double
        intRate = validateEntry(etRate);

        // Store duration as int
        duration = Integer.parseInt(etDuration.getText().toString());

        ensureMonthlyValues();

        // Load new activity window now that the necessary parameters have been collected
        Intent i = new Intent(MainActivity.this,activity_compound_results.class);
        i.putExtra("pennyBal", pennies);
        i.putExtra("deposits", penniesDeposited);
        i.putExtra("interest", intRate);
        i.putExtra("timeDur", duration);
        i.putExtra("durGiven", durationFormat);
        // Start new activity with the above fields passed in as available data
        startActivity(i);
     }


     private void ensureMonthlyValues(){
        //  Make sure that we've got the total number of MONTHS, not years
        if (durationFormat.equals("Years")) {
            duration = duration * 12;
        }
        //  Make sure that we've got the MONTHLY interest rate, not yearly
        if (!intMonthly) {
            intRate = intRate / 12;
        }
     }


     private int validateEntry(EditText entry){
         if(entry.getText().toString().equals("")){
             return 0;   //  If they've not, set it to 0
         } else {
             return formatPennies(entry.getText()); //  If they have, store it in pennies as an int, like the initial balance
         }
     }



    /**
     *
     * @param aString - String, formatted in £/$ to be converted pennies/cents
     * @return The string multiplied by 100 as an int
     */
    private int formatPennies(String aString){
         float f = Float.parseFloat(aString);

         return Math.round(f*100);
     }

    /**
    *
    * @param notString - Editable, formatted in £/$ to be converted pennies/cents
    * @return The string multiplied by 100 as an int
    */
    private int formatPennies(Editable notString){
        float f = Float.parseFloat(String.valueOf(notString));

        return Math.round(f*100);
    }

}