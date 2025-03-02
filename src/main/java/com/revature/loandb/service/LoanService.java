package com.revature.loandb.service;

import java.util.List;

import com.revature.loandb.dao.LoanDao;
import com.revature.loandb.model.Loan;

public class LoanService {
    private final LoanDao loanDao;

    public LoanService(LoanDao loanDao) {
        this.loanDao = loanDao;
    }

    public boolean createLoan(Loan loan) {
        return loanDao.createLoan(loan);
    }

    public List<Loan> getLoans() {
        return loanDao.getLoans();
    }

    public Loan getLoanById(int loanId) {
        return loanDao.getLoanById(loanId);
    }

    public boolean updateLoan(int loanId, Loan updatedLoan) {
        Loan existingLoan = loanDao.getLoanById(loanId);
        if (existingLoan == null) {
            return false; // Loan not found
        }
        updatedLoan.setId(loanId); // Ensure the ID remains the same
        return loanDao.updateLoan(updatedLoan);
    }
}
