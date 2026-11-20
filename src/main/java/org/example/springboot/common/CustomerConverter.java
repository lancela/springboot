package org.example.springboot.common;

import org.example.springboot.entity.Customer;
import org.example.springboot.vo.CustomerVO;
// 这个类是用来在 Customer 和 CustomerVO 之间进行转换的工具类。
// Customer → CustomerVO
// CustomerConverter.toVO(customer);
public class CustomerConverter {

    public static CustomerVO toVO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerVO vo = new CustomerVO();

        vo.setId(customer.getId());
        vo.setName(customer.getName());
        vo.setPhone(customer.getPhone());
        vo.setEmail(customer.getEmail());
        vo.setCompany(customer.getCompany());
        vo.setAddress(customer.getAddress());
        vo.setStatus(customer.getStatus());

        return vo;
    }
}
