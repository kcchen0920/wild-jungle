package com.game.wildcatcher.presentation.util

import android.graphics.Bitmap
import android.util.LruCache

/**
 * ASTC & PBR 貼圖高效能動態快取管理器 (Texture & Surface Memory Manager)
 * - 採用 LRU Cache 快取機制，防止 2K/4K PBR 材質頻繁解碼與記憶體洩漏 (OOM)
 * - 限制快取容量為應用可用記憶體的 1/8，鎖定 60 FPS 平板流暢度
 */
object TextureCacheManager {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8

    private val memoryCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    fun getTexture(key: String): Bitmap? {
        return memoryCache.get(key)
    }

    fun putTexture(key: String, bitmap: Bitmap) {
        if (getTexture(key) == null) {
            memoryCache.put(key, bitmap)
        }
    }

    fun clearCache() {
        memoryCache.evictAll()
    }
}
