package com.yazantarifi.models

data class FeatureInfo(
    val packageName: String,
    val featureName: String,
    val isViewModelGenerated: Boolean = false,
    val isScreenNavigationComponent: Boolean = false,
    val isFragmentInstanceEnabled: Boolean = false,
    val isFragmentsGeneratedOnly: Boolean = false,
    val isFragmentListGenerated: Boolean = false,
    val isHelperClassEnabled: Boolean = false
)
