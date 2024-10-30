package projectHS.DBminiPTL.DAO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import projectHS.DBminiPTL.VO.Inv_OrderVO;
import java.sql.*;
import java.util.List;


@Repository
@Slf4j

public class Inv_OrderDAO {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public Inv_OrderDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    boolean isSuccess = false;


    // 메뉴 확인 (Select)
    public List<Inv_OrderVO> Inv_OrderSelect() {
        String query = "SELECT * FROM INV_ORDER";
        return jdbcTemplate.query(query, new ioVOSelectRowMapper());
    }

    private static class ioVOSelectRowMapper implements RowMapper<Inv_OrderVO> {
        @Override
        public Inv_OrderVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Inv_OrderVO(
                    rs.getString("MENU_NAME"),
                    rs.getInt("PRICE"),
                    rs.getString("CATEGORY"),
                    rs.getString("DESCR")
            );
        }
    }

    // 메뉴 수정
    public void Inv_OrderUpdate(Inv_OrderVO ioVO) {
        String query = "UPDATE INV_ORDER SET PRICE = ?, CATEGORY = ?, DESCR = ? WHERE MENU_NAME = ?";
        jdbcTemplate.update(query, ioVO.getPrice(), ioVO.getCategory(), ioVO.getDescr(), ioVO.getMenuName());
    }

    // 메뉴 삭제
    public boolean Inv_OrderDelete(String menuName) {
        int result = 0;
        String query = "DELETE FROM INV_ORDER WHERE MENU_NAME = ?";
        try {
            result = jdbcTemplate.update(query, menuName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }

    // 메뉴 추가
    public void Inv_OrderInsert(Inv_OrderVO ioVO) {
        String query = "INSERT INTO INV_ORDER(MENU_NAME, PRICE, CATEGORY, DESCR) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(query, ioVO.getMenuName(), ioVO.getPrice(), ioVO.getCategory(), ioVO.getDescr());
    }

}