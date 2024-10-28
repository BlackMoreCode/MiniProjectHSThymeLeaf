package projectHS.DBminiPTL.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import projectHS.DBminiPTL.Common.Common;
import projectHS.DBminiPTL.VO.Acc_InfoVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import static projectHS.DBminiPTL.DAO.Acc_InfoDAO.Acc_InfoSelect;
//import static projectHS.DBminiPTL.DAO.Acc_InfoDAO.psmt;

@Repository
public class MyPageDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 회원 정보 업데이트
    public void membUpdate(Acc_InfoVO vo, List<Acc_InfoVO> accInfo) {
        String userPw = vo.getUserPw();
        String userPhone = vo.getUserPhone();

        // 비밀번호 조건
        if (userPw != null && !userPw.isEmpty()) {
            Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
            Matcher passMatcher1 = passPattern1.matcher(userPw);

            if (!passMatcher1.matches()) {
                throw new RuntimeException("비밀번호는 8자 이상 20자 이하의 영문자, 숫자, 특수문자의 조합이어야 합니다.");
            }
        }

        // 연락처 중복인지 확인
        if (accInfo.stream().anyMatch(n -> userPhone.equals(n.getUserPhone()))) {
            throw new RuntimeException("이미 사용중인 번호입니다."); //
        }

        String sql;
        if (userPw != null && !userPw.isEmpty()) {
            sql = "UPDATE ACC_INFO SET USER_PW = ?, USER_PHONE = ? WHERE USER_ID = ?";
            jdbcTemplate.update(sql, userPw, userPhone, vo.getUserId());
        } else {
            sql = "UPDATE ACC_INFO SET USER_PHONE = ? WHERE USER_ID = ?";
            jdbcTemplate.update(sql, userPhone, vo.getUserId());
        }

        System.out.println("회원정보 수정이 완료되었습니다.");
    }

    // 회원 탈퇴의 경우
    public boolean membDelete(Acc_InfoVO vo) {
        String userId = vo.getUserId();
        String sql = "DELETE FROM ACC_INFO WHERE USER_ID = ?";

        try {
            int rowsAffected = jdbcTemplate.update(sql, userId); // DELETE 쿼리문 실행?

            if (rowsAffected > 0) {
                System.out.println("회원 탈퇴가 완료되었습니다.");
                return true;
            } else {
                System.out.println("삭제할 회원을 찾지 못했습니다.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("회원 탈퇴 중 에러 발생: " + e.getMessage());
            return false; // 옵셔널한 방법으로 다른 에러 발생시 처리
        }
    }
}

