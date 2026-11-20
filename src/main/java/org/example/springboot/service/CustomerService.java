package org.example.springboot.service;

import org.example.springboot.common.CustomerConverter;
import org.example.springboot.common.PageResult;
import org.example.springboot.dao.CustomerDao;
import org.example.springboot.entity.Customer;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.vo.CustomerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerDao customerDao;
    //    private final CustomerDao customerDao;
    //
    //    public CustomerService(CustomerDao customerDao) {
    //        this.customerDao = customerDao;
    //    }

    public List<CustomerVO> findAllCustomers() {

        List<Customer> customers = customerDao.findAll();


        //List<CustomerVO>  list= new ArrayList<>();
        //for (Customer customer : customers) {
        //    CustomerVO vo = new CustomerVO();
        //
        //    vo.setId(customer.getId());
        //    vo.setName(customer.getName());
        //    vo.setPhone(customer.getPhone());
        //    vo.setEmail(customer.getEmail());
        //    vo.setCompany(customer.getCompany());
        //    vo.setAddress(customer.getAddress());
        //    vo.setStatus(customer.getStatus());
        //    list.add(vo);
        //}

        return customers.stream()
                .map(CustomerConverter::toVO)
                .toList();
    }

    /**
     * 新增客户
     */
    public int addCustomer(Customer customer) {
        return customerDao.add(customer);
    }

    /**
     * 修改客户
     */
    public int updateCustomer(Long id, Customer customer) {
        int rows = customerDao.update(id, customer);
        if (rows == 0) {
            throw new BusinessException("客户不存在或没有发生修改");
        }
        return rows;
    }

    /**
     * 删除客户
     */
    public int deleteCustomer(Long id) {
        int rows = customerDao.deleteById(id);
        if (rows == 0) {
            throw new BusinessException("客户不存在");
        }
        return rows;
    }

    /**
     * 根据 ID 查询客户
     */
    public CustomerVO findCustomerById(Long id) {
        Customer customer = customerDao.findById(id);

        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

//        return customer;
        return CustomerConverter.toVO(customer);
    }

    /**
     * 客户分页查询
     */
    public PageResult<CustomerVO> findCustomerPage(
            int pageNum,
            int pageSize
    ) {
        // 查询当前页数据
        List<Customer> records =
                customerDao.findPage(pageNum, pageSize);

        // 查询总记录数
        long total = customerDao.count();

        // 封装分页结果
        return new PageResult<>(
                pageNum,
                pageSize,
                total,
                records.stream()
                        .map(CustomerConverter::toVO)
                        .toList()
        );
    }

    /**
     * 客户条件分页查询
     */
    public PageResult<CustomerVO> findCustomerPageByCondition(
            String name,
            String company,
            String phone,
            int pageNum,
            int pageSize
    ) {
        long count = customerDao.countByCondition(
                name,
                company,
                phone
        );

        List<Customer> pageByCondition = customerDao.findPageByCondition(
                name, company, phone, pageNum, pageSize);

        PageResult<Customer> pageResult = new PageResult();
        pageResult.setRecords(pageByCondition);
        pageResult.setTotal(count);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setPages((int) (count / pageSize + (count % pageSize == 0 ? 0 : 1)));


        return pageResult.convert(CustomerConverter::toVO);

//        List<Customer> records =
//                customerDao.findPageByCondition(
//                        name,
//                        company,
//                        phone,
//                        pageNum,
//                        pageSize
//                );
//
//        long count = customerDao.countByCondition(
//                name,
//                company,
//                phone
//        );
//
//        return new PageResult<>(
//                pageNum,
//                pageSize,
//                total,
//                records.stream()
//                        .map(CustomerConverter::toVO)
//                        .toList()
//        );
    }

    // 测试事务回滚
    @Transactional
    public void testTransaction(Long id) {

        // 第一步：修改客户名称
        customerDao.updateName(id, "事务测试客户");

        // 第二步：模拟业务异常
        throw new RuntimeException("模拟业务失败");
    }
}
