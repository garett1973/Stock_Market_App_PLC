package net.virgis.tutorials.stockmarketapp.domain.repository

import kotlinx.coroutines.flow.Flow
import net.virgis.tutorials.stockmarketapp.domain.model.CompanyInfo
import net.virgis.tutorials.stockmarketapp.domain.model.CompanyListing
import net.virgis.tutorials.stockmarketapp.domain.model.IntradayInfo
import net.virgis.tutorials.stockmarketapp.util.Resource

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntradayInfo(
        symbol: String
    ): Resource<List<IntradayInfo>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>
}