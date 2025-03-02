package com.revature.loandb.controller;

import com.revature.loandb.dto.LoanAuthRequestDTO;
import com.revature.loandb.model.Loan;
import com.revature.loandb.service.LoanService;

import io.javalin.http.Context;


public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService; 
    }

    public void createLoan(Context ctx) {
        LoanAuthRequestDTO req = ctx.bodyAsClass(LoanAuthRequestDTO.class);

        if (req.getUser_id() <= 0 || req.getAmount() <= 0 || req.getType() == null || req.getType().isEmpty()) {
            ctx.status(400).json("{\"error\":\"Invalid loan data\"}");
            return;
        }

        Loan newLoan = new Loan();
        newLoan.setUserId(req.getUser_id());
        newLoan.setAmount(req.getAmount());
        newLoan.setType(req.getType());
        newLoan.setStatus("pending");

        boolean success = loanService.createLoan(newLoan);

        if (success) {
            ctx.status(201).json("{\"message\":\"Loan created successfully\"}");
        } else {
            ctx.status(500).json("{\"error\":\"Failed to create loan\"}");
    }

}
}