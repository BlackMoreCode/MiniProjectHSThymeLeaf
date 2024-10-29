package projectHS.DBminiPTL.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import projectHS.DBminiPTL.VO.Acc_InfoVO;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import static projectHS.DBminiPTL.DAO.Acc_InfoDAO.Acc_InfoSelect;
//import static projectHS.DBminiPTL.DAO.Acc_InfoDAO.psmt;

@Repository
public class MyPageDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 회원 정보 업데이트
    public boolean membUpdate(Acc_InfoVO vo, String sessionUserId, List<Acc_InfoVO> accInfo) {
        // 세션 유저 ID가 Acc_InfoVO 에서의 userId랑 비교해서 적합한지 확인
        if (!sessionUserId.equals(vo.getUserId())) {
            System.out.println("check");
            System.out.println("Session User ID: " + sessionUserId);
            System.out.println("User ID from VO: " + vo.getUserId());
            throw new RuntimeException("You are not authorized to update this account.");
        }

        String userPw = vo.getUserPw();
        String userPhone = vo.getUserPhone();

        // 값 확인용 디버깅 로그
        System.out.println("Session User ID: " + sessionUserId);
        System.out.println("User ID from VO: " + vo.getUserId());
        System.out.println("New Password: " + userPw);
        System.out.println("New Phone: " + userPhone);

        // 비밀번호 조건
        if (userPw != null && !userPw.isEmpty()) {
            Pattern passPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
            Matcher passMatcher = passPattern.matcher(userPw);

            if (!passMatcher.matches()) {
                throw new RuntimeException("비밀번호는 8자 이상 20자 이하의 영문자, 숫자, 특수문자의 조합이어야 합니다.");
            }
        }

        // 연락처 중복인지 확인
        if (accInfo.stream().anyMatch(n -> userPhone.equals(n.getUserPhone()))) {
            throw new RuntimeException("이미 사용중인 번호입니다.");
        }

        String sql;
        int rst = 0;
        try{
            if (userPw != null && !userPw.isEmpty()) {
                sql = "UPDATE ACC_INFO SET USER_PW = ?, USER_PHONE = ? WHERE USER_ID = ?";
                rst = jdbcTemplate.update(sql, userPw, userPhone, vo.getUserId());
                System.out.println(rst);
            } else {
                sql = "UPDATE ACC_INFO SET USER_PHONE = ? WHERE USER_ID = ?";
                rst = jdbcTemplate.update(sql, userPhone, vo.getUserId());
                System.out.println(rst);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("회원정보 수정이 완료되었습니다.");
        return rst > 0;
    }


    // 회원 탈퇴의 경우
    public boolean membDelete(Acc_InfoVO vo, String sessionUserId, List<Acc_InfoVO> accInfo) {
        if (!sessionUserId.equals(vo.getUserId())) {
            System.out.println("check");
            System.out.println("Session User ID: " + sessionUserId);
            System.out.println("User ID from VO: " + vo.getUserId());
            throw new RuntimeException("You are not authorized to update this account.");
        }

        String userId = vo.getUserId();
        int rst = 0;
        String sql = "DELETE FROM ACC_INFO WHERE USER_ID = ?";
        try {
            rst = jdbcTemplate.update(sql, userId); // DELETE 쿼리문 실행?
            if (rst > 0) {
                System.out.println("회원 탈퇴가 완료되었습니다.");
                return true;
            } else {
                System.out.println("삭제할 회원을 찾지 못했습니다.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("회원 탈퇴 중 에러 발생: " + e.getMessage());
        }
        return rst > 0;
    }
}

