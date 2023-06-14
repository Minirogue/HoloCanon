package filters

data class MediaFilter(
    val id: Int,
    val name: String,
    val filterType: FilterType,
    val isPositive: Boolean,
    val isActive: Boolean,
)

data class FilterGroup(val type: FilterType, val isFilterPositive: Boolean, val text: String)

enum class FilterType(val legacyIntegerConversion: Int) {
    MediaType(1),
    CheckboxOne(3),
    CheckboxTwo(4),
    CheckboxThree(5),
    Series(6),
    Publisher(7),
}
