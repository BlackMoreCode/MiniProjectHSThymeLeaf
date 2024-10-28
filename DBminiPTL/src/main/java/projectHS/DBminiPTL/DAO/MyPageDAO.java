package projectHS.DAO;

import projectHS.Common.Common;
import projectHS.VO.Acc_InfoVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Scanner;

import static projectHS.DAO.Acc_InfoDAO.Acc_InfoSelect;
import static projectHS.DAO.Acc_InfoDAO.psmt;

public class MyPageDAO {
    static Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    static Scanner sc = new Scanner(System.in);

    Acc_InfoDAO aiDAO = new Acc_InfoDAO();

    // ================ 필요 메서드 ===================
    //주문 내역 조회 (추후 작업)



    //회원 정보 수정
    public static void membUpdate(Acc_InfoVO vo, String userId) {
        // 회원정보 불러오기; Acc_InfoSelect 참조.
        List<Acc_InfoVO> accInfo = Acc_InfoSelect();

        //비밀번호 수정 시
        String userPw = "";
        while (true) {
            System.out.println("비밀번호를 바꾸지 않을 경우 NO를 입력해주세요");
            System.out.print("변경할 비밀번호(8자 이상 20자 이하) : ");
            userPw = sc.next();

            if(userPw.equalsIgnoreCase("NO")) break; // 위에서 언급한 NO 입력시 비밀번호 수정 안함 처리.
            else if(userPw.length() < 8) System.out.println("비밀번호는 8자 이상 입력해주세요");
            else if (userPw.length() > 20) System.out.println("비밀번호는 20자 이하로 입력해주세요");
            else if (userPw.indexOf('&') >= 0) System.out.println("&는 비밀번호로 사용할수 없습니다.");
            else break;
        }

        // 연락처 수정시
        String userPhone = "";
        while(true) {
            System.out.println("연락처를 바꾸지 않을 경우 NO를 입력해주세요");
            System.out.print("변경할 핸드폰 번호를 입력하세요 : ");
            userPhone = sc.next();
            String check = userPhone;
            //중복 체크
            // 회원정보 리스트에 스트림으로 필터를 걸어서 하나라도 일치하는게 있다면 값 반환 없으면 null 반환
            if(accInfo.stream().filter(n -> check.equals(n.getUserPhone())).findAny().orElse(null) != null) {
                System.out.println("이미 사용중인 번호 입니다.");
            }else if (userPhone.equalsIgnoreCase("NO")){ // // 위에서 언급한 NO 입력시 연락처 수정 안함 처리.
                userPhone = vo.getUserPhone();
                break;
            }else break;
        }

        String sql = "";
        if (userPw.equalsIgnoreCase("NO")) {
            sql = "UPDATE ACC_INFO SET USER_PHONE = ? WHERE USER_ID = ?";
            try{
                conn = Common.getConnection();
                psmt = conn.prepareStatement(sql);
                psmt.setString(1, userPhone);
                psmt.setString(2, userId);
                psmt.executeUpdate();
            }catch(Exception e)  {
                System.out.println(e.getMessage());
            }
        } else {
            sql = "UPDATE ACC_INFO SET USER_PW = ?, USER_PHONE = ? WHERE USER_ID = ?";
            try{
                conn = Common.getConnection();
                psmt = conn.prepareStatement(sql);
                psmt.setString(1, userPw);
                psmt.setString(2, userPhone);
                psmt.setString(3, userId);
                psmt.executeUpdate();
            }catch(Exception e)  {
                System.out.println(e.getMessage());
            }
        }
        Common.close(psmt);
        Common.close(conn);
        System.out.println("회원정보 수정이 완료되었습니다.");
    }

    // 회원 탈퇴의 경우
    public static boolean membDelete(Acc_InfoVO vo) {
        Connection conn = null;
        PreparedStatement psmt = null;
        Scanner sc = new Scanner(System.in);

        System.out.print("삭제할 회원의 아이디를 입력 하세요 : ");
        String userId = sc.next();
        String sql = "DELETE FROM ACC_INFO WHERE USER_ID = ?";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, userId);
            int rst = psmt.executeUpdate(); // INSERT, UPDATE, DELETE에 해당하는 함수
            System.out.println("DELETE 결과로 영향 받는 행의 갯수 : " + rst);
            return true; // 원래는 반환값 받는거 처리해야한다.. 쿼리문에 대한 성공실패만 판정. 이 부분에 대한 변경은 추후 논의
        } catch (Exception e) {
            System.out.println("DELETE 실패");
            return false;
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
    }
}