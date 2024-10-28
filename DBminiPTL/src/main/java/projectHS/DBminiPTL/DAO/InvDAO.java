package projectHS.DAO;

import projectHS.Common.Common;
import projectHS.Customer.SetMenu;
import projectHS.Customer.SingleMenu;
import projectHS.VO.InvVO;


import java.sql.*;
import java.util.*;

import static projectHS.DAO.Order_RecordDAO.orderRecordInsert;
import static projectHS.DAO.StoreDAO.salesPTp;


public class InvDAO {
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement psmt = null;
    ResultSet rs = null;
    Scanner sc = new Scanner(System.in);
    String storeId = null;

    List<SetMenu> setCart = new ArrayList<>();
    List<SingleMenu> singleCart = new ArrayList<>();

    public void choiceStore() { // 지점 설정 메서드
        List<String> lst = new ArrayList<>();
        int i = 1;
        int storeIdx;

        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT DISTINCT STORE_ID FROM INV";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                lst.add(rs.getString("STORE_ID"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.printf("%20s", "점포 목록\n");
        for (String e : lst) {
            System.out.printf("[%d] %s \n", i++, e);
        }
        System.out.println("주문 지점을 선택 해 주세요 : ");
        storeIdx = sc.nextInt() - 1;

        if(storeId != null && !this.storeId.equals(lst.get(storeIdx))){
            setCart.clear();
            singleCart.clear();
        }

        this.storeId = lst.get(storeIdx);
    }

