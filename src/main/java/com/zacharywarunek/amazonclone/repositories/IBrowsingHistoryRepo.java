package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import com.zacharywarunek.amazonclone.entitys.BrowsingHistory;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

//TODO Create Browsing History table in database
public interface IBrowsingHistoryRepo extends IJPABaseRepo<BrowsingHistory> {
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
