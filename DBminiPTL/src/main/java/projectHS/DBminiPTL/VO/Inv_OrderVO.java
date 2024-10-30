package projectHS.DBminiPTL.VO;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Inv_OrderVO {
    private String menuName;
    private int price;
    private String category;
    private String descr;
    private boolean editMode;

    // 생성자
    public Inv_OrderVO(String menuName, int price, String category, String descr) {
        this.menuName = menuName;
        this.price = price;
        this.category = category;
        this.descr = descr;
    }

    // 빈 생성자
    public Inv_OrderVO() {
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    // Getter
    public String getMenuName() {
        return menuName;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getDescr() {
        return descr;
    }


    // Setter
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}