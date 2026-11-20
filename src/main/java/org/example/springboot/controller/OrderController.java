package org.example.springboot.controller;


import org.example.springboot.common.Result;
import org.example.springboot.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/test")
    public Result<Void> createOrder() {

        orderService.createOrder();

        return Result.success("订单创建成功", null);
    }
}
