package org.example.springboot.dao;


import org.example.springboot.entity.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    public CustomerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Customer> findAll() {

        String sql = "SELECT * FROM customer ORDER BY id DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            Customer customer = new Customer();

            customer.setId(rs.getLong("id"));
            customer.setName(rs.getString("name"));
            customer.setPhone(rs.getString("phone"));
            customer.setEmail(rs.getString("email"));
            customer.setCompany(rs.getString("company"));
            customer.setAddress(rs.getString("address"));
            customer.setStatus(rs.getInt("status"));

            return customer;
        });
    }

    /**
     * 新增客户
     */
    public int add(Customer customer) {
        String sql = "INSERT INTO customer\n" +
                "(name, phone, email, company, address, status)\n" +
                "VALUES (?, ?, ?, ?, ?, ?)\n";

        return jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getCompany(),
                customer.getAddress(),
                customer.getStatus()
        );
    }

    /**
     * 根据 ID 修改客户
     */
    public int update(Long id, Customer customer) {
        String sql = "UPDATE customer\n" +
                "SET name = ?,\n" +
                "    phone = ?,\n" +
                "    email = ?,\n" +
                "    company = ?,\n" +
                "    address = ?,\n" +
                "    status = ?\n" +
                "WHERE id = ?\n";

        return jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getCompany(),
                customer.getAddress(),
                customer.getStatus(),
                id
        );
    }

    /**
     * 根据 ID 删除客户
     */
    public int deleteById(Long id) {
        String sql = "DELETE FROM customer WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }

    /**
     * 根据 ID 查询客户
     */
    public Customer findById(Long id) {
        String sql = "SELECT * FROM customer WHERE id = ?";

        List<Customer> customers = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Customer customer = new Customer();

                    customer.setId(rs.getLong("id"));
                    customer.setName(rs.getString("name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    customer.setCompany(rs.getString("company"));
                    customer.setAddress(rs.getString("address"));
                    customer.setStatus(rs.getInt("status"));

                    return customer;
                },
                id
        );

        return customers.isEmpty() ? null : customers.get(0);
    }

    /**
     * 分页查询客户
     */
    public List<Customer> findPage(int pageNum, int pageSize) {

        // 计算需要跳过多少条记录
        int offset = (pageNum - 1) * pageSize;

        String sql = """
                SELECT * FROM customer
                ORDER BY id DESC
                LIMIT ? OFFSET ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Customer customer = new Customer();

                    customer.setId(rs.getLong("id"));
                    customer.setName(rs.getString("name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    customer.setCompany(rs.getString("company"));
                    customer.setAddress(rs.getString("address"));
                    customer.setStatus(rs.getInt("status"));

                    return customer;
                },
                pageSize,
                offset
        );
    }

    /**
     * 查询客户总数
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM customer";

        Long total = jdbcTemplate.queryForObject(sql, Long.class);

        return total == null ? 0 : total;
    }

    /**
     * 根据条件分页查询客户
     */
    public List<Customer> findPageByCondition(
            String name,
            String company,
            String phone,
            int pageNum,
            int pageSize
    ) {
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM customer WHERE 1 = 1"
        );

        List<Object> params = new ArrayList<>();

        // 客户名称模糊查询
        if (name != null && !name.isBlank()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name.trim() + "%");
        }

        // 公司名称模糊查询
        if (company != null && !company.isBlank()) {
            sql.append(" AND company LIKE ?");
            params.add("%" + company.trim() + "%");
        }

        // 手机号模糊查询
        if (phone != null && !phone.isBlank()) {
            sql.append(" AND phone LIKE ?");
            params.add("%" + phone.trim() + "%");
        }

        // 按 ID 倒序
        sql.append(" ORDER BY id DESC");

        // 分页
        sql.append(" LIMIT ? OFFSET ?");

        int offset = (pageNum - 1) * pageSize;

        params.add(pageSize);
        params.add(offset);

        return jdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> {
                    Customer customer = new Customer();

                    customer.setId(rs.getLong("id"));
                    customer.setName(rs.getString("name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    customer.setCompany(rs.getString("company"));
                    customer.setAddress(rs.getString("address"));
                    customer.setStatus(rs.getInt("status"));

                    return customer;
                },
                params.toArray()
        );
    }
    /**
     * 根据条件查询客户总数
     */
    public long countByCondition(
            String name,
            String company,
            String phone
    ) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM customer WHERE 1 = 1"
        );

        List<Object> params = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name.trim() + "%");
        }

        if (company != null && !company.isBlank()) {
            sql.append(" AND company LIKE ?");
            params.add("%" + company.trim() + "%");
        }

        if (phone != null && !phone.isBlank()) {
            sql.append(" AND phone LIKE ?");
            params.add("%" + phone.trim() + "%");
        }

        Long total = jdbcTemplate.queryForObject(
                sql.toString(),
                Long.class,
                params.toArray()
        );

        return total == null ? 0 : total;
    }


    public int updateName(Long id, String name) {

        String sql = "UPDATE customer SET name = ? WHERE id = ?";

        return jdbcTemplate.update(sql, name, id);
    }


    public String findNameById(Long id) {

        String sql = "SELECT name FROM customer WHERE id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                String.class,
                id
        );
    }
}