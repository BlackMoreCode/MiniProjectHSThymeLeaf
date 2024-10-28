package projectHS.VO;

import java.sql.Timestamp;

public class Order_RecordVO {
    private String orderCode;
    private String orderList;
    private String orderTime;
    private int orderPrice;
    private String userId;
    private String storeId;

    public Order_RecordVO(String orderCode, String orderList, String orderTime, int orderPrice, String userId, String storeId) {
        this.orderCode = orderCode;
        this.orderList = orderList;
        this.orderTime = orderTime;
        this.orderPrice = orderPrice;
        this.userId = userId;
        this.storeId = storeId;
    }

    // 고객의 주문조회를 위한 생성자
    public Order_RecordVO(String orderCode, String orderList, String orderTime, int orderPrice, String storeId) {
        this.orderCode = orderCode;
        this.orderList = orderList;
        this.orderTime = orderTime;
        this.orderPrice = orderPrice;
        this.storeId = storeId;
    }

    // 가게의 주문조회를 위한 생성자
    public Order_RecordVO(String orderCode, String orderList, String orderTime, String userId, int orderPrice) {
        this.orderCode = orderCode;
        this.orderList = orderList;
        this.orderTime = orderTime;
        this.orderPrice = orderPrice;
        this.userId = userId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getOrderList() {
        return orderList;
    }

    public String  getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String  orderTime) {
        this.orderTime = orderTime;
    }

    public int getOrderPrice() {
        return orderPrice;
    }

    public String getUserId() {
        return userId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setOrderList(String orderList) {
        this.orderList = orderList;
    }


    public void setOrderPrice(int orderPrice) {
        if (orderPrice < 0) {   // null은 허용 안하니까 조건은 음수일 수 없다로만 설정.
            throw new IllegalArgumentException("제품 가격은 음수일 수 없습니다.");
        }
        this.orderPrice = orderPrice;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
