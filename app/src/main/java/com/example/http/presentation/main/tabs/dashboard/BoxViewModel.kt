package com.example.http.presentation.main.tabs.dashboard

import androidx.lifecycle.viewModelScope
import com.example.http.domain.Success
import com.example.http.domain.accounts.AccountsRepository
import com.example.http.domain.boxes.BoxesRepository
import com.example.http.domain.boxes.entities.BoxesFilter
import com.example.http.presentation.base.BaseViewModel
import com.example.http.utils.MutableLiveEvent
import com.example.http.utils.logger.Logger
import com.example.http.utils.publishEvent
import com.example.http.utils.share
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

//Use only Dagger, because need add one parameter.
class BoxViewModel @AssistedInject constructor(
    @Assisted boxId: Long,
    private val boxesRepository: BoxesRepository,
    accountsRepository: AccountsRepository,
    logger: Logger
) : BaseViewModel(accountsRepository, logger) {

    private val _shouldExitEvent = MutableLiveEvent<Boolean>()
    val shouldExitEvent = _shouldExitEvent.share()


    init {
        viewModelScope.launch {
           boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE)
                .map { result -> result.map { boxes -> boxes.firstOrNull { it.box.id == boxId } } }
                .collect { result ->
                    _shouldExitEvent.publishEvent(result is Success && result.value == null)
                }
        }
    }
    // Interface for create parameter boxId. Invoke in Fragment.

    @AssistedFactory
    interface Factory{
        fun create(boxId: Long) : BoxViewModel
    }

}