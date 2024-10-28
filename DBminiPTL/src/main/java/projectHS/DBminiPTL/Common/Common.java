package projectHS.DBminiPTL.Common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Common {
    // DB 연결 정보 생성
    final static String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    final static String ORACLE_ID = "HAMBURGERSHOP";
    final static String ORACLE_PW = "khacademy";
    final static String ORACLE_DRV = "oracle.jdbc.driver.OracleDriver";

    // 오라클 드라이버 로딩 및 연결
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(ORACLE_DRV);
            conn = DriverManager.getConnection(ORACLE_URL, ORACLE_ID, ORACLE_PW);
        } catch (Exception e) {
            System.out.println(e + "오라클 DB 연결 실패, 에러 발생");
        }
        return conn;
    }
    public static void close(Connection conn) {
        try {
            if (conn != null & !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("Connection 해제 실패");
        }
    }
    public static void close(Statement stmt) {
        try {
            if(stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet rset) {
        try {
            if(rset != null && !rset.isClosed()) {
                rset.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void commit(Connection conn) {
        try {
            if(conn != null && !conn.isClosed()) {
                conn.commit();
                System.out.println("커밋 완료");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rollback(Connection conn) {
        try {
            if(conn != null && !conn.isClosed()) {
                conn.rollback();
                System.out.println("롤백 완료");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Util {
        public boolean checkInputNumAndAlphabet(String userId) {

            char chrInput;

            for (int i = 0; i < userId.length(); i++) {
                chrInput = userId.charAt(i); // 입력받은 텍스트에서 문자 하나하나 가져와서 체크
                if (chrInput >= 0x61 && chrInput <= 0x7A) {
                    // 영문(소문자) OK!
                }
                else if (chrInput >=0x41 && chrInput <= 0x5A) {
                    // 영문(대문자) OK!
                }
                else if (chrInput >= 0x30 && chrInput <= 0x39) {
                    // 숫자 OK!
                }
                else {
                    return false;   // 영문자도 아니고 숫자도 아니다 = false 반환
                }

            }
            return true;
        }
        // 입력된 값이 정수로만 이루어져 있는지 확인하는 메소드
        public boolean isInteger(String strValue) {
            try {
                Integer.parseInt(strValue);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }
}