package view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PaymentController {

    @PostMapping("/payment")
    public String paymentWidgetByToss() {
        return "redirect:/payment-widget-by-toss";
    }
}