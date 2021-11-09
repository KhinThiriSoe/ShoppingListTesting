package com.androiddevs.shoppinglisttestingyt.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androiddevs.shoppinglisttestingyt.data.local.ShoppingItem
import com.androiddevs.shoppinglisttestingyt.data.remote.responses.ImageResponse
import com.androiddevs.shoppinglisttestingyt.other.Resource

class FakeShoppingRepository : ShoppingRepository {

    private val shoppingItems = mutableListOf<ShoppingItem>()

    private val observeShoppingItems = MutableLiveData<List<ShoppingItem>>(shoppingItems)
    private val observeTotalPrice = MutableLiveData<Float>()

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.add(shoppingItem)
        refreshLiveData()
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.remove(shoppingItem)
        refreshLiveData()
    }

    override fun observeAllShoppingItems(): LiveData<List<ShoppingItem>> = observeShoppingItems

    override fun observeTotalPrice(): LiveData<Float> = observeTotalPrice

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return if (shouldReturnNetworkError) {
            Resource.error("Error", null)
        } else {
            Resource.success(ImageResponse(listOf(), 0, 0))
        }
    }

    private fun refreshLiveData() {
        observeShoppingItems.postValue(shoppingItems)
        observeTotalPrice.postValue(getTotalPrice())
    }

    private fun getTotalPrice(): Float = shoppingItems.sumOf {
        it.price.toDouble()
    }.toFloat()

}