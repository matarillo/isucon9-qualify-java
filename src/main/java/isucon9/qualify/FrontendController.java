package isucon9.qualify;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    private static final String defaultView = "index";

    @GetMapping("/")
    public String index() {
        return defaultView;
    }

    // mux.HandleFunc(pat.Get("/login"), getIndex)
    // mux.HandleFunc(pat.Get("/register"), getIndex)
    // mux.HandleFunc(pat.Get("/timeline"), getIndex)
    // mux.HandleFunc(pat.Get("/categories/:category_id/items"), getIndex)
    // mux.HandleFunc(pat.Get("/sell"), getIndex)
    // mux.HandleFunc(pat.Get("/items/:item_id"), getIndex)
    // mux.HandleFunc(pat.Get("/items/:item_id/edit"), getIndex)
    // mux.HandleFunc(pat.Get("/items/:item_id/buy"), getIndex)
    // mux.HandleFunc(pat.Get("/buy/complete"), getIndex)
    // mux.HandleFunc(pat.Get("/transactions/:transaction_id"), getIndex)
    // mux.HandleFunc(pat.Get("/users/:user_id"), getIndex)
    // mux.HandleFunc(pat.Get("/users/setting"), getIndex)

}
