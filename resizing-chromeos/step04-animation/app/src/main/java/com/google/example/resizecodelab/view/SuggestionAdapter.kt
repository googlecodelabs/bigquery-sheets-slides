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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.example.resizecodelab.R
import com.google.example.resizecodelab.model.Suggestion

class SuggestionAdapter : RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    private val suggestions = ArrayList<Suggestion>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_suggested, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.name.text = suggestion.name
        holder.image.setImageResource(suggestion.imageId)
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

    fun updateSuggestions (suggestions: Array<Suggestion>) {
        this.suggestions.clear()
        this.suggestions.addAll(suggestions)
        notifyDataSetChanged()
    }

    class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textSuggestedProductName)
        val image: ImageView = itemView.findViewById(R.id.imageSuggestedProduct)
    }
}
