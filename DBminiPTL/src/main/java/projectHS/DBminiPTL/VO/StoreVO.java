package projectHS.DBminiPTL.VO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class StoreVO {
    private String storeId; // 지점이름
    private BigDecimal sales; // 매출 현황
    private int capital; // 지점 가용 계좌(자본금)
    private BigDecimal amount;

    public StoreVO(String storeId, int capital) {
        this.storeId = storeId;
        this.capital = capital;
    }

    public StoreVO(BigDecimal sales) {
        this.sales = sales;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
