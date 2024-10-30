package projectHS.DBminiPTL.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projectHS.DBminiPTL.Common.Session;
import projectHS.DBminiPTL.Customer.SetMenu;
import projectHS.DBminiPTL.Customer.SingleMenu;
import projectHS.DBminiPTL.DAO.InvDAO;
import projectHS.DBminiPTL.DAO.Order_RecordDAO;
import projectHS.DBminiPTL.VO.InvVO;

import java.util.List;


@Controller
@RequestMapping("/main/customer")
public class CustomerOrderController {
    private final InvDAO invDAO;
    private final Order_RecordDAO orderRecordDAO;

    public CustomerOrderController(InvDAO invDAO, Order_RecordDAO orderRecordDAO) {
        this.invDAO = invDAO;
        this.orderRecordDAO = orderRecordDAO;
    }

    @GetMapping("/stores")
    public String showStores(Model model) {
        List<String> lst = invDAO.showStores();
        model.addAttribute("stores", lst);
        return "thymeleaf/stores";
    }

    @PostMapping("/stores")
    public String choiceStore(@ModelAttribute("storeId") String storeId) {
        if (Session.storeId == null) {
            Session.storeId = storeId;
        } else if (!Session.storeId.equals(storeId)) {
            invDAO.clearCart();
            Session.storeId = storeId;
        }

        return "redirect:/main/customer/showMenu";
    }


    @GetMapping("/showMenu")
    public String cusOrder(Model model) {
        List<InvVO> menus = invDAO.getAllMenus(Session.storeId);

        // 장바구니 내용 가져오기
        List<SetMenu> setCart = invDAO.getSetCart();
        List<SingleMenu> singleCart = invDAO.getSingleCart();

        // 총 가격 계산
        int totalPrice = invDAO.getTotalPrice();


        // 모델에 추가
        model.addAttribute("allMenus", menus);
        model.addAttribute("setCart", setCart);
        model.addAttribute("singleCart", singleCart);
        model.addAttribute("totalPrice", totalPrice);

        model.addAttribute("selectedStore", Session.storeId);

        return "thymeleaf/showMenu"; // 장바구니 내용이 포함된 메뉴 페이지로 이동
    }

    @PostMapping("/cart/set")
    public String addSetToCart(@RequestParam String burgerName, @RequestParam int burgerPrice,
                               @RequestParam String sideName, @RequestParam int sidePrice,
                               @RequestParam String drinkName, @RequestParam int drinkPrice,
                               @RequestParam int setCount) {
        System.out.println("세트 메뉴 추가 메서드 호출");

        if (burgerName == null || sideName == null || drinkName == null || setCount <= 0) {
            return "redirect:/main/customer/showMenu"; // 유효하지 않은 경우 메뉴 페이지로 리다이렉트
        }

        // SingleMenu 객체 생성
        SingleMenu burger = new SingleMenu(burgerName, burgerPrice);
        SingleMenu side = new SingleMenu(sideName, sidePrice);
        SingleMenu drink = new SingleMenu(drinkName, drinkPrice);

        // SetMenu 객체 생성
        SetMenu setMenu = new SetMenu(burger, side, drink, setCount);

        invDAO.addSetMenu(setMenu);
        // 디버깅

        return "redirect:/main/customer/showMenu";
    }

    @PostMapping("/cart/updateSet")
    public String updateSetCart(@RequestParam String burgerName, @RequestParam String sideName, @RequestParam String drinkName, @RequestParam int newCount) {
        invDAO.updateSetMenu(burgerName, sideName, drinkName, newCount);

        return "redirect:/main/customer/showMenu";
    }

    @PostMapping("/cart/removeSet")
    public String removeSetCart(@RequestParam String burgerName, @RequestParam String sideName, @RequestParam String drinkName) {
        invDAO.removeSetMenu(burgerName, sideName, drinkName);

        return "redirect:/main/customer/showMenu";
    }

    @PostMapping("/cart/single")
    public String addSingleToCart(@RequestParam String menuName,
                                  @RequestParam int menuPrice,
                                  @RequestParam int count) {
        System.out.println("단일 메뉴 추가 메서드 호출");

        if (menuName == null || count <= 0) {
            return "redirect:/main/customer/showMenu"; // 유효하지 않은 경우 메뉴 페이지로 리다이렉트
        }

        // SingleMenu 객체 생성
        SingleMenu singleMenu = new SingleMenu(menuName, menuPrice, count);

        // 리스트에 추가
        invDAO.addSingleMenu(singleMenu);
        System.out.println(singleMenu.getName() + "추가");

        return "redirect:/main/customer/showMenu";
    }

    @PostMapping("/cart/updateSingle")
    public String updateSingleCart(@RequestParam String menuName, @RequestParam int newCount) {
        invDAO.updateSingleMenu(menuName, newCount);

        return "redirect:/main/customer/showMenu";
    }

    @PostMapping("/cart/removeSingle")
    public String removeSingleCart(@RequestParam String menuName) {
        invDAO.removeSingleMenu(menuName);

        return "redirect:/main/customer/showMenu";
    }


    @PostMapping("/checkout")
    public String checkout(Model model, RedirectAttributes redirectAttributes) {
        // 재고 확인
        String stockCheckMessage = invDAO.customerStockCheck(Session.storeId);


        if (stockCheckMessage != null) {
            System.out.println(stockCheckMessage);
            // 재고가 부족한 경우, 메시지를 모델에 추가
            redirectAttributes.addFlashAttribute("stockCheckMsg", stockCheckMessage);
            return "redirect:/main/customer/showMenu"; // 재고 부족 메시지를 표시할 메뉴 페이지로 리다이렉트
        }

        try {
            invDAO.checkoutStockUpdate(Session.storeId);

            String orderList = invDAO.orderToString();
            int price = invDAO.getTotalPrice();
            //orderRecordInsert(Session.storeId, invDAO.orderToString(), invDAO.getTotalPrice(), Session.loggedInUserId);
            orderRecordDAO.orderRecordInsert(Session.storeId, orderList, price, Session.loggedInUserId);


            model.addAttribute("orderPrice", price);
            invDAO.clearCart();


        } catch (Exception e) {
            System.out.println("결제 실패 : " + e.getMessage());
            model.addAttribute("errorMsg", "결제 처리 중 오류가 발생했습니다.");
            return "redirect:/main/customer/showMenu";
        }

        return "redirect:/main/customer"; // 주문 성공 이후 기존 회원 페이지로
    }


}
