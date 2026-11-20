package org.example.springboot.controller;

import org.example.springboot.common.Result;
import org.example.springboot.entity.Customer;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
public class HelloController {
    private final JdbcTemplate jdbcTemplate;

    public HelloController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //    @GetMapping("/hello")
    //    public String hello() {
    //        return "Hello Spring Boot!";
    //    }
    //    1. 使用 @PathVariable 接收路径参数
    @GetMapping("/customersId/{id}")
    public String getCustomer(@PathVariable("id") Long id) {
        return "查询客户 ID：" + id;
    }

    //    2. 使用 @RequestParam 接收查询参数
    @GetMapping("/customers2")
    public String getCustomer2(@RequestParam String name) {
        return "查询客户名称：" + name;
    }

    // 3. @RequestParam 接收多个参数
    @GetMapping("/customers/search1")
    public String search(@RequestParam String name,
                         @RequestParam Integer status) {
        return "查询客户名称：" + name + "，状态：" + status;
    }

    // 4. @RequestParam 接收可选参数
    @GetMapping("/customers/search2")
    public String search4(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "男") String sex) {

        return "客户名称：" + name + "，客户状态：" + status + "，客户性别：" + sex;
    }

    @PostMapping("/addCustomer")
    public Result<Customer> addCustomer(@RequestBody Customer customer) {
//        System.out.println("接收到客户数据：" + customer);
//        return Result.success( customer);
        return Result.success("客户接收成功", customer);
//        return Result.error("客户接收失败");

    }

    @GetMapping("/db/test")
    public Result<String> testDatabase() {

        String databaseName = jdbcTemplate.queryForObject(
                "SELECT DATABASE()",
                String.class
        );

        return Result.success("数据库连接成功", databaseName);
    }

    @GetMapping("/test/error")
    public Result<Void> testError() {
        int result = 10 / 0;
         return Result.error("执行失败");

    }
    @GetMapping("/test/getNull")
    public Result<Void> getNull() {
        int result = 10 / 0;
        return Result.success("执行成功", null);
    }
}
