package projectHS.DBminiPTL.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projectHS.DBminiPTL.Common.Session;
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

//    @GetMapping // ADMIN MAIN 페이지
//    public String storeMainView(Model model) {
//        model.addAttribute("stVO", new StoreVO());
//        return "thymeleaf/storeAdmin";
//    }

    @GetMapping("/sales") // 매출 현황
    public String storeAdminSales(Model model) {
        BigDecimal sales = storeDAO.slSelect(Session.loggedInUserId ); // 매출 데이터를 가져오는 메서드 호출
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
        model.addAttribute("capitalCg", new StoreVO());
        return "thymeleaf/storeAdminAcctCharge";
    }

    @PostMapping("/account/cpCharge") // capitalCharge
    public String storeAdminCpCharge(@ModelAttribute("capitalCg") StoreVO capitalSc, Model model) {
        BigDecimal amount = capitalSc.getAmount();
        BigDecimal newCapital = storeDAO.cpCharge(amount, Session.loggedInUserId);
        boolean isSuccess = (newCapital != null); // 또는 다른 성공 조건
        model.addAttribute("isSuccess", isSuccess);
        model.addAttribute("newCapital", newCapital);
        return "thymeleaf/storeAdminAcctChargeRs";
    }


    @GetMapping("/account/cpSearch") // capitalSearch
    public String storeAdminCpSearch(Model model) {
        BigDecimal capitalSc = storeDAO.cpSelect(Session.loggedInUserId); // capitalSearch
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        String formattedCapital = numberFormat.format(capitalSc);
        model.addAttribute("capitalSc", formattedCapital);
        return "thymeleaf/storeAdminAcctSearch";
    }
}