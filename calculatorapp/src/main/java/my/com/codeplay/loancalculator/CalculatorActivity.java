package my.com.codeplay.loancalculator;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

import my.com.codeplay.loancalculator.utils.LoanCalculationUtils;

public class CalculatorActivity extends Activity {
	private EditText etLoanAmount, etDownPayment, etTerm, etAnnualInterestRate;
	private TextView tvMonthlyPayment, tvTotalRepayment,
			tvTotalInterest, tvAverageMonthlyInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        etLoanAmount = findViewById(R.id.loan_amount);
        etDownPayment = findViewById(R.id.down_payment);
        etTerm = findViewById(R.id.term);
        etAnnualInterestRate = findViewById(R.id.annual_interest_rate);
        tvMonthlyPayment = findViewById(R.id.monthly_repayment);
        tvTotalRepayment = findViewById(R.id.total_repayment);
        tvTotalInterest = findViewById(R.id.total_interest);
        tvAverageMonthlyInterest = findViewById(R.id.average_monthly_interest);

        findViewById(R.id.btnCalculate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				calculate();
			}
		});

        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				reset();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }
    
    public void onClick(View v) {
    	switch (v.getId()) {
    	case R.id.btnCalculate:
    		if (validate()) {
    			calculate();
    		}
    		break;
    	case R.id.btnReset:
    		reset();
    		break;
    	}
    }
    
    private void reset() {
    	etLoanAmount.setText("");
		etLoanAmount.clearFocus();
		etDownPayment.setText("");
		etDownPayment.clearFocus();
		etTerm.setText("");
		etTerm.clearFocus();
		etAnnualInterestRate.setText("");
		etAnnualInterestRate.clearFocus();
		tvMonthlyPayment.setText(getString(R.string.default_result));
		tvTotalRepayment.setText(getString(R.string.default_result));
		tvTotalInterest.setText(getString(R.string.default_result));
		tvAverageMonthlyInterest.setText(getString(R.string.default_result));
    }
    
    private boolean validate() {
    	if (TextUtils.isEmpty(etLoanAmount.getText())) {
    		Toast.makeText(this, getString(R.string.empty_input, getString(R.string.loan_amount)), Toast.LENGTH_SHORT).show();
    		etLoanAmount.requestFocus();
    		return false;
    	} else if (TextUtils.isEmpty(etDownPayment.getText())) {
    		Toast.makeText(this, getString(R.string.empty_input, getString(R.string.down_payment)), Toast.LENGTH_SHORT).show();
    		etDownPayment.requestFocus();
    		return false;
    	} else if (TextUtils.isEmpty(etTerm.getText())) {
    		Toast.makeText(this, getString(R.string.empty_input, getString(R.string.term)), Toast.LENGTH_SHORT).show();
    		etTerm.requestFocus();
    		return false;
    	} else if (TextUtils.isEmpty(etAnnualInterestRate.getText())) {
    		Toast.makeText(this, getString(R.string.empty_input, getString(R.string.annual_interest_rate)), Toast.LENGTH_SHORT).show();
    		etAnnualInterestRate.requestFocus();
    		return false;
    	}
    	return true;
    }

	private void calculate() {
    	try {
			double loanAmount = Double.parseDouble(etLoanAmount.getText().toString())
					- Double.parseDouble(etDownPayment.getText().toString());
			double interest = Double.parseDouble(etAnnualInterestRate.getText().toString());
			double noOfMonth = Integer.parseInt(etTerm.getText().toString()) * 12;

			double monthlyRepayment = LoanCalculationUtils
					.getMonthlyRepayment(loanAmount, interest, noOfMonth);
			double totalRepayment = monthlyRepayment * noOfMonth;
			double totalInterest = totalRepayment - loanAmount;
			double monthlyInterest = totalInterest / noOfMonth;

			tvMonthlyPayment.setText(String.valueOf(
					new BigDecimal(monthlyRepayment).setScale(2, RoundingMode.HALF_UP)));
			tvTotalRepayment.setText(String.valueOf(new BigDecimal(totalRepayment).setScale(2, RoundingMode.HALF_UP)));
			tvTotalInterest.setText(String.valueOf(new BigDecimal(totalInterest).setScale(2, RoundingMode.HALF_UP)));
			tvAverageMonthlyInterest.setText(String.valueOf(new BigDecimal(monthlyInterest).setScale(2, RoundingMode.HALF_UP)));
		} catch(NumberFormatException e) {
			e.printStackTrace();
			
			Toast.makeText(this, R.string.err_calculation, Toast.LENGTH_LONG).show();
		}
    }

}