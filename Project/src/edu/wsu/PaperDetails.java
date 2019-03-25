package edu.wsu;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/paperdetails/*")
public class PaperDetails extends HttpServlet {

    private DBConnection _dbConnection;

    private ArrayList<String> getPcMembersList() {
        ArrayList<String> pcMembers = new ArrayList<>();

        try {
            Statement statementPcMembers = _dbConnection.createStatement();
            ResultSet rsPcMembers = statementPcMembers.executeQuery(" SELECT * FROM pc_members");

            while (rsPcMembers.next()) {
                pcMembers.add(rsPcMembers.getString("email"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return pcMembers;
    }

    private Map<String, String> getPaperDetails(String paperId) {
        Map<String, String> paperDetails = new HashMap<>();

        try {
            Statement statementPaper = _dbConnection.createStatement();
            ResultSet rsPaper = statementPaper.executeQuery("SELECT * FROM papers WHERE paperid = " + paperId);

            if (rsPaper.first()) {
                paperDetails.put("paperid", String.valueOf(rsPaper.getInt("paperid")));
                paperDetails.put("title", rsPaper.getString("title"));
                paperDetails.put("description", rsPaper.getString("abstract"));
                paperDetails.put("pdf", rsPaper.getString("pdf"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return paperDetails;
    }

    private ArrayList<String> getPaperReviewers(String paperId) {
        ArrayList<String> paperReviewers = new ArrayList<>();

        try {
            Statement statementPaperReviewers = _dbConnection.createStatement();
            ResultSet rsPaperReviewers = statementPaperReviewers.executeQuery(" SELECT pc_member_id FROM reports WHERE paper_id = " + paperId);

            while (rsPaperReviewers.next()) {
                paperReviewers.add(rsPaperReviewers.getString("pc_member_id"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return paperReviewers;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> paperDetails;
        ArrayList<String> pcMembers = new ArrayList<>();
        ArrayList<String> paperReviewers;

        String[] pathInfo = req.getPathInfo().split("/");
        String paperId = pathInfo[1];

        _dbConnection = new DBConnection();

        paperReviewers = getPaperReviewers(paperId);
        if (paperReviewers.isEmpty()) {
            pcMembers = getPcMembersList();
        }
        paperDetails = getPaperDetails(paperId);

        _dbConnection.closeConnection();

        req.setAttribute("paperDetails", paperDetails);
        req.setAttribute("pcMembers", pcMembers);
        req.setAttribute("paperReviewers", paperReviewers);
        req.getRequestDispatcher("/paper-details.jsp").forward(req, resp);
    }
}

/*
                    Statement statement = dbConnection.getConnection().createStatement();

                    statement.executeUpdate("INSERT INTO reports (paper_id, pc_member_id) VALUES ('" + paperId + "', '" + firstReviewer + "'), " +
                            "('" + paperId + "', '" + secondReviewer + "'), " +
                            "('" + paperId + "', '" + thirdReviewer + "')");

 */