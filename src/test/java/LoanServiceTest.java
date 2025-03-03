/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Victor Elias
 */


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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


    // Test if a loan can be created with valid data. This test should pass
    @Test
    public void testCreateLoanWithValidData() {
        Loan validLoan = new Loan(1, 1000, "Personal", "pending");

        when(loanDao.createLoan(validLoan)).thenReturn(true);

        boolean result = loanService.createLoan(validLoan);

        assertTrue(result, "Creating a loan with valid data should succeed");
    }
    // Test if a loan can be created with invalid data. This test should fail
    @Test
    public void testCreateLoanWithInvalidData() {
        Loan invalidLoan = new Loan(0, -1000, "", "pending");

        when(loanDao.createLoan(invalidLoan)).thenReturn(false);

        boolean result = loanService.createLoan(invalidLoan);

        assertFalse(result, "Creating a loan with invalid data should return false");
    }

    // Test if a loan can be get by ID. This test should pass
    @Test
    public void testRetrieveLoanById() {
        Loan loan = new Loan(1, 1000, "Personal", "pending");
        loan.setId(1);

        when(loanDao.getLoanById(1)).thenReturn(loan);

        Loan result = loanService.getLoanById(1);

        assertNotNull(result, "Retrieving a loan by ID should succeed");
        assertEquals(loan, result, "The retrieved loan should match the expected loan");
    }

    // Test if a loan can be get by invalid ID. This test should fail
    @Test
    public void testRetrieveLoanByInvalidId() {
        when(loanDao.getLoanById(999)).thenReturn(null);

        Loan result = loanService.getLoanById(999);

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

    // Test if a loan can be updated with valid data. This test should pass
    @Test
    public void testUpdateLoanWithValidData() {
        Loan loan = new Loan(1, 1000, "Personal", "pending");
        loan.setId(1);

        when(loanDao.getLoanById(1)).thenReturn(loan);
        when(loanDao.updateLoan(loan)).thenReturn(true);

        loan.setAmount(1500);
        boolean result = loanService.updateLoan(1, loan);

        assertTrue(result, "Updating a loan with valid data should succeed");
    }

    // Test if a loan can be rejected by a regular user. This test should fail
    @Test
    public void testUpdateLoanWithInvalidData() {
        Loan loan = new Loan(1, 1000, "Personal", "pending");
        loan.setId(1);

        when(loanDao.getLoanById(1)).thenReturn(loan);
        when(loanDao.updateLoan(loan)).thenReturn(false);

        loan.setAmount(-1500); // Invalid amount
        boolean result = loanService.updateLoan(1, loan);

        assertFalse(result, "Updating a loan with invalid data should fail");
    }

    // Test if loans can be retrieved for a logged-in user. This test should pass
    @Test
    public void testRetrieveLoansForLoggedInUser() {
        Loan loan1 = new Loan(1, 1000, "Personal", "pending");
        Loan loan2 = new Loan(1, 2000, "Business", "approved");

        when(loanDao.getLoansByUserId(1)).thenReturn(List.of(loan1, loan2));

        List<Loan> loans = loanService.getLoansByUserId(1);

        assertEquals(2, loans.size(), "Logged-in user should be able to retrieve their own loans");
    }
    // Test if a loan can be updated by a regular user. This test should pass
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

    //  Test if a loan can be  approved by a manager. This test should pass
    @Test
    public void testApproveLoanNotPending() {
        Loan loan = new Loan(1, 1000, "Personal", "approved");
        loan.setId(1);

        when(loanDao.getLoanById(1)).thenReturn(loan);

        loan.setStatus("approved");
        boolean result = loanService.statusLoan(1, loan);

        assertFalse(result, "Approving a loan that is not in a pending state should fail");
    }

    // Test if a loan can be rejected by a manager. This test should pass
    @Test
    public void testRejectLoanNotPending() {
        Loan loan = new Loan(1, 1000, "Personal", "approved");
        loan.setId(1);

        when(loanDao.getLoanById(1)).thenReturn(loan);

        loan.setStatus("rejected");
        boolean result = loanService.statusLoan(1, loan);

        assertFalse(result, "Rejecting a loan that is not in a pending state should fail");
    }




}

