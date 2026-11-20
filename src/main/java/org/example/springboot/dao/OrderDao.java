package org.example.springboot.dao;



import org.example.springboot.entity.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;

@Repository
public class OrderDao {

    private final JdbcTemplate jdbcTemplate;

    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 新增订单，返回数据库生成的主键 ID
    public Long addOrder(Order order) {

        String sql = """
                INSERT INTO orders
                (order_no, customer_id, total_amount)
                VALUES (?, ?, ?)
                """;
        // 获取数据库自动生成的主键
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, order.getOrderNo());
            ps.setLong(2, order.getCustomerId());
            ps.setBigDecimal(3, order.getTotalAmount());

            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}