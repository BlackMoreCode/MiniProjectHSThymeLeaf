package projectHS.DAO;

import projectHS.Common.Common;
import projectHS.VO.Acc_InfoVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Acc_InfoDAO {
    static Connection conn = null;
    static Statement stmt = null;
    static PreparedStatement psmt = null;
    static ResultSet rs = null;
    static Scanner sc = new Scanner(System.in);

    public int checkUserAuthLevel(String userId, String userPw) {
        int authLevel = 0;
        try {
            conn = Common.getConnection();
            String query = "SELECT AUTH_LV FROM ACC_INFO WHERE USER_ID = ? AND USER_PW = ?";
            psmt = conn.prepareStatement(query);
            psmt.setString(1, userId);
            psmt.setString(2, userPw);
            rs = psmt.executeQuery();
            if (rs.next()) {
                authLevel = rs.getInt("AUTH_LV");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);
        }
        return authLevel;
    }

    // 회원 정보 조회 (전체 조회; 중복 체크만을 위험이므로 유저권한 및 지점 정보를 제외하고 불러온다)
    public static List<Acc_InfoVO> Acc_InfoSelect() {
        List<Acc_InfoVO> accInfo = new ArrayList<>();
        try {
            conn = Common.getConnection(); // 오라클DB 연결
            stmt = conn.createStatement(); // Statement 생성
            String query = "SELECT * FROM ACC_INFO"; //추후 수정 가능
//            String query = "SELECT USER_ID, USER_NAME FROM ACC_INFO"; //추후 수정 가능
            // executeQuery: select 문과 같이 결과값이 여러 개의 레코드로 반환되는 경우 사용
            rs = stmt.executeQuery(query); // ResultSet: 여러 행의 결과값을 받아서 반복자(iterator)를 제공
            while (rs.next()) { // 아이디 & 이름만 불러오니까 필요 없는건 주석처리 혹은 제거 필ㅇ?
                String userId = rs.getString("USER_ID");
                String userPw = rs.getString("USER_PW");
                String userName = rs.getString("USER_NAME");
                String userPhone = rs.getString("USER_PHONE");
                Date joinDate = rs.getDate("JOIN_DATE");
                int authLv = rs.getInt("AUTH_LV");
                Acc_InfoVO vo = new Acc_InfoVO(userId, userPw, userName, userPhone, joinDate, authLv);
                accInfo.add(vo);
            }
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);

        } catch (Exception e) {
            System.out.println("회원정보 조회 실패");
        }
        return accInfo;
    }

    public String adminStore(String userId){
        String sql = "SELECT STORE_ID FROM ACC_INFO WHERE USER_ID = ?";
        String id = "";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, userId);
            rs = psmt.executeQuery();

            while (rs.next()){
                id = rs.getString("STORE_ID");
            }
            return id;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    // 회원 가입을 한다 = ACC_INFO 테이블에 추가한다 = INSERT 처리다?
    // 회원 가입을 위해서는 희망 아이디, 비밀번호, 연락처를 기입. 가입일시, 유저레벨(AUTH_LV)은 자동으로 부여. STORE_ID 역시 입력하지 않는다.
    public static void Acc_InfoInsert() {
        // 회원정보 불러오기; Acc_InfoSelect 참조.
        List<Acc_InfoVO> accInfo = Acc_InfoSelect();
        Scanner sc = new Scanner(System.in);
        System.out.println("가입을 위해 회원 정보를 입력해주세요!");
        // 회원 정보 입력 시작
        // 유저 아이디
        String userId;
        while(true) {
            Common.Util ut = new Common.Util();

            System.out.print("아이디 : ");
            userId = sc.next();
            String check = userId;

            // 중복 체크; 스트림 객체로 변환한 뒤 메서드 체이닝으로 각각 체크. filter(), findAny(), orElse() 사용.
            if(accInfo.stream().filter(n -> check.equals(n.getUserId())).findAny().orElse(null) != null) {
                System.out.println("이미 사용중인 아이디 입니다.");
            }else if (!ut.checkInputNumAndAlphabet(userId)) System.out.println("영문과 숫자 조합만 사용해주세요.");
            else if (userId.length() < 5) System.out.println("ID는 5자 이상 입력해주세요");
            else if (userId.length() > 20) System.out.println("ID는 20자 이하로 입력해주세요");
            else break;
        }
        // 유저 비밀번호
        // 정규식, Pattern, Matcher 클래스 동원한다
        String userPw ;
        while(true) {
            System.out.print("비밀번호(8자 이상 20자 이하) : ");
            userPw = sc.next();

            // Pattern compile : 주어진 정규식들을 Pattern 객체로 컴파일 처리. 즉 합당한 비밀번호가 뭔지에 대한 규칙을 제시
            // ^ : 문자열의 시작
            // (?=.*[a-zA-Z]) = 최소 한 글자가 문자인가 체크
            // (?=.*\\d) : 최소 비밀번호 한자리가 0~9 사이 숫자인가 (if there is at least one digit (0-9))
            // (?=.*\\W) : 특수문자가 하나 포함되어 있는가 (e.g. !@#$%^&*)
            // .{8,20} : 비밀번호 문자열이 8~20자 사이의 문자인지
            // $ : 문자열의 끝
            Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
            // Matcher : userPw를 passPattern1 에 대조해서 검사하는 Matcher 객체를 생성
            // userPw가 검증되어야할 문자열 변수로 판단될 것이다.
            Matcher passMatcher1 = passPattern1.matcher(userPw);

            if(userPw.length() < 8) System.out.println("비밀번호는 8자 이상 입력해주세요");
            else if (userPw.getBytes().length > 20) System.out.println("비밀번호는 20자 이하 영문자와 &제외 특수문자로 입력해주세요");
            else if (!passMatcher1.find()) System.out.println("비밀번호는 영문자, 숫자, 특수기호의 조합으로만 사용 할 수 있습니다.");
            else if (userPw.indexOf('&') >= 0) System.out.println("&는 비밀번호로 사용할수 없습니다.");
            else break;
        }

        // 이름 입력
        System.out.print("이름 : ");
        String userName = sc.next();

        // 연락처 입력  - 13자리만 허용 (비워둘 수 없습니다 → 자동적용)
        String userPhone;
        while(true) {
            System.out.print("연락처 : ");
            userPhone = sc.next();
            String check = userPhone;
            //중복 체크; 스트림 객체로 변환한 뒤 메서드 체이닝으로 각각 체크. filter(), findAn(), orElse() 사용.
            if(accInfo.stream().filter(n -> check.equals(n.getUserPhone())).findAny().orElse(null) != null) {
                System.out.println("이미 사용중인 번호 입니다.");
            }
            else if (userPhone.length() != 13) System.out.print("전화번호는 (-)포함 13글자로 작성하세요.");
            else break;
        }

        // 필요 값들 전부 입력 완료시 ACC_INFO 테이블에 자료 추가
        String sql = "INSERT INTO ACC_INFO(USER_ID, USER_PW, USER_NAME, USER_PHONE, JOIN_DATE, AUTH_LV) VALUES (?, ?, ?, ?, SYSDATE, ?)";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, userId);
            psmt.setString(2, userPw);
            psmt.setString(3, userName);
            psmt.setString(4, userPhone);
            psmt.setInt(5, 3); // 새로 가입하는 소비자 유저는 무조건 3 처리.
            int rst = psmt.executeUpdate(); // INSERT, UPDATE, DELETE에 해당하는 함수
            System.out.println("INSERT 결과로 영향 받는 행의 갯수 : " + rst);

        } catch (Exception e) {
            System.out.println("INSERT 실패");
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
        System.out.println("회원가입이 완료되었습니다. 메인메뉴로 이동합니다.");
    }


    public static void accInfoSelectResult(List<Acc_InfoVO> list) {
        System.out.println("--------------------------------------------------------");
        System.out.println("                회원정보");
        System.out.println("--------------------------------------------------------");
        for(Acc_InfoVO e : list) {
            System.out.print(e.getUserId() + " ");
            System.out.print(e.getUserPw() + " ");
            System.out.print(e.getUserName() + " ");
            System.out.print(e.getUserPhone() + " ");
            System.out.print(e.getStoreId() + " ");
            System.out.print(e.getAuthLv() + " ");
            System.out.println();
        }
        System.out.println("--------------------------------------------------------");
    }

}