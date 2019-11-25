package my.com.codeplay.loancalculator;

import org.junit.Assert;
import org.junit.Test;

import my.com.codeplay.loancalculator.utils.LoanCalculationUtils;


public class LoanCalculationUtilsTest {
    @Test
    public void getMonthlyRepayment_ValidCalculation() {
        final double result = LoanCalculationUtils.getMonthlyRepayment(
                400000-100000, 4.2, 25*12);

        Assert.assertEquals(1616.17, result, 1.0);
    }

    @Test
    public void getMonthlyRepayment_ValidCalculation_ZeroMonth() {
        final double result = LoanCalculationUtils.getMonthlyRepayment(
                400000-100000, 4.2, 0*12);

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void getMonthlyRepayment_ValidCalculation_ZeroInterest() {
        final double result = LoanCalculationUtils.getMonthlyRepayment(
                400000-100000, 0, 25*12);

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void getMonthlyRepayment_ValidCalculation_ZeroLoanAmount() {
        final double result = LoanCalculationUtils.getMonthlyRepayment(
                0, 4.2, 25*12);

        Assert.assertEquals(0.0, result, 0);
    }
}
