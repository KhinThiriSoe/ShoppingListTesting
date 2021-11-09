package com.androiddevs.shoppinglisttestingyt.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androiddevs.shoppinglisttestingyt.MainCoroutineRule
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValueTest
import com.androiddevs.shoppinglisttestingyt.other.Constants
import com.androiddevs.shoppinglisttestingyt.other.Status
import com.androiddevs.shoppinglisttestingyt.repositories.FakeShoppingRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ShoppingViewModelTest {

    /*
     * This method is used to make sure that everything will run on the same thread.
     * One action after the other.
     */
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    /*
     * To solve the module with main dispatchers has failed to initialize
     * it won't be a problem when we test in androidTest or real app.
     */
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ShoppingViewModel

    @Before
    fun setup() {
        viewModel = ShoppingViewModel(FakeShoppingRepository())
    }

    @Test
    fun `insert shopping item with empty field, return error`() {
        viewModel.insertShoppingItem("name", "", "100.0")

        val result = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long name, return error`() {
        val string = buildString {
            for (i in 1..Constants.MAX_NAME_LENGTH + 1) {
                append(i)
            }
        }
        viewModel.insertShoppingItem(string, "5", "100.0")

        val result = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long price, return error`() {
        val string = buildString {
            for (i in 1..Constants.MAX_PRICE_LENGTH + 1) {
                append(i)
            }
        }
        viewModel.insertShoppingItem("name", "5", string)

        val result = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too high amount, return error`() {
        viewModel.insertShoppingItem("name", "9999999999999999999", "2.5")

        val result = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with valid inputs, return success`() {
        viewModel.insertShoppingItem("name", "90", "2.5")

        val result = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }
}