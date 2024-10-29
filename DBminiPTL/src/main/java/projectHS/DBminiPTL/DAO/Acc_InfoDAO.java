package projectHS.DBminiPTL.DAO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import projectHS.DBminiPTL.Common.Common;
import projectHS.DBminiPTL.VO.Acc_InfoVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
@Slf4j

public class Acc_InfoDAO {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public Acc_InfoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int checkUserAuthLevel(String userId, String userPw) {
        int authLevel = 0;
        String query = "SELECT AUTH_LV FROM ACC_INFO WHERE USER_ID = ? AND USER_PW = ?";
        System.out.println("Checking user auth level for ID: " + userId);

        try {
            // Try to get the authentication level from the database
            authLevel = jdbcTemplate.queryForObject(query, new Object[]{userId, userPw}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            // 주어진 ID 와 비밀번호로 나오는 결과 값이 없음
            System.out.println("No auth level found for ID: " + userId);
            return 0; // 혹은 authlevel 이 없는 쪽이라던지..
        } catch (Exception e) {
            // 디버깅용; 결과 로깅.
            System.err.println("Error while checking user auth level: " + e.getMessage());
            e.printStackTrace();
        }
        return authLevel;
    }

    // 회원 정보 조회 (전체 조회; 중복 체크만을 위험이므로 유저권한 및 지점 정보를 제외하고 불러온다)
    public List<Acc_InfoVO> Acc_InfoSelect() {
        String query = "SELECT * FROM ACC_INFO"; //추후 수정 가능
        return jdbcTemplate.query(query, new Acc_InfoRowMapper());
    }

    private static class Acc_InfoRowMapper implements RowMapper<Acc_InfoVO> {
        @Override
        public Acc_InfoVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Acc_InfoVO(
                    rs.getString("USER_ID"),
                    rs.getString("USER_PW"),
                    rs.getString("USER_NAME"),
                    rs.getString("USER_PHONE"),
                    rs.getDate("JOIN_DATE"),
                    rs.getString("STORE_ID"),
                    rs.getInt("AUTH_LV")

            );
        }
    }

    // 점주 로그인시 storeID 확인하고 점주용 페이지 접속
    public String adminStore(String userId){
        String query = "SELECT STORE_ID FROM ACC_INFO WHERE USER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{userId}, String.class);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    // 회원 가입을 한다 = ACC_INFO 테이블에 추가한다 = INSERT 처리다?
    // 회원 가입을 위해서는 희망 아이디, 비밀번호, 연락처를 기입. 가입일시, 유저레벨(AUTH_LV)은 자동으로 부여. STORE_ID 역시 입력하지 않는다.
    public boolean Acc_InfoInsert(Acc_InfoVO aiVO) {
        // 회원정보 불러오기; Acc_InfoSelect 참조.


        List<Acc_InfoVO> accInfo = Acc_InfoSelect();

        String userId = aiVO.getUserId();
        Common.Util ut = new Common.Util();

        if (accInfo.stream().anyMatch(n -> n.getUserId().equals(userId))) {
            System.out.println("이미 사용중인 아이디 입니다.");
            return false;
        } else if (!ut.checkInputNumAndAlphabet(userId)) {
            System.out.println("영문과 숫자 조합만 사용해주세요.");
            return false;
        } else if (userId.length() < 5) {
            System.out.println("ID는 5자 이상 입력해주세요");
            return false;
        } else if (userId.length() > 20) {
            System.out.println("ID는 20자 이하로 입력해주세요");
            return false;
        }

        String userPw = aiVO.getUserPw();
        Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
        Matcher passMatcher1 = passPattern1.matcher(userPw);
        if (userPw.length() < 8) {
            System.out.println("비밀번호는 8자 이상 입력해주세요");
            return false;
        } else if (userPw.getBytes().length > 20) {
            System.out.println("비밀번호는 20자 이하 영문자와 &제외 특수문자로 입력해주세요");
            return false;
        } else if (!passMatcher1.find()) {
            System.out.println("비밀번호는 영문자, 숫자, 특수기호의 조합으로만 사용 할 수 있습니다.");
            return false;
        } else if (userPw.indexOf('&') >= 0) {
            System.out.println("&는 비밀번호로 사용할수 없습니다.");
            return false;
        }

        String userPhone = aiVO.getUserPhone();
        if (accInfo.stream().anyMatch(n -> n.getUserPhone().equals(userPhone))) {
            System.out.println("이미 사용중인 번호 입니다.");
            return false;
        } else if (userPhone.length() != 13) {
            System.out.print("전화번호는 (-)포함 13글자로 작성하세요.");
            return false;
        }

        String query = "INSERT INTO ACC_INFO(USER_ID, USER_PW, USER_NAME, USER_PHONE, JOIN_DATE, AUTH_LV) VALUES (?, ?, ?, ?, SYSDATE, 3)";
        int result = 0;

        try {
            result = jdbcTemplate.update(query, aiVO.getUserId(), aiVO.getUserPw(), aiVO.getUserName(), aiVO.getUserPhone());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }
}