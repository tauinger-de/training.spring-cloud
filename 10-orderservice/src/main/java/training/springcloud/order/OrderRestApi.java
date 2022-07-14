package training.springcloud.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(OrderRestApi.ROOT_PATH)
public class OrderRestApi {

    // --- constants ---

    public static final String ROOT_PATH = "/orders";

    // --- beans ---

    private final String greetingMessage;

    private final OrderService orderService;

    // --- construction ---

    public OrderRestApi(@Value("${greeting.message}") String greetingMessage, OrderService orderService) {
        this.greetingMessage = greetingMessage;
        this.orderService = orderService;
    }

    // --- endpoints ---

    @GetMapping("/greeting")
    public String getGreeting() {
        return greetingMessage;
    }

    @PostMapping()
    public Order placeOrder(@RequestBody OrderRequest orderRequest) {
        return this.orderService.placeOrder(
                orderRequest.getPhoneNumber(),
                orderRequest.getProductQuantities()
        );
    }
}
