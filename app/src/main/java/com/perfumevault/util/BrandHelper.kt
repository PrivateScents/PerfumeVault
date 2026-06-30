package com.perfumevault.util

object BrandHelper {
    fun shortenBrand(brand: String): String {
        return when (brand.trim().lowercase()) {
            "maison francis kurkdjian" -> "MFK"
            "jean paul gaultier" -> "JPG"
            "parfums de marly" -> "PDM"
            "yves saint laurent" -> "YSL"
            "dolce & gabbana" -> "D&G"
            "viktor & rolf" -> "V&R"
            "van cleef & arpels" -> "VC&A"
            "comme des garcons" -> "CDG"
            "narciso rodriguez" -> "NR"
            "estee lauder" -> "Estée Lauder"
            else -> brand
        }
    }
}
