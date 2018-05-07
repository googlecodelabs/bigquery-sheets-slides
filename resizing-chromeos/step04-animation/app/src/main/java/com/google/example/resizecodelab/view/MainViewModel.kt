/*      Copyright 2018 Google LLC

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.google.example.resizecodelab.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.example.resizecodelab.model.AppData
import com.google.example.resizecodelab.model.DataProvider

class MainViewModel : ViewModel() {

    private val internalProductName = MutableLiveData<String>()
    private val internalIsDescriptionExpanded = MutableLiveData<Boolean>()
    private val internalAppData = MutableLiveData<AppData>()
    private val reviewProvider = DataProvider()

    val productName: LiveData<String>
        get() = internalProductName

    fun setProductName(newName: String) {
        internalProductName.value = newName
    }

    val isDescriptionExpanded: LiveData<Boolean>
        get() = internalIsDescriptionExpanded

    fun setDescriptionExpanded(newState: Boolean) {
        internalIsDescriptionExpanded.value = newState
    }

    val appData: LiveData<AppData>
        get() = internalAppData

    init {
        reviewProvider.fetchData(object : DataProvider.Listener {
            override fun onSuccess(appData: AppData) {
                internalAppData.postValue(appData)
            }
        })
    }
}
