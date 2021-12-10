package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import com.zacharywarunek.amazonclone.entitys.OrderHistory;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
//TODO Create Order History table in database
public interface IOrderHistoryRepo extends IJPABaseRepo<OrderHistory> {
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
