package org.example.springboot.dao;


import org.example.springboot.entity.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemDao {

    private final JdbcTemplate jdbcTemplate;

    public OrderItemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int addOrderItem(OrderItem item) {

        String sql = """
                INSERT INTO order_item
                (order_id, product_name, quantity, price)
                VALUES (?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                item.getOrderId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}