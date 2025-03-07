package com.revature.loandb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.loandb.dto.LoanAuthRequestDTO;
import com.revature.loandb.model.Loan;
import com.revature.loandb.service.LoanService;

import io.javalin.http.Context;

public class LoanController {

    private final LoanService loanService;
    Logger logger = LoggerFactory.getLogger(LoanController.class);

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    public void createLoan(Context ctx) {

        try {
            // Retrieve the loan data from the request body
            LoanAuthRequestDTO req = ctx.bodyAsClass(LoanAuthRequestDTO.class);

            int userId = ctx.sessionAttribute("userId");
            req.setUserId(userId);

            if (req.getUserId() <= 0 || req.getAmount() <= 0 || req.getType() == null || req.getType().isEmpty()) {
                ctx.status(400).json("{\"error\":\"Invalid loan data\"}");
                logger.warn("Invalid Loan data");
                return;
            }

            Loan newLoan = new Loan(req.getUserId(), req.getAmount(), req.getType(), "pending");

            boolean success = loanService.createLoan(newLoan);

            if (success) {
                ctx.status(201).json("{\"message\":\"Loan created successfully\"}");
                logger.info("Loan created successfully");
            } else {
                ctx.status(500).json("{\"error\":\"Failed to create loan\"}");
                logger.warn("Failed to create loan");
            }
        } catch (Exception e) {
            ctx.status(400).json("{\"error\":\"Invalid loan format\"}");
            logger.error("Invalid loan format");
        }

    }

    public void updateLoan(Context ctx) {
        int loanId;
        try {
            loanId = Integer.parseInt(ctx.pathParam("loanId"));
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"error\":\"Invalid loan ID format\"}");
            logger.error("Invalid Loan ID forman");
            return;
        }

        try {
            LoanAuthRequestDTO req = ctx.bodyAsClass(LoanAuthRequestDTO.class);

            // Check if the status value is provided in the request
            if (req.getStatus() != null && !req.getStatus().isEmpty()) {
                ctx.status(400).json("{\"error\":\"Status cannot be modified\"}");
                logger.warn("Status cannot be modified");
                return;
            }

            // Check if the loan exists
            String role = ctx.sessionAttribute("role");
            Integer userId = ctx.sessionAttribute("userId");

            Loan existingLoan = loanService.getLoanById(loanId);
            if (existingLoan == null) {
                ctx.status(404).json("{\"error\":\"Loan not found\"}");
                logger.warn("Loan not found");
                return;
            }

            // Allow update if the user is the loan owner or a manager
            if (role == null || (!role.toLowerCase().contains("manager") && existingLoan.getUserId() != userId)) {
                ctx.status(403).json(
                        "{\"error\":\"Operation not permitted. Only the loan owner or a manager can update the loan.\"}");
                logger.warn("Operation not permitted. Only the loan owner or a manager can update the loan.");
                return;
            }

            existingLoan.setAmount(req.getAmount());
            existingLoan.setType(req.getType());
            // Do not update the status value
            // existingLoan.setStatus(req.getStatus());

            boolean success = loanService.updateLoan(loanId, existingLoan);

            if (success) {
                ctx.status(200).json("{\"message\":\"Loan updated successfully\"}");
                logger.info("Loan updated successfully");
            } else {
                ctx.status(500).json("{\"error\":\"Failed to update loan\"}");
                logger.warn("Failed to update loan");
            }
        } catch (Exception e) {
            ctx.status(400).json("{\"error\":\"Invalid loan format\"}");
            logger.error("Invalid loan format");
        }

    }

    public void getLoans(Context ctx) {
        // Allow users to view their own loans or managers to view all loans
        String role = ctx.sessionAttribute("role");
        Integer userId = ctx.sessionAttribute("userId");

        // If the user is not logged in, return an error
        if (role == null) {
            ctx.status(403).json("{\"error\":\"Operation not permitted. Login required.\"}");
            logger.warn("Operation not permitted. Login required");
            return;
        }

        if (role.equalsIgnoreCase("user")) {
            ctx.json(loanService.getLoansByUserId(userId));
            logger.info("Loans retrieved successfully for user");
            return;
        }

        if (role.toLowerCase().contains("manager")) {
            ctx.json(loanService.getLoans());
            logger.info("All Loans retrieved successfully for manager");
        } else {
            if (userId == null) {
                ctx.status(403).json("{\"error\":\"Operation not permitted. Login required.\"}");
                logger.warn("Operation not permitted. Login required");
            }

        }

    }

    public void getLoan(Context ctx) {
        int loanId;
        // Parse the loan ID from the URL path
        try {
            loanId = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"error\":\"Invalid loan ID format\"}");
            logger.error("Invalid loan ID format");
            return;
        }

        // Retrieve the loan by ID
        Loan loan = loanService.getLoanById(loanId);

        if (loan == null) {
            ctx.status(404).json("{\"error\":\"Loan not found\"}");
            logger.warn("Loan not found");
            return;
        }

        // Check user role and ownership
        String role = ctx.sessionAttribute("role");
        Integer userId = ctx.sessionAttribute("userId");

        if (role == null || (!role.toLowerCase().contains("manager") && loan.getUserId() != userId)) {
            ctx.status(403).json(
                    "{\"error\":\"Operation not permitted. Only the loan owner or a manager can view the loan.\"}");
            logger.warn("Operation not permitted. Only the loan owner or a manager can view the loan.");
            return;
        }

        ctx.json(loan);
        logger.info("Loan retrieved successfully");
    }

    public void statusLoan(Context ctx) {

        // Only allow managers to perform this update
        String role = ctx.sessionAttribute("role");

        if (role == null || !role.toLowerCase().contains("manager")) {
            ctx.status(403).json("{\"error\":\"Operation not permitted. Manager access required.\"}");
            logger.warn("Operation not permitted. Manager access required.");
            return;
        }

        String loanIdStr = ctx.pathParam("id");
        int loanId;
        try {
            loanId = Integer.parseInt(loanIdStr);
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"error\":\"Invalid loan ID format\"}");
            logger.error("Invalid loan ID format");
            return;
        }

        // Determine new status based on URL path
        String path = ctx.path();
        String newStatus;
        if (path.endsWith("/approve")) {
            newStatus = "approved";
        } else if (path.endsWith("/reject")) {
            newStatus = "rejected";
        } else {
            ctx.status(400).json("{\"error\":\"Invalid loan operation\"}");
            logger.warn("Invalid loan operation");
            return;
        }

        // Retrieve the existing loan
        Loan existingLoan = loanService.getLoanById(loanId);
        if (existingLoan == null) {
            ctx.status(404).json("{\"error\":\"Loan not found\"}");
            logger.warn("Loan not found");
            return;
        }

        // Only update if the current status is pending
        if (!"pending".equalsIgnoreCase(existingLoan.getStatus())) {
            ctx.status(400).json("{\"error\":\"Only loans with a pending status can be updated\"}");
            logger.warn("Only loans with a pending status can be updated");
            return;
        }

        // Update only the status field
        existingLoan.setStatus(newStatus);

        // Update the loan status
        boolean success = loanService.statusLoan(loanId, existingLoan);

        if (success) {
            ctx.status(200).json("{\"message\":\"Loan updated successfully\"}");
            logger.info("Loan updated successfully");
        } else {
            ctx.status(500).json("{\"error\":\"Failed to update loan\"}");
            logger.warn("Failed to update loan");
        }
    }
}
