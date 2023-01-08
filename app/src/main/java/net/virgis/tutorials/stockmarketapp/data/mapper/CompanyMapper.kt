package net.virgis.tutorials.stockmarketapp.data.mapper

import net.virgis.tutorials.stockmarketapp.data.local.CompanyListingEntity
import net.virgis.tutorials.stockmarketapp.data.remote.dto.CompanyInfoDto
import net.virgis.tutorials.stockmarketapp.domain.model.CompanyInfo
import net.virgis.tutorials.stockmarketapp.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing(): CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyListing.toCompanyListingEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}