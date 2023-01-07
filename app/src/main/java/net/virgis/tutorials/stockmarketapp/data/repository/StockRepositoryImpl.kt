package net.virgis.tutorials.stockmarketapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.virgis.tutorials.stockmarketapp.data.local.StockDatabase
import net.virgis.tutorials.stockmarketapp.data.mapper.toCompanyListing
import net.virgis.tutorials.stockmarketapp.data.remote.StockApi
import net.virgis.tutorials.stockmarketapp.domain.model.CompanyListing
import net.virgis.tutorials.stockmarketapp.domain.repository.StockRepository
import net.virgis.tutorials.stockmarketapp.util.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabase
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

            } catch(e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Could not load data from remote source"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Server error"))
                emit(Resource.Loading(false))
                return@flow
            }
        }
    }
}