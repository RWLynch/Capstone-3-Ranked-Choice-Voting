package com.techelevator.dao;

import com.techelevator.model.IssueDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcIssueDetailsDao implements IssueDetailsDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcIssueDetailsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IssueDetails getIssueById(int issueId) {
        IssueDetails results = new IssueDetails();
        String sql = "SELECT issue_id, issue_name, issue_owner_id, description, date_posted, expiration_date, expiration_time, status, genre_tag " +
                "FROM issues WHERE issue_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, issueId);
        if (rowSet.next()) {
            results = mapRowToIssueDetails(rowSet);
        }

        return results;
    }

    @Override
    public boolean postIssue(IssueDetails issue) {
        String sql = "INSERT INTO issues " +
                "(issue_name, issue_owner_id, description, date_posted, expiration_date, expiration_time, status, genre_tag) " +
                "VALUES (?, 1, ?, CURRENT_TIMESTAMP(0), ?, ?, 'active', ?) RETURNING issue_id;";
        try {
            jdbcTemplate.update(sql, issue.getIssueName(), issue.getDescription(), issue.getDateExpiration(), issue.getTimeExpiration(), issue.getGenreTag());
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateAnIssue(IssueDetails issue) {
        String sql = "UPDATE issues " +
                "SET issue_name = ?, description = ?, expiration_date = ?, expiration_time = ?, genre_tag = ? WHERE issue_id = ?;";
        try {
            jdbcTemplate.update(sql, issue.getIssueName(), issue.getDescription(), issue.getDateExpiration(), issue.getTimeExpiration(), issue.getGenreTag(), issue.getIssueId());
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean moveToCompleted(int issueId) {
        String sql = "UPDATE issues SET status = 'completed' WHERE issue_id = ?;";
        try {
            jdbcTemplate.update(sql, issueId);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }


    @Override
    public List<IssueDetails> getAllCompletedIssues() {
        List<IssueDetails> results = new ArrayList<>();
        String sql = "SELECT issue_id, issue_name, issue_owner_id, description, date_posted, expiration_date, expiration_time, status, genre_tag FROM issues WHERE status = 'completed';";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while(rowSet.next()){
            IssueDetails issue = mapRowToIssueDetails(rowSet);
            results.add(issue);
        }
        return results;
    }

    @Override
    public List<IssueDetails> getAllActiveIssues() {
        List<IssueDetails> results = new ArrayList<>();
        String sql = "SELECT issue_id, issue_name, issue_owner_id, description, date_posted, expiration_date, expiration_time, status, genre_tag FROM issues WHERE status = 'active';";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while(rowSet.next()){
            IssueDetails issue = mapRowToIssueDetails(rowSet);
            results.add(issue);
        }
        return results;
    }

    @Override
    public IssueDetails queryForIssueId() {
        IssueDetails postedIssue = new IssueDetails();
        String sql = "SELECT issue_id, issue_name, issue_owner_id, description, date_posted, expiration_date, expiration_time, status, genre_tag FROM issues ORDER BY issue_id DESC LIMIT 1;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        if (rowSet.next()) {
            postedIssue = mapRowToIssueDetails(rowSet);
        }
        return postedIssue;
    }

    @Override
    public boolean updateChoices(String choiceString) {
        String[] splitStringArray = choiceString.split(",");
        boolean success = false;
        String sql = "UPDATE choices SET choice_1 = ?";
        return success;
    }

    @Override
    public void deleteIssue(int issueId) {
        String sql = "DELETE FROM issues WHERE issue_id = ?;";
        jdbcTemplate.update(sql, issueId);
    }

    private IssueDetails mapRowToIssueId(SqlRowSet rowSet) {
        IssueDetails issueDetails = new IssueDetails();
        issueDetails.setIssueId(rowSet.getInt("issue_id"));
        return issueDetails;
    }

    private IssueDetails mapRowToIssueDetails(SqlRowSet rowSet) {
        IssueDetails issueDetails = new IssueDetails();
        issueDetails.setIssueId(rowSet.getInt("issue_id"));
        issueDetails.setIssueName(rowSet.getString("issue_name"));
        issueDetails.setOwnerId(rowSet.getInt("issue_owner_id"));
        issueDetails.setDescription(rowSet.getString("description"));
        issueDetails.setDatePosted(rowSet.getTimestamp("date_posted"));
        issueDetails.setDateExpiration(rowSet.getString("expiration_date"));
        issueDetails.setTimeExpiration(rowSet.getString("expiration_time"));
        issueDetails.setStatus(rowSet.getString("status"));
        issueDetails.setGenreTag(rowSet.getString("genre_tag"));
        return issueDetails;
    }
}
