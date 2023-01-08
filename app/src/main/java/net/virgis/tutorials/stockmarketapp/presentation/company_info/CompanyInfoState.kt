package net.virgis.tutorials.stockmarketapp.presentation.company_info

import net.virgis.tutorials.stockmarketapp.domain.model.CompanyInfo
import net.virgis.tutorials.stockmarketapp.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
