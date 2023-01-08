package net.virgis.tutorials.stockmarketapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.virgis.tutorials.stockmarketapp.data.csv.CSVParser
import net.virgis.tutorials.stockmarketapp.data.csv.CompanyListingsParser
import net.virgis.tutorials.stockmarketapp.data.csv.IntradayInfoParser
import net.virgis.tutorials.stockmarketapp.data.local.StockDatabase
import net.virgis.tutorials.stockmarketapp.data.mapper.toCompanyInfo
import net.virgis.tutorials.stockmarketapp.data.mapper.toCompanyListing
import net.virgis.tutorials.stockmarketapp.data.mapper.toCompanyListingEntity
import net.virgis.tutorials.stockmarketapp.data.remote.StockApi
import net.virgis.tutorials.stockmarketapp.domain.model.CompanyInfo
import net.virgis.tutorials.stockmarketapp.domain.model.CompanyListing
import net.virgis.tutorials.stockmarketapp.domain.model.IntradayInfo
import net.virgis.tutorials.stockmarketapp.domain.repository.StockRepository
import net.virgis.tutorials.stockmarketapp.util.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>,
): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch(e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Could not load data from remote source"))
                emit(Resource.Loading(false))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Server error"))
                emit(Resource.Loading(false))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol)
            val results = intradayInfoParser.parse(response.byteStream())
            Resource.Success(results)
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(
                message = "Could not load intraday info"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(
                message = "Http error"
            )
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(
                message = "Could not load company info"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(
                message = "Http error"
            )
        }
    }
}