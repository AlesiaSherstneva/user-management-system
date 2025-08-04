package com.example.dao.impl;

import com.example.dao.AdminEmailDao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminEmailDaoImpl implements AdminEmailDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<String> findAdminEmails() {
        String sql = "SELECT email FROM \"user\" WHERE role = 'ROLE_ADMIN'";
        return jdbcTemplate.queryForList(sql, String.class);
    }
}