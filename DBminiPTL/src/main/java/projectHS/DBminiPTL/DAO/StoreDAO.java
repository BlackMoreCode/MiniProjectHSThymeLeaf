package projectHS.DBminiPTL.DAO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
@Slf4j

public class StoreDAO {
    private final JdbcTemplate jdbcTemplate;

    public StoreDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 가상 계좌 충전
    public BigDecimal cpCharge(BigDecimal amount, String user_Id) { // capital Charge
        String sql = "UPDATE STORE SET capital = capital + ?" +
                "WHERE store_id = (SELECT store_id FROM ACC_INFO WHERE user_id = ?)";
        int rowsAffected = jdbcTemplate.update(sql, amount, user_Id);

        if (rowsAffected > 0) {
            // 업데이트 성공 시, 현재 자본 값을 반환
            String selectSql = "SELECT capital FROM STORE " +
                    "WHERE store_id = (SELECT store_id FROM ACC_INFO WHERE user_id = ?)";
            return jdbcTemplate.queryForObject(selectSql, new Object[]{user_Id}, BigDecimal.class);
        } else {
            // 업데이트 실패 시 null 반환
            return null;
        }
    }

    // 가용금액 표시
    public BigDecimal cpSelect(String user_Id) { // capitalSelect
        String sql = "SELECT s.capital FROM Store s JOIN ACC_INFO  a ON s.store_id = a.store_id" +
                " WHERE a.user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{user_Id}, BigDecimal.class);
    }


    // 매출현황
    public BigDecimal slSelect(String user_Id) {
        String sql = "SELECT s.sales FROM Store s JOIN ACC_INFO  a ON s.store_id = a.store_id" +
                " WHERE a.user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{user_Id}, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            log.warn("No sales record found for user ID: " + user_Id);
            return BigDecimal.ZERO; // Or any default value appropriate for your use case
        }

    }

}