    // 고객의 주문 관련 메서드
    public void cusOrder() {
        //List<InvVO> burger = new ArrayList<>();
        List<InvVO> burger = new ArrayList<>();
        List<InvVO> side = new ArrayList<>();
        List<InvVO> drink = new ArrayList<>();

        String query = "SELECT i.MENU_NAME, i.PRICE, o.DESCR, o.CATEGORY, i.STOCK FROM INV i JOIN INV_ORDER o " +
                "ON i.MENU_NAME = o.MENU_NAME WHERE STORE_ID = ?";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(query);
            psmt.setString(1, this.storeId);
            rs = psmt.executeQuery();

            while (rs.next()) {
                String menuName = rs.getString("MENU_NAME");
                int price = rs.getInt("PRICE");
                String descr = rs.getString("DESCR");
                String cat = rs.getString("CATEGORY");
                int stock = rs.getInt("STOCK");

                InvVO vo = new InvVO(menuName, price, descr, cat, stock);
                if (vo.getCategory().equals("버거")) {
                    burger.add(vo);
                } else if (vo.getCategory().equals("사이드")) {
                    side.add(vo);
                } else {
                    drink.add(vo);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        while (true) {
            int i = 1;
            System.out.print("주문할 메뉴의 분류를 선택 해 주세요 [1]세트, [2]단품, [3]사이드, [4]음료 [9] 주문종료 : ");
            int cat = sc.nextInt();
            sc.nextLine();

            switch (cat) {
                case 1:
                    System.out.println("-".repeat(40));
                    System.out.printf("%20s", "세트 메뉴 목록 \n");
                    for (InvVO e : burger) {
                        System.out.printf("[%d] %s 세트, %s의 세트메뉴, %d원 부터\n", i++, e.getMenuName(), e.getMenuName(), e.getPrice()); // 가격 설정 필요(버거가격+감튀+콜라가 기본가격)
                    }
                    System.out.print("메뉴를 선택 해 주세요 : ");
                    int b = sc.nextInt() - 1;
                    if (burger.size() < b || b < 0) {
                        System.out.println("해당 메뉴가 존재하지 않습니다.");
                        break;
                    }

                    i = 1;
                    System.out.println("-".repeat(40));
                    for (InvVO e : side) {
                        System.out.printf("[%d] %s, %s \n", i++, e.getMenuName(), e.getDescr());
                    }
                    System.out.print("사이드 선택 : ");
                    int s = sc.nextInt() - 1;
                    if (side.size() < s || s < 0) {
                        System.out.println("해당 메뉴가 존재하지 않습니다.");
                        break;
                    }

                    i = 1;
                    System.out.println("-".repeat(40));
                    for (InvVO e : drink) {
                        System.out.printf("[%d] %s, %s \n", i++, e.getMenuName(), e.getDescr());
                    }
                    System.out.print("음료 선택 : ");
                    int d = sc.nextInt() - 1;
                    if (drink.size() < d || d < 0) {
                        System.out.println("해당 메뉴가 존재하지 않습니다.");
                        break;
                    }

                    System.out.print("수량 선택 : "); // 0 이하의 수를 입력하면 주문 안되게 수정
                    int cnt = sc.nextInt();
                    sc.nextLine();

                    boolean isExist = false;
                    int idx = -1;
                    for (SetMenu setMenu : setCart) {
                        idx++;
                        if (setMenu.getBurger().getName().equals(burger.get(b).getMenuName()) &&
                                setMenu.getSide().getName().equals(side.get(s).getMenuName()) &&
                                setMenu.getDrink().getName().equals(drink.get(d).getMenuName())) {
                            isExist = true;
                            break;
                        }
                    }

                    if (isExist) {
                        int add = cnt + setCart.get(idx).getCount();
                        System.out.println("이미 동일한 세트 메뉴가 장바구니에 존재합니다.");

                        if (burger.get(b).getStock() < add || side.get(s).getStock() < add || drink.get(d).getStock() < add) {
                            System.out.printf("선택하신 메뉴의 재고가 부족합니다. \n %s : %d개, %s : %d개, %s : %d개\n",
                                    burger.get(b).getMenuName(), burger.get(b).getStock(),
                                    side.get(s).getMenuName(), side.get(s).getStock(),
                                    drink.get(d).getMenuName(), drink.get(d).getStock());
                            break;
                        } else {
                            System.out.println("해당 세트의 개수는 " + add + "개 입니다.");
                            setCart.get(idx).setCount(add);
                        }
                    } else if (burger.get(b).getStock() < cnt || side.get(s).getStock() < cnt || drink.get(d).getStock() < cnt) {
                        System.out.printf("선택하신 메뉴의 재고가 부족합니다. \n %s : %d개, %s : %d개, %s : %d개\n",
                                burger.get(b).getMenuName(), burger.get(b).getStock(),
                                side.get(s).getMenuName(), side.get(s).getStock(),
                                drink.get(d).getMenuName(), drink.get(d).getStock());
                    } else {
                        setCart.add(new SetMenu(
                                new SingleMenu(burger.get(b).getMenuName(), burger.get(b).getPrice()),
                                new SingleMenu(side.get(s).getMenuName(), side.get(s).getPrice()),
                                new SingleMenu(drink.get(d).getMenuName(), drink.get(d).getPrice()),
                                cnt
                        ));
                    }
                    break;
                case 2:
                    menuSelect(burger, "버거");
                    break;
                case 3:
                    menuSelect(side, "사이드");
                    break;
                case 4:
                    menuSelect(drink, "음료");
                    break;
                case 9:
                    System.out.println("메뉴 주문을 종료합니다.");
                    return;
                default:
                    System.out.println("메뉴 분류를 다시 선택 해 주세요.");
            }


        }

    }

    // 고객의 장바구니 내부 동작
    public void inCart(String userId) {
        int n;
        int change;
        List<InvVO> lst = new ArrayList<>();

        String sql = "SELECT MENU_NAME, STOCK FROM INV WHERE STORE_ID = ?";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, storeId);
            rs = psmt.executeQuery();

            while (rs.next()) {
                String menuName = rs.getString("MENU_NAME");
                int stock = rs.getInt("STOCK");

                lst.add(new InvVO(menuName, stock));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            int i = 1;
            for (SingleMenu e : singleCart) {
                System.out.printf("[%d] %s, %d개, %d원\n", i++, e.getName(), e.getCount(), e.getPrice() * e.getCount());
            }
            for (SetMenu e : setCart) {
                System.out.printf("[%d] %s 세트, %d개, %d원\n", i++, e.getBurger().getName(), e.getCount(), e.getPrice() * e.getCount());
                System.out.printf("     ㄴ 사이드 : %s\n", e.getSide().getName());
                System.out.printf("     ㄴ 음료 : %s\n", e.getDrink().getName());
            }

            System.out.println("=".repeat(20));
            System.out.print("[1]메뉴 수량 변경, [2]전체 취소, [3] 결제 [4] 이전화면: ");
            int sel = sc.nextInt();
            sc.nextLine();

            switch (sel) {
                case 1:
                    if (singleCart.isEmpty() && setCart.isEmpty()) {
                        System.out.println("장바구니가 비어있습니다.");
                        break;
                    }
                    System.out.print("수량을 변경할 메뉴를 선택 해 주세요 : ");
                    n = sc.nextInt() - 1;
                    sc.nextLine();

                    if (setCart.size() + singleCart.size() < n) {
                        System.out.println("해당 메뉴가 존재하지 않습니다.");
                        break;
                    }


                    if (n >= singleCart.size()) {
                        n = n - singleCart.size();
                        System.out.printf("%s 세트의 현재 수량 %d개 \n", setCart.get(n).getBurger().getName(), setCart.get(n).getCount());
                        System.out.print("증가, 감소시킬 개수를 입력 해 주세요 : ");
                        change = sc.nextInt();
                        sc.nextLine();

                        String[] setMenu = new String[]{setCart.get(n).getBurger().getName(),
                                setCart.get(n).getSide().getName(),
                                setCart.get(n).getDrink().getName()};


                        for (String e : setMenu) { // 재고 검색 후 추가할 시 그보다 많으면 break
                            for (InvVO f : lst) {
                                if (f.getMenuName().equals(e)) {
                                    if (f.getStock() < change + setCart.get(n).getCount()) {
                                        System.out.printf("%s의 재고는 %d개 입니다. 수량 변경이 불가능합니다.\n", e, f.getStock());
                                        break;
                                    }
                                }
                            }

                        }

                        if (change < 0 && -change >= setCart.get(n).getCount()) {   // 음수인지 체크하고 메뉴에서 제거하는 로직
                            System.out.println("메뉴를 제거하였습니다.");
                            setCart.remove(n);
                        } else {
                            setCart.get(n).changeCount(change);
                        }
                    } else {
                        System.out.printf("%s 의 현재 수량 %d개 \n", singleCart.get(n).getName(), singleCart.get(n).getCount());
                        System.out.print("증가, 감소시킬 개수를 입력 해 주세요 : ");
                        change = sc.nextInt();
                        sc.nextLine();

                        String menu = singleCart.get(n).getName();
                        for (InvVO e : lst) { // 재고 검색 후 추가할 시 그보다 많으면 break
                            if (e.getMenuName().equals(menu) && change + singleCart.get(n).getCount() > e.getStock()) {
                                System.out.printf("%s의 재고는 %d개 입니다.", e.getMenuName(), e.getStock());
                                break;
                            }
                        }

                        if (change >= singleCart.get(n).getCount()) {
                            System.out.println("메뉴를 제거하였습니다.");
                            singleCart.remove(n);
                        } else {
                            singleCart.get(n).changeCount(change);
                        }
                    }
                    break;
                case 2:
                    System.out.print("정말로 전체 취소 하시겠습니까?(y/n) : ");
                    String yn = sc.nextLine();
                    if (yn.equalsIgnoreCase("y")) {
                        System.out.println("장바구니를 비웁니다.");
                        singleCart.clear();
                        setCart.clear();
                        return;
                    } else {
                        System.out.println("취소하지 않습니다.");
                        break;
                    }
                case 3:
                    Map<String, Integer> map = new HashMap<>();
                    int totalPrice = 0;
                    for (SingleMenu e : singleCart) {
                        totalPrice += e.getPrice() * e.getCount();
                        System.out.println(e.getPrice() * e.getCount() + "원 추가, 총 가격 : " + totalPrice);

                        map.put(e.getName(), map.getOrDefault(e.getName(), 0) + e.getCount());
                    }
                    for (SetMenu e : setCart) {
                        totalPrice += e.getPrice() * e.getCount();
                        System.out.println(e.getPrice() * e.getCount() + "원 추가, 총 가격 :" + totalPrice);

                        map.put(e.getBurger().getName(), map.getOrDefault(e.getBurger().getName(), 0) + e.getCount());
                        map.put(e.getSide().getName(), map.getOrDefault(e.getSide().getName(), 0) + e.getCount());
                        map.put(e.getDrink().getName(), map.getOrDefault(e.getDrink().getName(), 0) + e.getCount());
                    }
                    boolean isStockCheck = true;

                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                        String menuName = entry.getKey();
                        int requiredCount = entry.getValue();

                        // 재고 확인
                        for (InvVO f : lst) {
                            if (f.getMenuName().equals(menuName)) {
                                if (f.getStock() < requiredCount) {
                                    System.out.printf("%s의 재고가 현재 %d개 남아있습니다. (필요 수량: %d개)\n",
                                            menuName, f.getStock(), requiredCount);
                                    isStockCheck = false; // 재고 부족 표시
                                }
                                break; // 메뉴를 찾았으므로 내부 루프 종료
                            }
                        }
                    }

                    if (!isStockCheck) {
                        break;
                    }

                    System.out.printf("총 가격 : %d원\n", totalPrice);

                    System.out.print("결제 하시겠습니까?(y/n)");
                    String pay = sc.nextLine();
                    if (pay.equals("y")) {
                        System.out.println("결제를 진행합니다.");
                        paymentUpdate();

                        // totalPrice 매출액으로 쏴주기
                        salesPTp(totalPrice, storeId);
                        // Order_RecordDAO의 메서드
                        orderRecordInsert(storeId, orderToString(setCart, singleCart), totalPrice, userId);
                        singleCart.clear();
                        setCart.clear();
                        return;
                    } else {
                        System.out.println("이전 화면으로 돌아갑니다.");
                        break;
                    }
                case 4:
                    System.out.println("이전 화면으로 돌아갑니다.");
                    return;
            }
        }
    }


