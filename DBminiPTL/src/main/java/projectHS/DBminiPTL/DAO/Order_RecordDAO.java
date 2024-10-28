package projectHS.DAO;

import projectHS.Common.Common;
import projectHS.VO.Order_RecordVO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Order_RecordDAO {

    static Connection conn = null;
    static PreparedStatement psmt = null;
    static ResultSet rs = null;

    // 고객의 주문 정보 출력
    // 메인에서 이 메서드만 실행하면 출력됨
    public static void userOrderList(String userId) {
        List<Order_RecordVO> vo = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일, HH시 mm분 ss초");
        String sql = "SELECT * FROM ORDER_RECORD WHERE USER_ID = ?";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, userId);
            rs = psmt.executeQuery();

            while (rs.next()) {
                String code = rs.getString("ORDER_CODE");
                String list = rs.getString("ORDER_LIST");
                Timestamp time = rs.getTimestamp("ORDER_TIME");
                int price = rs.getInt("ORDER_PRICE");
                String store = rs.getString("STORE_ID");

                String formattedTime = format.format(time); // 주문시간을 문자열로 변환

                vo.add(new Order_RecordVO(code, list, formattedTime, price, store));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // 지점 이름순 정렬, 지점 이름이 같다면 주문시간이 빠른순 정렬
        vo.sort((o1, o2) -> {
            if (!o1.getStoreId().equals(o2.getStoreId())) {
                return o1.getStoreId().compareTo(o2.getStoreId());
            }
            return o1.getOrderTime().compareTo(o2.getOrderTime());
        });

        System.out.println("=".repeat(15) + userId + " 고객님의 주문 내역" + "=".repeat(15));
        for (Order_RecordVO e : vo) {
            System.out.println("주문 지점 : " + e.getStoreId());
            System.out.println("주문 시각 : " + e.getOrderTime());
            System.out.println("*주문 내역*\n" + e.getOrderList()
                    .replace("/", " : ").replace(",", "\n").replace("+", "\n"));
            System.out.println("총 가격 : " + e.getOrderPrice() + "원");
            System.out.println("-".repeat(15));
        }
    }

    // 주문 내역 삽입(InvDAO 에서 결제 시 동작)
    public static void orderRecordInsert(String storeId, String orderList, int price, String userId) {
        String sql = "INSERT INTO ORDER_RECORD VALUES (?, ?, ?, ?, ?, ?)";
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = format.format(timestamp);

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, (storeId + timeStr));
            psmt.setString(2, orderList);
            psmt.setTimestamp(3, timestamp);
            psmt.setInt(4, price);
            psmt.setString(5, userId);
            psmt.setString(6, storeId);
            int rows = psmt.executeUpdate();

            System.out.println(rows + "개 행 삽입");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
    }

}
