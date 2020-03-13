package my.com.codeplay.loancalculator.utils;

public class LoanCalculationUtils {

    public static double getMonthlyRepayment(
            double loanAmount, double interestRate, double numberOfMonth) {
        if (numberOfMonth <= 0 || interestRate <= 0) return 0;

        interestRate = interestRate / 12 / 100;
        return loanAmount * (interestRate + (interestRate /
                (java.lang.Math.pow(++interestRate, numberOfMonth) - 1)));
    }

}
