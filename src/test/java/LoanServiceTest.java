/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Win 10
 */


 import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.revature.loandb.dao.LoanDao;
import com.revature.loandb.model.Loan;
import com.revature.loandb.service.LoanService;

public class LoanServiceTest {

    @Mock
    private LoanDao loanDao;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test if a loan can be created with invalid data
    // This test should fail
    @Test
    public void testCreateLoanWithInvalidData() {
        Loan invalidLoan = new Loan(0, -1000, "", "pending");

        when(loanDao.createLoan(invalidLoan)).thenReturn(false);

        boolean result = loanService.createLoan(invalidLoan);

        assertFalse(result, "Creating a loan with invalid data should return false");
    }

    // Test if a loan can be created with valid data
    // This test should pass
    @Test   
    public void testApproveLoanAsManager() {
        Loan loan = new Loan(1, 1000, "Personal", "pending");
        loan.setId(1);

        when(loanDao.getLoanById(1)).thenReturn(loan);
        when(loanDao.updateLoan(loan)).thenReturn(true);

        loan.setStatus("approved");
        boolean result = loanService.updateLoan(1, loan);

        assertTrue(result, "Manager should be able to approve the loan");
    }

    // This test should fail because a regular user should not be able to approve a loan
    
    @Test
    public void testApproveLoanAsRegularUser() {
        Loan loan = new Loan(1, 1000, "Personal", "pending");
        loan.setId(1);

        when(loanDao.getLoanById(1)).thenReturn(loan);
        when(loanDao.updateLoan(loan)).thenReturn(false);

        loan.setStatus("approved");
        boolean result = loanService.updateLoan(1, loan);

        assertFalse(result, "Regular user should not be able to approve the loan");
    }

    // This test should pass because a regular user should be able to retrieve their own loans

    @Test
    public void testRetrieveLoansForLoggedInUser() {
        Loan loan1 = new Loan(1, 1000, "Personal", "pending");
        Loan loan2 = new Loan(1, 2000, "Business", "approved");

        when(loanDao.getLoansByUserId(1)).thenReturn(List.of(loan1, loan2));

        List<Loan> loans = loanService.getLoansByUserId(1);

        assertEquals(2, loans.size(), "Logged-in user should be able to retrieve their own loans");
    }

    @Test
    public void testUpdateLoanForLoggedInUser() {
        Loan loan = new Loan(1, 1000, "Personal", "pending");
        loan.setId(1);

        when(loanDao.getLoanById(1)).thenReturn(loan);
        when(loanDao.updateLoan(loan)).thenReturn(true);

        loan.setAmount(1500);
        boolean result = loanService.updateLoan(1, loan);

        assertTrue(result, "Logged-in user should be able to update their own loan");
    }
}
