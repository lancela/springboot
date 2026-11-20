package org.example.springboot.service;


import org.example.springboot.dao.CustomerDao;
import org.example.springboot.dao.OrderDao;
import org.example.springboot.dao.OrderItemDao;
import org.example.springboot.entity.Order;
import org.example.springboot.entity.OrderItem;
import org.example.springboot.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    @Autowired
    private CustomerDao customerDao;

    public OrderService(
            OrderDao orderDao,
            OrderItemDao orderItemDao) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }
    // 整个 createOrder() 方法中的数据库操作都属于同一个事务
    // rollbackFor指定哪些异常触发回滚
    @Transactional(rollbackFor = Exception.class)
    public void createOrder() {
        if (customerDao.findById(6L) == null) {
            throw new BusinessException("客户不存在，不能创建订单");
        }
        // 1. 创建订单
        Order order = new Order();

        order.setOrderNo(
                "ORD-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
        );

        order.setCustomerId(1L);
        order.setTotalAmount(new BigDecimal("800"));

        // 2. 保存订单主表
        Long orderId = orderDao.addOrder(order);

        // 3. 创建产品明细
        List<OrderItem> items = List.of(
                createItem(orderId, "产品 A", 2, "100"),
                createItem(orderId, "产品 B", 3, "200")
        );

        // 4. 保存订单明细
        for (OrderItem item : items) {
            orderItemDao.addOrderItem(item);
        }
    }

    private OrderItem createItem(
            Long orderId,
            String productName,
            Integer quantity,
            String price) {

        OrderItem item = new OrderItem();

        item.setOrderId(orderId);
        item.setProductName(productName);
        item.setQuantity(quantity);
        item.setPrice(new BigDecimal(price));

        return item;
    }
}
