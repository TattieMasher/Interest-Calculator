package com.example.financialsuite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("SpellCheckingInspection")
public class activity_compound_results extends AppCompatActivity {
    private int pennies;
    private float intRate;
    private boolean intMonthly;     //  Interest compounded monthly by default
    private int duration;
    private int deposits;
    private String durationFormat;
    private static final DecimalFormat moneyFormat = new DecimalFormat("0.00");
    private DecimalFormat df = new DecimalFormat("0.00");

    private HashMap<Integer, Double> calcMap = new HashMap<>();
    private HashMap<Integer, Double> profitMap = new HashMap<>();

    private ArrayList<String> testArray;

    TextView etTesting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compound_result);

        Intent i = getIntent();
        pennies = i.getIntExtra("pennyBal", 0);
        deposits = i.getIntExtra("deposits", 0);
        intRate = i.getFloatExtra("interest", 0);
        durationFormat = i.getStringExtra("durGiven");

        duration = i.getIntExtra("timeDur", 0);


        if (deposits == 0) {
            calculateMonthly(pennies, duration, intRate);
        } else {
            calculateMonthly(pennies, duration, intRate, deposits);
        }

        populateResults();
        createHeading();

        double testy = round(100000);
        String testoid = df.format(testy);
        testoid = testoid;

        String t = df.format(round(0.002));
        String t1 = df.format(round(0.005));
        String t2 = df.format(round(0.105));
        String t3 = df.format(round(0.098));

        double blah = 0;
        // THIS WORKS ^^^^^^^^^^^^^
    }

    /**
     * The meat n' bones of this class! Contains the actual loop to create the HashMap containing the monthly interest values.
     *
     * @param prin   = principal (in pennies, to avoid any floating-point errors)
     * @param months = duration of calculation period in months
     * @param rate   = interest rate
     */
    private void calculateMonthly(int prin, int months, float rate) {
        double amount = prin;
        amount = amount / 100;
        calcMap.put(0, amount);

        for (int i = 1; i <= months; i++) {
            // calculate new amount on deposit for specified year (still in pennies)
            double interest = amount * rate / 100;
            amount = amount + interest;
            // round the penny value to the nearest one, then change it to pounds
            amount = round(amount);

            calcMap.put(i, amount);

            Double profit = amount - calcMap.get(i - 1);
            profitMap.put(i, round(profit));
        }
    }

    private void calculateMonthly(int prin, int months, float rate, int deposits) {
        double amount = prin;
        double begin = amount / 100;
        calcMap.put(0, begin);

        for (int i = 1; i <= months; i++) {
            // calculate new amount on deposit for specified year (still in pennies)
            double interest = amount * rate / 100;
            amount = amount + interest + deposits;

            double result = round(round(amount) / 100); //  Make new variable to format current result only and continue on in pennies with the calculation variable
            calcMap.put(i, result);

            Double profit = result - calcMap.get(i - 1);
            profitMap.put(i, round(profit));
        }
    }

    // START HERE

    private void createHeading() {
        //  Create object to reference the TextView
        TextView headingText = (TextView) findViewById(R.id.intro_textView);
        TextView totalText = (TextView) findViewById(R.id.total_amount);

        String result = "£";

        // Begin constructing the intro, gradually.
        String heading = "After ";

        if (durationFormat.equals("years")) {
            heading = heading.concat(Integer.toString(duration / 12) + " ");
        } else {
            heading = heading.concat(Integer.toString(duration));
        }
        heading = heading.concat(" " + durationFormat + ", you would have...");

        result = result.concat(Double.toString(calcMap.get(duration)));

        headingText.setText(heading);
        totalText.setText(result);

    }


    //  This gets the base xml for adding a single calculation step result and eventually displays it
    private void populateResults() {
        //  Create LinearLayout object to hold a reference to the parent of all the calculations
        LinearLayout container = (LinearLayout) findViewById(R.id.linear_layout_container);
        //  Create Inflater to allow programatically adding views
        LayoutInflater inflater = getLayoutInflater();
        //  Declare a View object to hold references to the view to inflate
        View myLayout;

        // Iterates all months calculated (including month 0)
        for (int key : calcMap.keySet()) {
            //  Reference the base xml to inflate, and inflates it to myLayout View object
            myLayout = inflater.inflate(R.layout.row_add_month, container, false);
            //  Declares a TextView object from a reference to an element on the to-be-inflated xml
            TextView textView1 = (TextView) myLayout.findViewById(R.id.entryL);

            TextView textView2 = (TextView) myLayout.findViewById(R.id.entryR);

            String result = formatWithCommas(calcMap.get(key).toString());
            result = df.format(calcMap.get(key));

            textView1.setText("Month " + key + ": " + "£" + result);
            textView2.setText("Profit since last calculation: £" + profitMap.get(key));

            container.addView(myLayout);
        }
    }


    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);      // Set two decimal places then round up from 50%
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);      // Done twice, to ensure that the final value always shows pennies after pounds

        return Double.parseDouble(moneyFormat.format(bd));
    }


    private int poundsToPennies(float gbp) {
        float f = Float.parseFloat(String.valueOf(gbp));

        return Math.round(f * 100);
    }


    private String formatWithCommas(String str) {
        int digits = str.length();
        String output = "";

        //  Decalare an array of characters to hold the string's digits individually
        char[] nums = new char[digits];

        reverseString(nums);

        //  Initialise the array to each digit
        for (int i = 0; i < str.length(); i++) {
            nums[i] = str.charAt(i);
        }

        output = commaFormatLoop(digits % 3, nums);

        if(output.charAt(output.length()-1) == ','){
            output = output.substring(0,output.length()-2);
        }

        return output;
    }

        //      FIX THIS SHIT

        private String commaFormatLoop(int preCount, char[] nums){
            boolean firstPass = true;
            int commaCounter = 3-preCount;
            int counter = 0;
            String output = "";

            for (char ch : nums) {
                if (commaCounter == 3) {
                    output = output.concat(",");
                    commaCounter = 0;
                }
                output = output.concat(String.valueOf(nums[counter]));

                counter++;
                commaCounter++;
            }
            
            if(output.charAt(0) == ','){
                output = output.substring(1);
            }
            if(output.charAt(output.length()-1) == ','){
                output = output.substring(0, output.length()-1);
            }

            return output;
        }

        private String commify(String str){

            return "";
        }


        private String reverseString(char[] nums){
            int digits = nums.length;
            int count = 0;
            char[] reversed = new char[nums.length];
            String output = "";

            for(int i = digits - 1; i > 0; i--){
                reversed[count] = nums[i];
                count++;
            }

            for(int i = 0; i < reversed.length; i++){
                output = output.concat(String.valueOf(reversed[i]));
            }

            return output;
        }


    }


