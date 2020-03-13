package my.com.codeplay.loancalculator;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CalculatorActivityTest {
    @Rule
    public final ActivityTestRule<CalculatorActivity> calculatorActivity =
            new ActivityTestRule<>(CalculatorActivity.class, true);

    private EditText etLoanAmount, etDownPayment, etTerm, etAnnualInterestRate;
    private TextView tvMonthlyPayment, tvTotalRepayment, tvTotalInterest, tvAverageMonthlyInterest;

    @Before
    public void init() {
        etLoanAmount = calculatorActivity.getActivity().findViewById(R.id.loan_amount);
        etDownPayment = calculatorActivity.getActivity().findViewById(R.id.down_payment);
        etTerm = calculatorActivity.getActivity().findViewById(R.id.term);
        etAnnualInterestRate = calculatorActivity.getActivity().findViewById(R.id.annual_interest_rate);
        tvMonthlyPayment = calculatorActivity.getActivity().findViewById(R.id.monthly_repayment);
        tvTotalRepayment = calculatorActivity.getActivity().findViewById(R.id.total_repayment);
        tvTotalInterest = calculatorActivity.getActivity().findViewById(R.id.total_interest);
        tvAverageMonthlyInterest = calculatorActivity.getActivity().findViewById(R.id.average_monthly_interest);
    }

    @Test
    public void checkCalculation() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                etLoanAmount.setText("400000");
                etDownPayment.setText("100000");
                etTerm.setText("25");
                etAnnualInterestRate.setText("4.2");

                Button btnCalculate = calculatorActivity.getActivity().findViewById(R.id.btnCalculate);
                btnCalculate.performClick();
            }
        });

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        Assert.assertEquals("Incorrect Monthly Repayment", "1616.83", tvMonthlyPayment.getText());
        Assert.assertEquals("Incorrect Total Repayment", "485048.09", tvTotalRepayment.getText());
        Assert.assertEquals("Incorrect Total Interest", "185048.09", tvTotalInterest.getText());
        Assert.assertEquals("Incorrect Average Interest", "616.83", tvAverageMonthlyInterest.getText());
    }

    @Test
    public void checkReset() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Button btnReset = calculatorActivity.getActivity().findViewById(R.id.btnReset);
                btnReset.performClick();
            }
        });

        final String defaultResult = calculatorActivity.getActivity().getString(R.string.default_result);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        Assert.assertEquals("Field should be empty", "", etLoanAmount.getText().toString());
        Assert.assertEquals("Field should be empty", "", etDownPayment.getText().toString());
        Assert.assertEquals("Field should be empty", "", etTerm.getText().toString());
        Assert.assertEquals("Field should be empty", "", etAnnualInterestRate.getText().toString());
        Assert.assertEquals("Value should be default", defaultResult, tvMonthlyPayment.getText());
        Assert.assertEquals("Value should be default", defaultResult, tvMonthlyPayment.getText());
        Assert.assertEquals("Value should be default", defaultResult, tvMonthlyPayment.getText());
        Assert.assertEquals("Value should be default", defaultResult, tvMonthlyPayment.getText());
    }
}
