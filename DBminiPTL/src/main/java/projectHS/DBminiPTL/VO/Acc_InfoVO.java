package projectHS.VO;

import java.sql.Date;

public class Acc_InfoVO {
    private String userId;
    private String userPw;
    private String userName;
    private String userPhone;// DB에서 char(13)라는 고정 값을 쓰므로 JDBC에선 유연성을 위해 문자열 처리
    private Date joinDate;
    private int authLv;
    private String storeId;

    public Acc_InfoVO(String userId, String userPw, String userName, String userPhone, Date joinDate, String storeId, int authLv) {
        this.userId = userId;
        this.userPw = userPw;
        this.userName = userName;
        this.userPhone = userPhone;
        this.joinDate = joinDate;
        this.authLv = authLv;
        this.storeId = storeId;
    }

    public Acc_InfoVO() {
    }

    public Acc_InfoVO(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public Acc_InfoVO(String userId, String userPw, String userName, String userPhone, Date joinDate, int authLv) {
        this.userId = userId;
        this.userPw = userPw;
        this.userName = userName;
        this.userPhone = userPhone;
        this.joinDate = joinDate;
        this.authLv = authLv;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public int getAuthLv() {
        return authLv;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhone(String userPhone) {
        if (userPhone != null && userPhone.length() == 13) { //13자리 입력시 문제 없이 통과.
            this.userPhone = userPhone;
        } else {    // 데이터베이스와의 일관성을 위해 제약 조건 부과.
            throw new IllegalArgumentException("연락처는 정확히 '-' 을 포함해 13자여야 합니다.");
        }
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public void setAuthLv(int authLv) {
        this.authLv = authLv;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}