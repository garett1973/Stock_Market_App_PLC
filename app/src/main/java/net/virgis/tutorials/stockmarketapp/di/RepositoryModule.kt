package net.virgis.tutorials.stockmarketapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.virgis.tutorials.stockmarketapp.data.csv.CSVParser
import net.virgis.tutorials.stockmarketapp.data.csv.CompanyListingsParser
import net.virgis.tutorials.stockmarketapp.data.csv.IntradayInfoParser
import net.virgis.tutorials.stockmarketapp.data.repository.StockRepositoryImpl
import net.virgis.tutorials.stockmarketapp.domain.model.CompanyListing
import net.virgis.tutorials.stockmarketapp.domain.model.IntradayInfo
import net.virgis.tutorials.stockmarketapp.domain.repository.StockRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntradayParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}