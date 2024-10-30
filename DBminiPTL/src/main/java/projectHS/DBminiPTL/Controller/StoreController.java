package projectHS.DBminiPTL.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projectHS.DBminiPTL.DAO.StoreDAO;
import projectHS.DBminiPTL.VO.StoreVO;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Controller
@RequestMapping("/main/admin")
public class StoreController { // http://localhost:8112/store
    private final StoreDAO storeDAO;

    public StoreController(StoreDAO storeDAO) {
        this.storeDAO = storeDAO;
    }

    /*
    @GetMapping // ADMIN MAIN 페이지
    public String storeMainView(Model model) {
        model.addAttribute("stVO", new StoreVO());
        return "thymeleaf/storeAdmin";
    }*/

    @GetMapping("/sales") // 매출 현황
    public String storeAdminSales(Model model) {
        BigDecimal sales = storeDAO.slSelect("역삼점"); // 매출 데이터를 가져오는 메서드 호출
        // NumberFormat 클래스는 숫자를 특정 형식으로 포맷팅, (Locale.KOREA) 한국 지역 설정
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        String formattedSales = numberFormat.format(sales);
        model.addAttribute("sales", formattedSales); // 매출 데이터를 모델에 추가
        return "thymeleaf/storeAdminSales";
    }

    @GetMapping("/account") // 계좌 메뉴 Main 페이지
    public String storeAccountView(Model model) {
        return "thymeleaf/storeAdminAccount";
    }

    @GetMapping("/account/cpCharge") // 계좌 메뉴 Main 페이지
    public String storeAdminCpChargeView(Model model) {
        model.addAttribute("capitalSc", new StoreVO());
        return "thymeleaf/storeAdminAcctCharge";
    }

    @PostMapping("/account/cpCharge") // capitalCharge
    public String storeAdminCpCharge(@ModelAttribute("capitalSc") StoreVO capitalSc, Model model) {
        BigDecimal amount = capitalSc.getAmount();
        BigDecimal newCapital = storeDAO.cpCharge(amount, "역삼점");
        boolean isSuccess = (newCapital != null); // 또는 다른 성공 조건
        model.addAttribute("isSuccess", isSuccess);
        model.addAttribute("newCapital", newCapital);
        return "thymeleaf/storeAdminAcctChargeRs";
    }


    @GetMapping("/account/cpSearch") // capitalSearch
    public String storeAdminCpSearch(Model model) {
        BigDecimal capitalSc = storeDAO.cpSelect("역삼점"); // capitalSearch
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        String formattedCapital = numberFormat.format(capitalSc);
        model.addAttribute("capitalSc", formattedCapital);
        return "thymeleaf/storeAdminAcctSearch";
    }



//    @GetMapping("/capital") // http://localhost:8112/store/capital
//    public ResponseEntity<StoreVO> getCapital(@RequestParam String storeId) {
//        List<StoreVO> capital = storeDAO.cpSelect(storeDAO.getCpSStoreId(storeId));
//        if (capital.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        return ResponseEntity.ok(capital.get(0));
//    }

//    @GetMapping("/sales") // http://localhost:8112/store/sales
//    public ResponseEntity<StoreVO> getSales(@RequestParam String storeId) {
//        List<StoreVO> sales = storeDAO.slSelect(storeDAO.getSlStoreId(storeId));
//        if (sales.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        return ResponseEntity.ok(sales.get(0));
//    }
}