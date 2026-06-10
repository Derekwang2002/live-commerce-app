package com.puskal.data.model

object VideoAsset {
    const val DIRECTORY = "vidoe"
    const val LIVE_DIRECTORY = "live"
    const val AD_DIRECTORY = "ad"

    fun assetPath(fileName: String, directory: String = DIRECTORY): String = "$directory/$fileName"

    fun assetUri(fileName: String, directory: String = DIRECTORY): String =
        "asset:///${assetPath(fileName, directory)}"

    fun liveAssetPath(fileName: String): String = "$LIVE_DIRECTORY/$fileName"

    fun liveAssetUri(fileName: String): String = "asset:///${liveAssetPath(fileName)}"
}
