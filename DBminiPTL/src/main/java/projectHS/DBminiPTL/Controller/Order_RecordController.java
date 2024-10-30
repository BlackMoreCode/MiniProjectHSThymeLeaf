package projectHS.DBminiPTL.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import projectHS.DBminiPTL.Common.Session;
import projectHS.DBminiPTL.DAO.Order_RecordDAO;
import projectHS.DBminiPTL.VO.Order_RecordVO;

import java.util.List;

@Controller
@RequestMapping("/main/customer")
public class Order_RecordController {
    private final Order_RecordDAO orderRecordDAO;
    public Order_RecordController(Order_RecordDAO orderRecordDAO) {
        this.orderRecordDAO = orderRecordDAO;
    }

    @GetMapping("/receipt")
    public String showReceipt(Model model) {
        System.out.println("Logged in User ID: " + Session.loggedInUserId); // Debugging line
        List<Order_RecordVO> lst = orderRecordDAO.userOrderList(Session.loggedInUserId);
        model.addAttribute("receipt", lst);
        return "thymeleaf/receipt";
    }

}
