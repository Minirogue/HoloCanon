package usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.usecase.GetCheckboxSettings
import settings.usecase.GetCheckboxText
import javax.inject.Inject

internal class GetCheckboxTextImpl @Inject constructor(private val getCheckboxSettings: GetCheckboxSettings) :
    GetCheckboxText {
    override operator fun invoke(): Flow<Array<String>> =
        getCheckboxSettings().map { checkboxSettings ->
            arrayOf(
                checkboxSettings.checkbox1Setting.name,
                checkboxSettings.checkbox2Setting.name,
                checkboxSettings.checkbox3Setting.name,
            )
        }
}