    // 고객의 결제 시 재고 감소
    public void paymentUpdate() {
        String sqlSingle = "UPDATE INV SET STOCK = STOCK - ? WHERE STORE_ID = ? AND MENU_NAME = ?";
        String sqlSet = "UPDATE INV SET STOCK = STOCK - ? WHERE STORE_ID = ? AND MENU_NAME IN (?, ?, ?)";
        int tuple = 0;

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sqlSingle);
            for (SingleMenu e : singleCart) {
                psmt.setInt(1, e.getCount());
                psmt.setString(2, storeId);
                psmt.setString(3, e.getName());
                System.out.println(e.getName() + " : " + e.getCount() + "개 감소");
                tuple += psmt.executeUpdate();
            }
            psmt = conn.prepareStatement(sqlSet);
            for (SetMenu e : setCart) {
                psmt.setInt(1, e.getCount());
                psmt.setString(2, storeId);
                psmt.setString(3, e.getBurger().getName());
                psmt.setString(4, e.getSide().getName());
                psmt.setString(5, e.getDrink().getName());
                System.out.printf("%s, %s, %s : %d개씩 감소\n", e.getBurger().getName(), e.getSide().getName(), e.getDrink().getName(), e.getCount());
                tuple += psmt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error in paymentUpdate : " + e.getMessage());
        }
        System.out.println(tuple + "번의 액세스");
    }

    // 고객의 주문 내역을 문자열로 나열
    public String orderToString(List<SetMenu> set, List<SingleMenu> single) {
        StringBuilder sb = new StringBuilder();
        for (SetMenu e : set) {
            sb.append(e.getBurger().getName()).append(" 세트").append("/");
            sb.append(e.getCount()).append("개").append(",").append("  ㄴ");
            sb.append(e.getSide().getName()).append(",").append("  ㄴ");
            sb.append(e.getDrink().getName()).append("+");
        }

        for (SingleMenu e : single) {
            sb.append(e.getName()).append("/");
            sb.append(e.getCount()).append("개").append(",");
        }

        return sb.toString();
    }


    // 고객의 단품 메뉴 세트 주문 메서드
    public void menuSelect(List<InvVO> menu, String cat) {
        System.out.printf("%20s\n", cat + " 목록");
        int i = 1;
        for (InvVO e : menu) {
            System.out.printf("[%d] %s , %s, %d\n", i++, e.getMenuName(), e.getDescr(), e.getPrice());
        }
        System.out.print("메뉴를 선택 해 주세요 : ");
        int select = sc.nextInt() - 1;
        sc.nextLine();

        if (menu.size() < select || select < 0) {
            System.out.println("해당 메뉴가 존재하지 않습니다.");
            return;
        }

        System.out.print("수량을 선택 해 주세요 : ");
        int count = sc.nextInt();
        sc.nextLine();

        if (menu.get(select).getStock() < count) {
            System.out.println("해당 메뉴의 재고가 주문량보다 적습니다.");
            System.out.println(menu.get(select).getMenuName() + "의 재고는 " + menu.get(select).getStock() + "개 입니다.");
            return;
        }

        // 장바구니에 추가
        boolean isExist = singleCart.stream().anyMatch(singleMenu -> singleMenu.getName().equals(menu.get(select).getMenuName()));
        if (isExist) {
            for (SingleMenu e : singleCart) {
                if (e.getName().equals(menu.get(select).getMenuName())) {
                    if (e.getCount() + count > menu.get(select).getStock()) {
                        System.out.printf("해당 메뉴의 재고는 현재 %d개 입니다.", menu.get(select).getStock());
                        break;
                    }
                    e.changeCount(count); // 수량 증가
                    break;
                }
            }
        } else {
            singleCart.add(new SingleMenu(menu.get(select).getMenuName(), menu.get(select).getPrice(), count));
        }
    }

    // 점주의 발주를 실행
    public void ownerOrder(String storeId) {
        int capital = CapitalCheck(storeId);

        List<InvVO> vo = orderInvCheck();
        List<InvVO> burger = new ArrayList<>();
        List<InvVO> side = new ArrayList<>();
        List<InvVO> drink = new ArrayList<>();

        String sqlOrder = "UPDATE INV SET STOCK = STOCK + ? WHERE MENU_NAME = ? AND STORE_ID = ?";
        String sqlCapital = "UPDATE STORE SET CAPITAL = CAPITAL - ? WHERE STORE_ID = ?";

        for (InvVO e : vo) {
            switch (e.getCategory()) {
                case "버거":
                    burger.add(e);
                    break;
                case "사이드":
                    side.add(e);
                    break;
                case "음료":
                    drink.add(e);
                    break;
            }
        }

        while (true) {
            System.out.println("발주를 넣을 메뉴의 분류 선택 [1] 버거, [2] 사이드, [3] 음료, [9] 종료");
            int sel = sc.nextInt();
            sc.nextLine();

            List<InvVO> selectedCategory = null;
            switch (sel) {
                case 1:
                    selectedCategory = burger;
                    break;
                case 2:
                    selectedCategory = side;
                    break;
                case 3:
                    selectedCategory = drink;
                    break;
                case 9:
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 선택하세요.");
                    continue;
            }

            if (selectedCategory != null && !selectedCategory.isEmpty()) {
                displayMenu(selectedCategory);

                System.out.print("발주할 메뉴를 선택하세요. : ");
                int idx = sc.nextInt() - 1;
                sc.nextLine();
                System.out.print("메뉴의 수량을 입력하세요 : ");
                int cnt = sc.nextInt();
                sc.nextLine();

                if (cnt <= 0) {
                    System.out.println("0개 이하의 주문을 할 수 없습니다.");
                    break;
                } else if (selectedCategory.get(idx).getPrice() * cnt <= capital) {
                    // 발주품 재고 증가와 매장계좌 잔액의 감소를 한 트랜잭션으로 묶음
                    try (Connection conn = Common.getConnection()) {
                        conn.setAutoCommit(false);

                        updateInventory(conn, sqlOrder, cnt, selectedCategory.get(idx).getMenuName(), storeId);
                        updateCapital(conn, sqlCapital, cnt * selectedCategory.get(idx).getPrice(), storeId);


                        conn.commit();
                        System.out.println("주문이 정상적으로 완료되었습니다.");
                    } catch (SQLException e) {
                        System.out.println("Error during update: " + e.getMessage());
                        try {
                            if (conn != null) {
                                conn.rollback();
                                System.out.println("Changes rolled back.");
                            }
                        } catch (SQLException rollbackEx) {
                            System.out.println(rollbackEx.getMessage());
                        }
                    }
                } else {
                    System.out.println("가용액이 부족합니다.");
                }
            } else {
                System.out.println("선택한 카테고리에 메뉴가 없습니다.");
            }
        }
    }

    // 점주의 발주 시 메뉴 출력
    private void displayMenu(List<InvVO> items) {
        int i = 1;
        for (InvVO e : items) {
            System.out.printf("[%d] %s %d원\n", i++, e.getMenuName(), e.getPrice());
        }
    }

    // 해당 지점의 재고 업데이트
    private void updateInventory(Connection conn, String sqlOrder, int cnt, String menuName, String storeId) {
        try (PreparedStatement psmt = conn.prepareStatement(sqlOrder)) {
            psmt.setInt(1, cnt);
            psmt.setString(2, menuName);
            psmt.setString(3, storeId);
            psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // 해당 지점의 가용금 감소
    private void updateCapital(Connection conn, String sqlCapital, int amount, String storeId) {
        try (PreparedStatement psmt = conn.prepareStatement(sqlCapital)) {
            psmt.setInt(1, amount);
            psmt.setString(2, storeId);
            psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // 점주의 잔고 확인(STORE 테이블 DAO로 넘겨야 하나?)
    public int CapitalCheck(String storeId) {
        String sqlCap = "SELECT CAPITAL FROM STORE WHERE STORE_ID = ?";
        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sqlCap);
            psmt.setString(1, storeId);
            rs = psmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("CAPITAL");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    // 점주의 발주에 쓰이는 메서드 (INV_ORDER 테이블 DAO로 넘겨야 하나?)
    public List<InvVO> orderInvCheck() {
        List<InvVO> vo = new ArrayList<>();
        String sqlInv = "SELECT MENU_NAME, PRICE, CATEGORY FROM INV_ORDER";

        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlInv);

            while (rs.next()) {
                String name = rs.getString("MENU_NAME");
                int price = rs.getInt("PRICE");
                String cat = rs.getString("CATEGORY");

                vo.add(new InvVO(name, cat, price));
            }
            return vo;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // 점주의 재고 확인 메서드
    public void invCheck(String storeId) {
        List<InvVO> vo = new ArrayList<>();

        // SQL query
        String sql = "SELECT i.MENU_NAME, i.STOCK, o.CATEGORY FROM INV i JOIN INV_ORDER o " +
                "ON i.MENU_NAME = o.MENU_NAME WHERE STORE_ID = ?";

        try {
            // Debug output to check the storeId value
            System.out.println("Current storeId: " + storeId);

            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, storeId); // Bind storeId value
            rs = psmt.executeQuery();

            // Fetching inventory data
            while (rs.next()) {
                String menuName = rs.getString("MENU_NAME");
                int stock = rs.getInt("STOCK");
                String category = rs.getString("CATEGORY");
                vo.add(new InvVO(menuName, stock, category));
            }

            // Debug output to check list size
            System.out.println("Inventory List Size: " + vo.size());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            Common.close(rs);
            Common.close(psmt);
            Common.close(conn);
        }

        // Menu selection logic remains the same
        while (true) {
            System.out.print("재고를 확인할 메뉴의 분류 선택 [1]버거, [2]사이드, [3]음료 [9]뒤로가기 : ");
            int sel = sc.nextInt();
            sc.nextLine();
            switch (sel) {
                case 1:
                    for (InvVO e : vo) {
                        if (e.getCategory().equals("버거")) {
                            System.out.println(e.getMenuName() + " : " + e.getStock() + "개");
                        }
                    }
                    break;
                case 2:
                    for (InvVO e : vo) {
                        if (e.getCategory().equals("사이드")) {
                            System.out.println(e.getMenuName() + " : " + e.getStock() + "개");
                        }
                    }
                    break;
                case 3:
                    for (InvVO e : vo) {
                        if (e.getCategory().equals("음료")) {
                            System.out.println(e.getMenuName() + " : " + e.getStock() + "개");
                        }
                    }
                    break;
                case 9:
                    return;
            }
        }
    }


}