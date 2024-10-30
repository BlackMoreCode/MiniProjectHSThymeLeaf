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
    public BigDecimal cpCharge(BigDecimal amount, String storeId) { // capital Charge
        String sql = "UPDATE STORE SET capital = capital + ? WHERE store_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, amount, storeId);

        if (rowsAffected > 0) {
            // 업데이트 성공 시, 현재 자본 값을 반환
            String selectSql = "SELECT capital FROM STORE WHERE store_id = ?";
            return jdbcTemplate.queryForObject(selectSql, new Object[]{storeId}, BigDecimal.class);
        } else {
            // 업데이트 실패 시 null 반환
            return null;
        }
    }

    // DataBase Table Store에 store_id와 로그인 했을때 지점이 같은지 확인
//    public String getCpCStoreId(String userId) {
//        String storeId = null;
//        String sql = "SELECT STORE_ID FROM ACC_INFO WHERE USER_ID = '" + userId + "'";
//        storeId = jdbcTemplate.queryForObject(sql, new Object[]{userId},String.class);
//        return storeId;
//    }

    // 가용금액 표시
    public BigDecimal cpSelect(String cpStoreId) { // capitalSelect
        String sql = "SELECT capital FROM Store WHERE store_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{cpStoreId}, BigDecimal.class);
    }

    // DataBase Table Store에 store_id와 로그인 했을때 지점이 같은지 확인
//    public String getCpSStoreId(String userId) {
//        String storeId = null;
//        String sql = "SELECT STORE_ID FROM ACC_INFO WHERE USER_ID = '" + userId + "'";
//        storeId = jdbcTemplate.queryForObject(sql, new Object[]{userId},String.class);
//        return storeId;
//    }

    // 매출현황
    public BigDecimal slSelect(String storeId) {
        String sql = "SELECT sales FROM Store WHERE store_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{storeId}, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            // Handle case where no result is found
            log.warn("No sales record found for store ID: " + storeId);
            return BigDecimal.ZERO; // Or any default value appropriate for your use case
        }
    }





    // DataBase Table Store에 store_id와 로그인 했을때 지점이 같은지 확인
//    public String getSlStoreId(String userId) {
//        String storeId = null;
//        String sql = "SELECT STORE_ID FROM ACC_INFO WHERE USER_ID = '" + userId + "'";
//        storeId = jdbcTemplate.queryForObject(sql, new Object[]{userId},String.class);
//        return storeId;
//    }

//    // 매출현황에 소비자가 지불한 메뉴 금액만큼 추가
//    public static void salesPTp(int tp, String store_id) { // sales + Totalprice
//        try {
//            conn = Common.getConnection(); // 오라클 DB연결
//            String sql = "UPDATE STORE SET sales = sales + ? WHERE store_id = ?"; // 계좌 충전
//            psmt = conn.prepareStatement(sql); // 동적인 데이터로 받을때 사용 (?)
//            psmt.setInt(1, tp); // capital 설정
//            psmt.setString(2,store_id); // store_id 설정
//
//            if (psmt.executeUpdate() == 0) { // UPDATE
//                throw new Exception();
//            };
//            Common.close(psmt);
//            Common.close(conn);
//
//        } catch (Exception e) {
//            System.out.println("매출액 합산 실패");
//            Common.close(psmt);
//            Common.close(conn);
//        }
//    }

//    public static class cpSelectRowMapper implements RowMapper<StoreVO> {
//        @Override
//        public StoreVO mapRow(ResultSet rs, int rowNum) throws SQLException {
//            return new StoreVO(
//                    rs.getString("storeId"),
//                    rs.getInt("capital")
//            );
//        }
//    }
//
//    public static class slSelectRowMapper implements RowMapper<StoreVO> {
//
//        @Override
//        public StoreVO mapRow(ResultSet rs, int rowNum) throws SQLException {
//            return new StoreVO(
//                    rs.getBigDecimal("sales")
//            );
//        }
//    }
}
