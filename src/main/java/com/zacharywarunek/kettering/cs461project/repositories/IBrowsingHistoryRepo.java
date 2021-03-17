package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.Address;
import com.zacharywarunek.kettering.cs461project.entitys.BrowsingHistory;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;

public interface IBrowsingHistoryRepo extends IJPABaseRepo<BrowsingHistory, String> {
    @Query(value = "SELECT * FROM BrowsingHistory", nativeQuery = true)
    Collection<BrowsingHistory> fetchAllBrowsingHistory();

    @Query(value = "SELECT * FROM BrowsingHistory WHERE AccountID=?", nativeQuery = true)
    BrowsingHistory fetchBrowsingHistoryByAccountID(int accountID);

    @Query(value = "SELECT * FROM Address WHERE TimesViewed=? AND AccountID=?", nativeQuery = true)
    BrowsingHistory fetchBrowsingHistorybyTimesViewedAndAccountID(int accountID, int timesViewed);

    @Query(value = "SELECT * FROM BrowsingHistory WHERE productID=? AND AccountID=?", nativeQuery = true)
    BrowsingHistory fetchBrowsingHistoryByProductIDAndAccountID(int accountID, int productID);

    @Query(value = "SELECT * FROM BrowsingHistory WHERE RecentView=? AND AccountID=?", nativeQuery = true)
    BrowsingHistory fetchBrowsingHistoryByRecentViewAndAccountID(int accountID, int recentView);
}