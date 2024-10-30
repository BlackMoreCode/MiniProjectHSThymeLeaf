package projectHS.DBminiPTL.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projectHS.DBminiPTL.Common.Session;
import projectHS.DBminiPTL.Customer.SingleMenu;
import projectHS.DBminiPTL.DAO.InvDAO;
import projectHS.DBminiPTL.VO.InvVO;

import java.util.List;


@Controller
@RequestMapping("/main/customer")
public class StoreOrderController {
    private final InvDAO invDAO;

    public StoreOrderController(InvDAO invDAO) {
        this.invDAO = invDAO;
    }


    @GetMapping("/storeStat")
    public String storeStat(Model model) {

        List<InvVO> stockCheck = invDAO.stockCheck(Session.storeId);
        List<InvVO> products = invDAO.productHQ();
        List<SingleMenu> cart = invDAO.getStoreCart();


        int capital = invDAO.capitalCheck(Session.storeId);
        int sales = invDAO.salesCheck(Session.storeId);
        String salesStr = invDAO.formatAmount(sales);
        int totalPrice = invDAO.getTotalPriceStore();

        for (SingleMenu e : cart) {
            System.out.println(e.getName() + " : " + e.getPrice());
        }

        model.addAttribute("selectedStore", Session.storeId);
        model.addAttribute("stockList", stockCheck);
        model.addAttribute("cart", cart);
        model.addAttribute("capital", capital);
        model.addAttribute("sales", salesStr);
        model.addAttribute("totalPrice", totalPrice);

        model.addAttribute("products", products);

        return "thymeleaf/StoreOrder";
    }


    @PostMapping("/addToCart")
    public String addCart(@RequestParam String menuName, @RequestParam int price, @RequestParam int count) {

        SingleMenu singleMenu = new SingleMenu(menuName, price, count);

        invDAO.addStoreCart(singleMenu);

        return "redirect:/main/customer/storeStat";
    }

    @PostMapping("/cartUpdate")
    public String updateCart(@RequestParam String menuName, @RequestParam int newCount) {
        invDAO.updateStoreCart(menuName, newCount);

        return "redirect:/main/customer/storeStat";
    }

    @PostMapping("/cartRemove")
    public String removeSingleCart(@RequestParam String menuName) {
        invDAO.removeStoreCart(menuName);

        return "redirect:/main/customer/storeStat";
    }

    @PostMapping("/storeCheckout")
    @Transactional
    public String storeCheckout(RedirectAttributes redirectAttributes) {
        int capital = invDAO.capitalCheck(Session.storeId);
        int totalPrice = invDAO.getTotalPriceStore();

        if (capital < totalPrice) {
            redirectAttributes.addFlashAttribute("overCapital", capital + "원 이하로 주문 해 주세요");
            return "redirect:/main/customer/storeStat";
        }

        try {
            // 재고 업데이트 및 자본금 감소
            invDAO.updateInventory(Session.storeId);
            invDAO.updateCapital(Session.storeId, totalPrice);

            // 발주가 성공한 후 storeClear() 호출
            invDAO.storeCartClear(); // 장바구니 비우기
        } catch (Exception e) {
            // 예외 발생 시 처리 (예: 로그 기록)
            // 트랜잭션이 롤백되므로 추가적인 조치 필요 없음
            // 예외를 던져서 롤백을 유도할 수 있습니다.
            throw new RuntimeException("발주 처리 중 오류 발생", e);
        }

        return "redirect:/main/customer/storeStat";
    }
}
