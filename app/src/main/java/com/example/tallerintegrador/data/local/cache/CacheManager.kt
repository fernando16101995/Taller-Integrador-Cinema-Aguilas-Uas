package com.example.tallerintegrador.data.local.cache

import android.content.Context
import com.example.tallerintegrador.data.model.pelicula
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * ✅ Gestor centralizado de cache
 *
 * Maneja la estrategia de cache-first con fallback a red
 */
class CacheManager(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val peliculaDao = database.peliculaCacheDao()
    private val favoritoDao = database.favoritoCacheDao()

    companion object {
        // Cache válido por 1 hora
        private const val CACHE_VALIDITY_MS = 60 * 60 * 1000L // 1 hora
    }

    // ========== PELÍCULAS ==========

    /**
     * Verifica si el cache de películas es válido
     */
    suspend fun isPeliculasCacheValid(): Boolean {
        val validTimestamp = System.currentTimeMillis() - CACHE_VALIDITY_MS
        val hasData = peliculaDao.getCacheSize() > 0
        val isValid = peliculaDao.isCacheValid(validTimestamp)
        return hasData && isValid
    }

    /**
     * Obtiene películas del cache
     */
    suspend fun getCachedPeliculas(): List<pelicula>? {
        return if (isPeliculasCacheValid()) {
            peliculaDao.getAllPeliculas().map { it.toPelicula() }
        } else {
            null
        }
    }

    /**
     * Obtiene una película específica del cache
     */
    suspend fun getCachedPelicula(id: Int): pelicula? {
        return peliculaDao.getPeliculaById(id)?.toPelicula()
    }

    /**
     * Guarda películas en cache
     */
    suspend fun cachePeliculas(peliculas: List<pelicula>) {
        val entities = peliculas.map { it.toCacheEntity() }
        peliculaDao.insertPeliculas(entities)
    }

    /**
     * Guarda una película individual en cache
     */
    suspend fun cachePelicula(pelicula: pelicula) {
        peliculaDao.insertPelicula(pelicula.toCacheEntity())
    }

    /**
     * Limpia el cache de películas
     */
    suspend fun clearPeliculasCache() {
        peliculaDao.clearCache()
    }

    // ========== FAVORITOS ==========

    /**
     * Obtiene los IDs de favoritos del cache
     */
    suspend fun getCachedFavoritosIds(): List<Int> {
        return favoritoDao.getAllFavoritos().map { it.peliculaId }
    }

    /**
     * Flow reactivo de IDs de favoritos
     */
    fun getFavoritosIdsFlow(): Flow<Set<Int>> {
        return favoritoDao.getAllFavoritosFlow().map { list ->
            list.map { it.peliculaId }.toSet()
        }
    }

    /**
     * Verifica si una película es favorita (suspending)
     */
    suspend fun isFavorito(peliculaId: Int): Boolean {
        return favoritoDao.isFavorito(peliculaId)
    }

    /**
     * Verifica si una película es favorita (Flow reactivo)
     */
    fun isFavoritoFlow(peliculaId: Int): Flow<Boolean> {
        return favoritoDao.isFavoritoFlow(peliculaId)
    }

    /**
     * Agrega un favorito al cache
     */
    suspend fun addFavoritoToCache(peliculaId: Int) {
        favoritoDao.insertFavorito(FavoritoCacheEntity(peliculaId))
    }

    /**
     * Elimina un favorito del cache
     */
    suspend fun removeFavoritoFromCache(peliculaId: Int) {
        favoritoDao.deleteFavorito(peliculaId)
    }

    /**
     * Sincroniza favoritos con el servidor
     * Reemplaza completamente el cache local
     */
    suspend fun syncFavoritos(peliculaIds: List<Int>) {
        favoritoDao.syncFavoritos(peliculaIds)
    }

    /**
     * Limpia el cache de favoritos
     */
    suspend fun clearFavoritosCache() {
        favoritoDao.clearCache()
    }

    // ========== UTILIDADES ==========

    /**
     * Limpia todo el cache
     */
    suspend fun clearAllCache() {
        clearPeliculasCache()
        clearFavoritosCache()
    }
}