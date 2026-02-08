package com.reelvault.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.reelvault.app.database.ReelVaultDatabase
import com.reelvault.app.domain.model.Collection
import com.reelvault.app.domain.repository.CollectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementation of CollectionRepository using SQLDelight for local persistence.
 */
class CollectionRepositoryImpl(
    private val database: ReelVaultDatabase
) : CollectionRepository {

    private val queries = database.reelVaultQueries

    override fun getCollections(): Flow<List<Collection>> {
        return queries.getAllCollections()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbCollections ->
                dbCollections.map { result ->
                    Collection(
                        id = result.id,
                        name = result.name,
                        color = result.color,
                        icon = result.icon,
                        reelCount = result.reelCount?.toInt() ?: 0
                    )
                }
            }
    }

    override suspend fun getCollectionCount(): Int = withContext(Dispatchers.IO) {
        queries.getCollectionCount().executeAsOne().toInt()
    }

    override suspend fun getCollectionById(id: Long): Collection? = withContext(Dispatchers.IO) {
        queries.getCollectionById(id).executeAsOneOrNull()?.let { result ->
            Collection(
                id = result.id,
                name = result.name,
                color = result.color,
                icon = result.icon,
                reelCount = 0 // Single query doesn't include count
            )
        }
    }

    override suspend fun createCollection(name: String, color: String, icon: String): Long =
        withContext(Dispatchers.IO) {
            queries.insertCollection(name = name, color = color, icon = icon)
            queries.transactionWithResult {
                database.reelVaultQueries.lastInsertRowId().executeAsOne()
            }
        }

    override suspend fun updateCollection(id: Long, name: String, color: String, icon: String) =
        withContext(Dispatchers.IO) {
            queries.updateCollection(name = name, color = color, icon = icon, id = id)
        }

    override suspend fun deleteCollection(id: Long) = withContext(Dispatchers.IO) {
        queries.deleteCollection(id)
    }
}
