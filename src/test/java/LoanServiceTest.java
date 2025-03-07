/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Victor Elias
 */

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    // Test if a loan can be created with invalid data. This test should fail
    @Test
    public void testCreateLoanWithInvalidData() {
        Loan invalidLoan = new Loan(0, -1000, "", "pending");

        when(loanDao.createLoan(invalidLoan)).thenReturn(false);

        boolean result = loanService.createLoan(invalidLoan);

        assertFalse(result, "Creating a loan with invalid data should return false");
    }

    // Test if a loan can be get by invalid ID. This test should fail
    @Test
    public void testRetrieveLoanByInvalidId() {
        int invalidId = 50;
        when(loanDao.getLoanById(invalidId)).thenReturn(null);

        Loan result = loanService.getLoanById(invalidId);

        assertNull(result, "Retrieving a loan by an invalid ID should fail");
    }

    // Test if a loan can be approved by a manager. This test should pass
    @Test
    public void testApproveLoanAsManager() {
        Loan loan = new Loan(1, 1000, "Personal", "pending");
        loan.setId(1);

        when(loanDao.getLoanById(1)).thenReturn(loan);
        when(loanDao.updateLoan(loan)).thenReturn(true);

        loan.setStatus("approved");
        boolean result = loanService.updateLoan(1, loan);

        assertTrue(result, "Manager should be able to approve the loan");
    }// Test if a loan can be approved by a regular user. This test should fail

    // Test if a loan can be approved by a regular user. This test should fail
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

    // Test if a loan can be approved by a manager. This test should pass
    @Test
    public void testApproveLoanNotPending() {
        Loan loan = new Loan(1, 1000, "Personal", "approved");
        loan.setId(1);

        when(loanDao.getLoanById(1)).thenReturn(loan);

        loan.setStatus("approved");
        boolean result = loanService.statusLoan(1, loan);

        assertFalse(result, "Approving a loan that is not in a pending state should fail");
    }

}
