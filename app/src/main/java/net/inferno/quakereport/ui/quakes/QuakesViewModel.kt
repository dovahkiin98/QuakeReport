package net.inferno.quakereport.ui.quakes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import net.inferno.quakereport.data.Repository

class QuakesViewModel : ViewModel() {

    fun requestData() = Repository.getEarthQuakesStream()
        .cachedIn(viewModelScope)
}