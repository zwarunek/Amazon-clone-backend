package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.OrderHistory;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;

public interface IOrderHistoryRepo extends IJPABaseRepo<OrderHistory, String> {
    @Query(value = "SELECT * FROM OrderHistory", nativeQuery = true)
    Collection<OrderHistory> fetchAllOrderHistory();

    @Query(value = "SELECT * FROM OrderHistory WHERE AccountID=?", nativeQuery = true)
    OrderHistory fetchOrderHistoryByAccountID(int accountID);

    @Query(value = "SELECT * FROM OrderHistory WHERE AddressID=?", nativeQuery = true)
    OrderHistory fetchOrderHistoryByOrderHistoryID(int addressID);

    @Query(value = "SELECT * FROM OrderHistory WHERE Status=? AND AccountID=?", nativeQuery = true)
    OrderHistory fetchOrderHistoryByStatusAndAccountID(int accountID, int status);

    @Query(value = "SELECT * FROM OrderHistory WHERE ProductListID=? AND AccountID=?", nativeQuery = true)
    OrderHistory fetchOrderHistoryByProductListIDAndAccountID(int accountID, int productListID);

    @Query(value = "SELECT * FROM OrderHistory WHERE PaymentMethodID=? AND AccountID=?", nativeQuery = true)
    OrderHistory fetchOrderHistoryByPaymentMethodIDAndAccountID(int accountID, int paymentMethodID);

    @Query(value = "SELECT * FROM OrderHistory WHERE Timestamp=? AND AccountID=?", nativeQuery = true)
    OrderHistory fetchOrderHistoryByTimestampAndAccountID(int accountID, int timestamp);
}