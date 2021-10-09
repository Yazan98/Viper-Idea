package com.yazantarifi.models

data class FragmentTemplate(
    val featureName: String,
    val results: ArrayList<String>
) {

    companion object {
        const val RECYCLER_VIEW_FRAGMENT = "recyclerView"
        const val TWO_RECYCLER_VIEWS_FRAGMENT = "2RecyclerViews"
        const val EMPTY_FRAGMENT = "empty"

    }

}
