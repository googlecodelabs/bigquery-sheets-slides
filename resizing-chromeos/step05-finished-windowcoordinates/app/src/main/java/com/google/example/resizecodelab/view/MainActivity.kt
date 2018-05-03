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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.example.resizecodelab.R
import com.google.example.resizecodelab.model.AppData
import com.google.example.resizecodelab.model.Suggestion
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val KEY_PRODUCT_NAME = "KEY_PRODUCT_NAME"
        private const val KEY_EXPANDED = "KEY_EXPANDED"
    }

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Retrieve the ViewModel with state data
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        //Restore savedInstanceState variables
        savedInstanceState?.getString(KEY_PRODUCT_NAME)?.let { viewModel.setProductName(it) }
        savedInstanceState?.getBoolean(KEY_EXPANDED)?.let { viewModel.setDescriptionExpanded(it) }

        //Set up recycler view for reviews
        reviewAdapter = ReviewAdapter()
        recyclerReviews.apply {
            setHasFixedSize(true)
            adapter = reviewAdapter
        }

        //Set up recycler view for suggested products
        suggestionAdapter = SuggestionAdapter()
        recyclerSuggested.apply {
            setHasFixedSize(true)
            adapter = suggestionAdapter
        }
        suggestionAdapter.updateSuggestions(getSuggestedProducts())

        //Expand/collapse button for product description
        buttonExpand.setOnClickListener { _ ->
            viewModel.appData.value?.let {
                toggleExpandButton();
            }
        }

        //Add Observer to review data
        viewModel.appData.observe(this, Observer<AppData> { appData ->
            handleReviewsUpdate(appData)
        })

        //Add Observer to the description expand/collapse button
        viewModel.isDescriptionExpanded.observe(this, Observer<Boolean> {
            if (true == it)
                buttonExpand.text = getString(R.string.button_collapse)
            else
                buttonExpand.text = getString(R.string.button_expand)

            textProductDescription.text = getDescriptionText(viewModel.appData.value)
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(KEY_EXPANDED, viewModel.isDescriptionExpanded.value == true)
        outState?.putString(KEY_PRODUCT_NAME, viewModel.productName.value)
    }

    private fun handleReviewsUpdate(appData: AppData?) {
        progressLoadingReviews.visibility = if (appData == null) VISIBLE else GONE
        buttonPurchase.visibility = if (appData != null) VISIBLE else GONE
        buttonExpand.visibility = if (appData != null) VISIBLE else GONE
        appData?.let {
            textProductName.text = it.title
            textProductCompany.text = it.developer
            textProductDescription.text = getDescriptionText(it)
            reviewAdapter.onReviewsLoaded(it.reviews)
        }
    }

    private fun getDescriptionText(appData: AppData?): String {
        if (null != appData)
            return if (viewModel.isDescriptionExpanded.value == true) appData.description else appData.shortDescription
        else
            return ""
    }

    private fun getSuggestedProducts() : Array<Suggestion> {
        return arrayOf(Suggestion(getString(R.string.label_product_name2), R.drawable.gregarious),
                Suggestion(getString(R.string.label_product_name3), R.drawable.byzantium),
                Suggestion(getString(R.string.label_product_name4), R.drawable.cratankerous),
                Suggestion(getString(R.string.label_product_name5), R.drawable.sunsari),
                Suggestion(getString(R.string.label_product_name6), R.drawable.squiggle),
                Suggestion(getString(R.string.label_product_name7), R.drawable.tenacious),
                Suggestion(getString(R.string.label_product_name8), R.drawable.venemial))
    }

    private fun toggleExpandButton() {
        //Invert isDescriptionExpanded
        viewModel.setDescriptionExpanded(viewModel.isDescriptionExpanded.value == false)
    }
}
