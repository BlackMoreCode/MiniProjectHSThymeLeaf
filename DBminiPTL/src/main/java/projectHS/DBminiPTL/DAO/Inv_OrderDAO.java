package projectHS.DAO;

import projectHS.Common.Common;
import projectHS.HSMain;
import projectHS.VO.Inv_OrderVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Inv_OrderDAO {


    // 기본 인스턴스필드
    // Connection : 자바와 오라클 DB 연결 설정
    // PreparedStatement : SQL문 수행 객체
    // ResultSet : PreparedStatement 동작에 대한 결과로 전달되는 DB 내용
    static Connection conn = null;
    static Statement stmt = null;
    static PreparedStatement pstmt = null;
    static ResultSet rs = null;

    static Scanner sc = new Scanner(System.in);
    boolean isSuccess = false;


    // 메뉴 선택
    public void runInv_Order() {

        while (true) {

            System.out.println("==== HQ STOCK ADJUSTMENT ====");
            System.out.print("[1]메뉴조회 [2]메뉴추가 [3]메뉴수정 [4]메뉴삭제 [5]뒤로가기 [9]종료 : ");
            int sel = sc.nextInt();

            switch (sel) {
                case 1 :
                    List<Inv_OrderVO> list = Inv_OrderSelect();
                    Inv_OrderSelectResult(list);
                    break;
                case 2 :
                    isSuccess = Inv_OrderInsert();
                    if (isSuccess) System.out.println("메뉴 등록 성공");
                    else System.out.println("메뉴 등록 실패");
                    break;
                case 3 :
                    isSuccess = Inv_OrderUpdate();
                    if (isSuccess) System.out.println("메뉴 수정 성공");
                    else System.out.println("메뉴 수정 실패");
                    break;
                case 4 :
                    isSuccess = Inv_OrderDelete();
                    if (isSuccess) System.out.println("메뉴 삭제 성공");
                    else System.out.println("메뉴 삭제 실패");
                    break;
                case 5:
                    HSMain.hqMenu();
                    break;
                case 9 :
                    System.out.println("프로그램을 종료합니다.");
                    return;

            }

        }

    }

    // 메뉴 확인 (Select)
    // 로우데이터를 받아내기 위해 ArrayList 생성
    // Inv_Order테이블과 똑같이 만들어둔 Inv_OrderVO 클래스
    public static List<Inv_OrderVO> Inv_OrderSelect() {
        List<Inv_OrderVO> list = new ArrayList<>();

        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();

            String query = "SELECT * FROM INV_ORDER";

            // 실행 결과 받기
            // 결과값이 여러 개의 레코드로 반환되어 executeQuery 사용
            rs = stmt.executeQuery(query);

            // 읽을 게 있으면 true
            // DB에 있는 내용을 rs가 받아 옴
            while (rs.next()) {
                String menuName = rs.getString("MENU_NAME");
                int price = rs.getInt("PRICE");
                String category = rs.getString("CATEGORY");
                String descr = rs.getString("DESCR");

                // Inv_OrderVO 객체를 만들어서 생성자 불러오기
                // 하나로 모아서 리스트에 담기
                Inv_OrderVO vo = new Inv_OrderVO(menuName, price, category, descr);
                list.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);
            return list;
        }
    }

    public static void Inv_OrderSelectResult(List<Inv_OrderVO> list) {
        System.out.println("==== HQ STOCK INFO ====");

        for (Inv_OrderVO e : list) {
            System.out.print(e.getMenuName() + " ");
            System.out.print(e.getPrice() + " ");
            System.out.print(e.getCategory() + " ");
            System.out.print(e.getDescr());
            System.out.println();
        }
    }


    // 메뉴 추가 (Insert)
    public static boolean Inv_OrderInsert() {

        System.out.println("추가하실 메뉴 정보를 입력하세요.");
        System.out.print("메뉴 이름 : ");
        String menuName = sc.next();
        System.out.print("메뉴 가격 : ");
        int price = sc.nextInt();
        System.out.print("카테고리(버거, 사이드, 음료) : ");
        String category = sc.next();
        System.out.print("메뉴 설명 : ");
        String descr = sc.next();

        String sql = "INSERT INTO INV_ORDER(MENU_NAME, PRICE, CATEGORY, DESCR) VALUES (?, ?, ?, ?)";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, menuName);
            pstmt.setInt(2, price);
            pstmt.setString(3, category);
            pstmt.setString(4, descr);

            // INSERT 해당 함수
            pstmt.executeUpdate();

            return true;

            // 예외 처리
        } catch (Exception e) {
            e.printStackTrace();
            return false;

            // 무조건 불려짐
        } finally {
            Common.close(pstmt);
            Common.close(conn);
        }
    }

    // 메뉴 수정 (Update)
    public static boolean Inv_OrderUpdate() {

        System.out.println("수정하실 메뉴 정보를 입력하세요, 이름은 수정할 수 없습니다.");
        System.out.print("메뉴 이름 : ");
        String menuName = sc.next();
        System.out.print("변경할 가격 : ");
        int price = sc.nextInt();
        System.out.print("변경할 카테고리(버거, 사이드, 음료) : ");
        String category = sc.next();
        System.out.print("변경할 설명 : ");
        String descr = sc.next();

        String sql = "UPDATE INV_ORDER SET PRICE = ?, CATEGORY = ?, DESCR = ? WHERE MENU_NAME = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, price);
            pstmt.setString(2, category);
            pstmt.setString(3, descr);
            pstmt.setString(4, menuName);

            // UPDATE 해당 함수
            pstmt.executeUpdate();

            return true;

            // 예외 처리
        } catch (Exception e) {
            e.printStackTrace();
            return false;

            // 무조건 불려짐
        } finally {
            Common.close(pstmt);
            Common.close(conn);
        }
    }

    // 메뉴 삭제 (Delete)
    public static boolean Inv_OrderDelete() {

        System.out.println("삭제하실 메뉴 정보를 입력하세요.");
        System.out.print("삭제할 메뉴 이름 : ");
        String menuName = sc.next();

        String sql = "DELETE FROM INV_ORDER WHERE MENU_NAME = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, menuName);

            // DELETE 해당 함수
            pstmt.executeUpdate();

            return true;

            // 예외 처리
        } catch (Exception e) {
            e.printStackTrace();
            return false;

            // 무조건 불려짐
        } finally {
            Common.close(pstmt);
            Common.close(conn);
        }
    }


}
