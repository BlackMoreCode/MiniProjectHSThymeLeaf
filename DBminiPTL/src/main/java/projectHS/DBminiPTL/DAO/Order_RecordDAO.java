package projectHS.DBminiPTL.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import projectHS.DBminiPTL.Common.Common;
import projectHS.DBminiPTL.VO.Order_RecordVO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class Order_RecordDAO {

    static Connection conn = null;
    static PreparedStatement psmt = null;
    static ResultSet rs = null;

    private final JdbcTemplate jdbcTemplate;

    public Order_RecordDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // 고객의 주문 정보 출력
    // 리스트를 반환
    public List<Order_RecordVO> userOrderList(String userId) {
        String sql = "SELECT * FROM ORDER_RECORD WHERE USER_ID = ?";
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일, HH시 mm분 ss초");

        List<Order_RecordVO> vo = jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            String code = rs.getString("ORDER_CODE");
            String lst = rs.getString("ORDER_LIST");
            String list = stringToList(lst);
            Timestamp time = rs.getTimestamp("ORDER_TIME");
            int price = rs.getInt("ORDER_PRICE"); // Ensure this is the correct field name
            String store = rs.getString("STORE_ID");

            String formattedTime = format.format(time);
            return new Order_RecordVO(code, list, formattedTime, price, store);
        });

        // Sort by store name and then by order time
        vo.sort((o1, o2) -> {
            if (!o1.getStoreId().equals(o2.getStoreId())) {
                return o1.getStoreId().compareTo(o2.getStoreId());
            }
            return o1.getOrderTime().compareTo(o2.getOrderTime());
        });

        return vo;
    }

    // 주문 내역 삽입(InvDAO 에서 결제 시 동작)

    public void orderRecordInsert(String storeId, String orderList, int price, String userId) {
        String sql = "INSERT INTO ORDER_RECORD (ORDER_CODE, ORDER_LIST, ORDER_TIME, ORDER_PRICE, USER_ID, STORE_ID) VALUES (?, ?, ?, ?, ?, ?)";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = format.format(timestamp);

        String orderId = storeId + timeStr;

        System.out.println(orderList + "삽입");

        // JDBC 템플릿을 사용하여 데이터 삽입
        int rows = jdbcTemplate.update(sql, orderId, orderList, timestamp, price, userId, storeId);

        System.out.println(rows + "개 행 삽입");
    }


    public static String stringToList(String lst) {
        return lst.replace("/", " : ").replace(",", "<br>").replace("+", "<br>");
    }


}
