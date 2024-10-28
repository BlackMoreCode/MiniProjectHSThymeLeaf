package projectHS.VO;
// 생성자를 원래 (String storeId, int sales), (String storeId, int capital) 두개를 생성하고 싶은데
// 컴파일이 String, int의 나열을 정확하게 인식을 못하는지 계속 already defined 오류가 떠서
// GPT한테 죽도록 물어봐서 '정적 팩토리 메소드'로 설정
// 팩토리 메소드는 생성자 오버로딩으로 인한 모호함을 방지하고 명확하게 상태 설정 가능
// 아래 withSales, withCapital 참고
// 객체 생성시에는 StoreVO vo = new StoreVO(); 형태가 아니고
// StoreVO vo = StorevO.withSales(storeId, sales); 형태로 사용

public class StoreVO {
    private String storeId; // 지점이름
    private int sales; // 매출 현황
    private int capital; // 지점 가용 계좌(자본금)

    public StoreVO(String storeId,int sales,int capital) {
        this.storeId = storeId;
        this.sales = sales;
        this.capital = capital;
    }

    public static StoreVO withSales (String storeId, int sales) {
        return new StoreVO(storeId, sales, 0);
    }

    public static StoreVO withCapital (String storeId, int capital) {
        return new StoreVO(storeId, 0, capital);
    }

    public StoreVO(int sales) {
        this.sales = sales;
    }

    public String getStoreId() {
        return storeId;
    }

    public int getSales() {
        return sales;
    }

    public int getCapital() {
        return capital;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public void setCapital(int capital) {
        this.capital = capital;
    }
}
