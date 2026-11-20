package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springboot.common.CustomerConverter;
import org.example.springboot.common.PageResult;
import org.example.springboot.common.Result;
import org.example.springboot.dto.CustomerDTO;
import org.example.springboot.entity.Customer;
import org.example.springboot.service.CustomerService;
import org.example.springboot.vo.CustomerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "客户管理",
        description = "CRM 客户管理相关接口"
)
@RestController
@RequestMapping("/customers")
public class CustomerController {

    // 注解注入 CustomerService
    @Autowired
    private CustomerService customerService;
    // 构造器注
    //    private final CustomerService customerService;
    //
    //    private CustomerController(CustomerService customerService){
    //        this.customerService = customerService;
    //    }

    /**
     * 查询客户列表
     */
    @Operation(summary = "查询客户列表")
    @GetMapping
    public Result<List<CustomerVO>> findAll() {

        List<CustomerVO> customers =
                customerService.findAllCustomers();

        return Result.success(customers);
    }

    /**
     * 新增客户
     */
    @Operation(summary = "新增客户")
    @PostMapping
    // 表示对接收到的 Customer 对象执行校验。
    //如果有字段不符合规则，就不会继续执行新增客户的业务代码。
    public Result<Integer> add(@RequestBody @Valid CustomerDTO customerDTO) {
        // 将 CustomerDTO 转换为 Customer 实体对象
        // CustomerDTO → Customer
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setCompany(customerDTO.getCompany());
        customer.setAddress(customerDTO.getAddress());
        customer.setStatus(customerDTO.getStatus());

        int rows = customerService.addCustomer(customer);
        return Result.success("新增客户成功", rows);
    }

    /**
     * 修改客户
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改客户")
    public Result<Integer> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid CustomerDTO customerDTO
    ) {
        // 将 CustomerDTO 转换为 Customer 实体对象
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setCompany(customerDTO.getCompany());
        customer.setAddress(customerDTO.getAddress());
        customer.setStatus(customerDTO.getStatus());

        int rows = customerService.updateCustomer(id, customer);
        return Result.success("修改客户成功", rows);
    }

    /**
     * 删除客户
     */
    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable("id") Long id) {
        int rows = customerService.deleteCustomer(id);

//        if (rows == 0) {
//            return Result.error("客户不存在");
//        }

        return Result.success("删除客户成功", rows);
    }

    /**
     * 根据 ID 查询客户详情
     */
    @Operation(summary = "根据 ID 查询客户详情")
    @GetMapping("/{id}")
    public Result<CustomerVO> findById(@PathVariable("id") Long id) {
        System.out.println("查询客户详情，id = " + id);
        CustomerVO customer = customerService.findCustomerById(id);

//        if (customer == null) {
//            return Result.error("客户不存在");
//        }

        return Result.success(customer);
    }

    /**
     * 客户分页查询
     */
    @Operation(summary = "客户分页查询")
    @GetMapping("/pageAll")
    public Result<PageResult<CustomerVO>> page(
            @RequestParam(name = "pageNum",defaultValue = "1") int pageNum,
            @RequestParam(name = "pageSize",defaultValue = "10") int pageSize
    ) {
        // 限制分页参数，避免不合理的查询
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(1, Math.min(pageSize, 100));

        PageResult<CustomerVO> result =
                customerService.findCustomerPage(pageNum, pageSize);

        return Result.success(result);
    }

    /**
     * 客户条件分页查询
     */
    @Operation(summary = "客户条件分页查询")
    @GetMapping("/page")
    public Result<PageResult<CustomerVO>> page(
            @RequestParam(name = "name",required = false) String name,
            @RequestParam(name = "company",required = false) String company,
            @RequestParam(name = "phone",required = false) String phone,
            @RequestParam(name = "pageNum",defaultValue = "1") int pageNum,
            @RequestParam(name = "pageSize",defaultValue = "10") int pageSize
    ) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(1, Math.min(pageSize, 100));

        PageResult<CustomerVO> result =
                customerService.findCustomerPageByCondition(
                        name,
                        company,
                        phone,
                        pageNum,
                        pageSize
                );

        return Result.success(result);
    }

    @Operation(summary = "测试事务回滚")
    @PutMapping("/test-transaction/{id}")
    public Result<Void> testTransaction(@PathVariable("id") Long id) {

        customerService.testTransaction(id);

        return Result.success("事务执行成功", null);
    }
}