package projectHS.DBminiPTL.DAO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import projectHS.DBminiPTL.Common.Common;
import projectHS.DBminiPTL.Customer.SetMenu;
import projectHS.DBminiPTL.Customer.SingleMenu;
import projectHS.DBminiPTL.VO.InvVO;


import java.sql.*;
import java.text.NumberFormat;
import java.util.*;


@Repository
public class InvDAO {

    private final JdbcTemplate jdbcTemplate;

    public InvDAO(JdbcTemplate jt) {
        this.jdbcTemplate = jt;
    }

    @Getter
    @Setter
    String storeId = null;


    // 메뉴를 장바구니에 담기 위함

    static List<SetMenu> setCart = new ArrayList<>();
    static List<SingleMenu> singleCart = new ArrayList<>();
    static List<SingleMenu> storeCart = new ArrayList<>();

    public List<SetMenu> getSetCart() {
        return setCart;
    }

    public List<SingleMenu> getSingleCart() {
        return singleCart;
    }

    public List<SingleMenu> getStoreCart() {
        return storeCart;
    }


    public static class storeRowMapper implements RowMapper<String> {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("STORE_ID");
        }
    }

    public List<String> showStores() {
        String sql = "SELECT DISTINCT STORE_ID FROM INV";
        return jdbcTemplate.query(sql, new storeRowMapper());
    }


    public void clearCart() {
        setCart.clear();
        singleCart.clear();
    }

    public static class menuRowMapper implements RowMapper<InvVO> {
        @Override
        public InvVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new InvVO(
                    rs.getString("MENU_NAME"),
                    rs.getInt("PRICE"),
                    rs.getString("DESCR"),
                    rs.getString("CATEGORY"),
                    rs.getInt("STOCK")
            );
        }
    }


    public List<InvVO> getAllMenus(String storeId) {
        String sql = "SELECT i.MENU_NAME, i.PRICE, o.DESCR, o.CATEGORY, i.STOCK FROM INV i JOIN INV_ORDER o " +
                "ON i.MENU_NAME = o.MENU_NAME WHERE STORE_ID = ? AND STOCK > 0";

        System.out.println("가게 아이디 : " + storeId);

        try {
            return jdbcTemplate.query(sql, new Object[]{storeId}, new menuRowMapper());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void addStoreCart(SingleMenu menu) {
        boolean isExist = storeCart.stream().anyMatch(singleMenu -> singleMenu.getName().equals(menu.getName()));

        if (isExist) {
            for (SingleMenu e : storeCart) {
                if (e.getName().equals(menu.getName())) {
                    e.changeCount(menu.getCount());
                }
            }
        } else {
            storeCart.add(menu);
        }

    }


    public void addSingleMenu(SingleMenu menu) {
        boolean isExist = singleCart.stream().anyMatch(singleMenu -> singleMenu.getName().equals(menu.getName()));

        if (isExist) {
            for (SingleMenu e : singleCart) {
                if (e.getName().equals(menu.getName())) {
                    e.changeCount(menu.getCount());
                }
            }
        } else {
            singleCart.add(menu);
        }

    }

    public void addSetMenu(SetMenu menu) {
        boolean isExist = false;

        for (SetMenu e : setCart) {
            if (e.getBurger().getName().equals(menu.getBurger().getName()) &&
                    e.getSide().getName().equals(menu.getSide().getName()) &&
                    e.getDrink().getName().equals(menu.getDrink().getName())) {
                isExist = true;
                e.changeCount(menu.getCount());
                break;
            }
        }

        if (!isExist) {
            setCart.add(menu);
        }
    }

    // 현재 장바구니에 담긴 메뉴와 수량을 반환하는 메서드(세트->단품 분배)
    public List<InvVO> stockList() {
        List<InvVO> cart = new ArrayList<>();

        // 단일 메뉴 처리
        for (SingleMenu e : singleCart) {
            cart.add(new InvVO(e.getName(), e.getCount()));
        }

        // 세트 메뉴 처리
        for (SetMenu s : setCart) {
            // 버거 추가
            addOrUpdateCart(cart, s.getBurger().getName(), s.getCount());
            // 사이드 추가
            addOrUpdateCart(cart, s.getSide().getName(), s.getCount());
            // 음료 추가
            addOrUpdateCart(cart, s.getDrink().getName(), s.getCount());
        }

        return cart;
    }

    private void addOrUpdateCart(List<InvVO> cart, String menuName, int count) {
        for (InvVO item : cart) {
            if (item.getMenuName().equals(menuName)) {
                item.setStock(item.getStock() + count);
                return; // 이미 존재하는 경우 업데이트 후 종료
            }
        }
        // 메뉴 이름이 cart에 없는 경우 새로 추가
        cart.add(new InvVO(menuName, count));
    }


    public static class stockMapper implements RowMapper<InvVO> {
        @Override
        public InvVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new InvVO(
                    rs.getString("MENU_NAME"),
                    rs.getInt("STOCK"),
                    rs.getString("CATEGORY")
            );
        }
    }

    public static class cusStockMapper implements RowMapper<InvVO> {
        @Override
        public InvVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new InvVO(
                    rs.getString("MENU_NAME"),
                    rs.getInt("STOCK")
            );
        }
    }


    // 고객의 결제 시 재고 체크
    public String customerStockCheck(String storeId) {
        String sql = "SELECT MENU_NAME, STOCK FROM INV WHERE STORE_ID = ?";
        List<InvVO> stock = new ArrayList<>();
        List<InvVO> cart = stockList(); // 장바구니의 내용

        try {
            stock = jdbcTemplate.query(sql, new Object[]{storeId}, new cusStockMapper());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // 재고를 Map으로 변환하여 빠른 검색 가능
        Map<String, Integer> stockMap = new HashMap<>();
        for (InvVO s : stock) {
            stockMap.put(s.getMenuName(), s.getStock());
        }

        // 장바구니의 각 항목과 재고 비교
        for (InvVO c : cart) {
            Integer availableStock = stockMap.get(c.getMenuName());
            if (availableStock != null && c.getStock() > availableStock) {
                return c.getMenuName() + "의 수량을 " + availableStock + "개 이하로 주문해주세요.";
            }
        }

        return null; // 재고가 충분한 경우 null 반환
    }

    public void updateSetMenu(String burger, String side, String drink, int cnt) {
        for (SetMenu e : setCart) {
            if (e.getBurger().getName().equals(burger) && e.getSide().getName().equals(side) && e.getDrink().getName().equals(drink)) {
                e.setCount(cnt);
                break;
            }
        }
    }

    public void removeSetMenu(String burger, String side, String drink) {
        setCart.removeIf(setMenu ->
                setMenu.getBurger().getName().equals(burger) &&
                        setMenu.getSide().getName().equals(side) &&
                        setMenu.getDrink().getName().equals(drink));
    }

    public void updateSingleMenu(String menuName, int cnt) {
        for (SingleMenu e : singleCart) {
            if (e.getName().equals(menuName)) {
                System.out.println(e.getName() + " : " + menuName);
                e.setCount(cnt);
            }
        }
    }

    public void updateStoreCart(String menuName, int cnt) {
        for (SingleMenu e : storeCart) {
            if (e.getName().equals(menuName)) {
                System.out.println(e.getName() + " : " + menuName);
                e.setCount(cnt);
            }
        }
    }

    public void removeSingleMenu(String menuName) {
        singleCart.removeIf(singleMenu -> singleMenu.getName().equals(menuName));
    }

    public void removeStoreCart(String menuName) {
        storeCart.removeIf(singleMenu -> singleMenu.getName().equals(menuName));
    }

    // 고객 결제 시 재고 감소
    @Transactional
    public void checkoutStockUpdate(String storeId) {
        List<InvVO> cart = stockList();

        String sql = "UPDATE INV SET STOCK = STOCK - ? WHERE STORE_ID = ? AND MENU_NAME = ?";
        String sql2 = "UPDATE STORE SET SALES = SALES + ? WHERE STORE_ID = ?";

        // 총 금액 계산
        int totalPrice = getTotalPrice();

        // 재고 감소 및 매출 증가 처리
        try {
            // 재고 업데이트
            for (InvVO e : cart) {
                int rowsAffected = jdbcTemplate.update(sql, e.getStock(), storeId, e.getMenuName());
                if (rowsAffected == 0) {
                    // 재고 감소에 실패한 경우 롤백
                    throw new RuntimeException("재고 감소 실패: " + e.getMenuName());
                }
                System.out.println(e.getMenuName() + " : " + e.getStock() + "개 감소");
            }

            // 매출 증가 업데이트
            int salesAffected = jdbcTemplate.update(sql2, totalPrice, storeId);
            if (salesAffected == 0) {
                // 매출 증가에 실패한 경우 롤백
                throw new RuntimeException("매출 증가 실패: " + storeId);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;  // 예외를 다시 던져 트랜잭션 롤백
        }

    }

    // 장바구니의 총 금액 계산
    public int getTotalPrice() {
        int totalPrice = 0;
        for (SetMenu setMenu : setCart) {
            totalPrice += setMenu.getPrice() * setMenu.getCount();
        }
        for (SingleMenu singleMenu : singleCart) {
            totalPrice += singleMenu.getPrice() * singleMenu.getCount();
        }

        return totalPrice;
    }

    public int getTotalPriceStore() {
        int totalPrice = 0;
        for (SingleMenu e : storeCart) {
            totalPrice += e.getPrice() * e.getCount();
        }

        return totalPrice;
    }


    // 고객의 주문 내역을 문자열로 나열
    public String orderToString() {
        StringBuilder sb = new StringBuilder();
        for (SetMenu e : setCart) {
            sb.append(e.getBurger().getName()).append(" 세트").append("/");
            sb.append(e.getCount()).append("개").append(",").append("  ㄴ");
            sb.append(e.getSide().getName()).append(",").append("  ㄴ");
            sb.append(e.getDrink().getName()).append("+");
        }

        for (SingleMenu e : singleCart) {
            sb.append(e.getName()).append("/");
            sb.append(e.getCount()).append("개").append(",");
        }

        return sb.toString();
    }

    public List<InvVO> stockCheck(String storeId) {
        String sql = "SELECT i.MENU_NAME, i.STOCK, o.CATEGORY FROM INV i JOIN INV_ORDER o " +
                "ON i.MENU_NAME = o.MENU_NAME WHERE STORE_ID = ?";

        try {
            return jdbcTemplate.query(sql, new Object[]{storeId}, new stockMapper());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    // 매장의 재고 업데이트
    public void updateInventory(String storeId) {

        String sql = "UPDATE INV SET STOCK = STOCK + ? WHERE MENU_NAME = ? AND STORE_ID = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SingleMenu menu = storeCart.get(i);
                ps.setInt(1, menu.getCount());
                ps.setString(2, menu.getName());
                ps.setString(3, storeId);
            }

            @Override
            public int getBatchSize() {
                return storeCart.size();
            }
        });
    }

    public List<String> isMenuExist(String storeId) {
        String sql = "SELECT MENU_NAME FROM INV WHERE STORE_ID = ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{storeId}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("menu_name"); // 원하는 컬럼 이름으로 수정
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void addInventory(String storeId, List<SingleMenu> newMenu) {
        String sql = "INSERT INTO INV VALUES (?,?,? + 1000,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SingleMenu menu = newMenu.get(i);
                ps.setString(1, menu.getName());
                ps.setString(2, storeId);
                ps.setInt(3, menu.getPrice());
                ps.setInt(4, menu.getCount());

            }

            @Override
            public int getBatchSize() {
                return newMenu.size();
            }
        });

    }

    // 해당 지점의 가용금 감소
    public void updateCapital(String storeId, int amount) {
        String sql = "UPDATE STORE SET CAPITAL = CAPITAL - ? WHERE STORE_ID = ?";

        jdbcTemplate.update(sql, amount, storeId);
    }


    public int capitalCheck(String storeId) {
        String sql = "SELECT CAPITAL FROM STORE WHERE STORE_ID = ?";

        try {
            Integer capital = jdbcTemplate.queryForObject(sql, new Object[]{storeId}, Integer.class);
            return capital != null ? capital : 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int salesCheck(String storeId) {
        String sql = "SELECT SALES FROM STORE WHERE STORE_ID = ?";

        try {
            Integer sales = jdbcTemplate.queryForObject(sql, new Object[]{storeId}, Integer.class);
            return sales != null ? sales : 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }


    public static class hqProductMapper implements RowMapper<InvVO> {
        @Override
        public InvVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new InvVO(
                    rs.getString("MENU_NAME"),
                    rs.getString("CATEGORY"),
                    rs.getInt("PRICE")
            );
        }
    }

    // 본사의 메뉴들을 가져오는 메서드
    public List<InvVO> productHQ() {
        String sql = "SELECT MENU_NAME, PRICE, CATEGORY FROM INV_ORDER";

        try {
            return jdbcTemplate.query(sql, new hqProductMapper());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Transactional
    public void storeCartClear() {
        storeCart.clear();
    }

    public String formatAmount(int amount) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA); // 한국 로케일 설정
        return numberFormat.format(amount);
    }

}