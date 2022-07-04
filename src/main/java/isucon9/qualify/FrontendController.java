package isucon9.qualify;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({ "/", "/login", "/register", "/timeline", "/categories/{category_id}/items", "/sell",
            "/items/{item_id}", "/items/{item_id}/edit", "/items/{item_id}/buy", "/buy/complete",
            "/transactions/{transaction_id}", "/users/{user_id}", "/users/setting" })
    public String index() {
        return "index";
    }
}
