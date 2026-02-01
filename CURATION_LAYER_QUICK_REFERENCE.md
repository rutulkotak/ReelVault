# ReelVault Curation Layer - Quick Reference

## 🎯 Overview
The Curation Layer allows users to organize saved reels into collections, add notes, and manage metadata before AI processing.

---

## 📚 Core Concepts

### Collections
- **User-created categories** for organizing reels (e.g., "Gym", "Recipes", "Travel")
- Each has a **name**, **color**, and **icon** (emoji)
- Reels can belong to **one collection or none**
- Deleting a collection sets reels to **uncategorized**

### Reel Metadata
- **Title**: Editable display name
- **Notes**: Personal notes/context for the reel
- **Tags**: Comma-separated keywords
- **Collection**: Optional assignment to a collection

---

## 🔧 Usage Examples

### Creating a Collection

```kotlin
// In your Composable
val viewModel: CollectionsViewModel = koinViewModel()

// Dispatch intent
viewModel.onIntent(
    CollectionsContract.Intent.CreateCollection(
        name = "Fitness",
        color = "#0A84FF",
        icon = "💪"
    )
)

// Observe effect
LaunchedEffect(Unit) {
    viewModel.effect.collectLatest { effect ->
        when (effect) {
            is CollectionsContract.Effect.CollectionCreated -> {
                // Show success message
            }
        }
    }
}
```

### Updating Reel Details

```kotlin
// In your Composable
val viewModel: LibraryViewModel = koinViewModel()

// Update reel
viewModel.onIntent(
    LibraryContract.Intent.UpdateReelDetails(
        id = "reel-123",
        title = "Morning Workout Routine",
        notes = "Great exercises for core strength",
        tags = listOf("fitness", "workout", "morning"),
        collectionId = 1L  // Fitness collection
    )
)
```

### Filtering by Collection

```kotlin
// Show all reels
viewModel.onIntent(
    LibraryContract.Intent.FilterByCollection(null)
)

// Show only uncategorized reels
viewModel.onIntent(
    LibraryContract.Intent.FilterByCollection(-1L)
)

// Show reels in specific collection
viewModel.onIntent(
    LibraryContract.Intent.FilterByCollection(collectionId = 1L)
)
```

### Moving Reels to Collection

```kotlin
// Move single or multiple reels
viewModel.onIntent(
    LibraryContract.Intent.MoveToCollection(
        reelIds = listOf("reel-1", "reel-2", "reel-3"),
        collectionId = 2L  // Recipes collection
    )
)

// Remove from collection (set to uncategorized)
viewModel.onIntent(
    LibraryContract.Intent.MoveToCollection(
        reelIds = listOf("reel-1"),
        collectionId = null
    )
)
```

---

## 🎨 UI Components

### ReelDetailScreen

```kotlin
// Navigate to detail screen
ReelDetailScreen(
    reel = selectedReel,
    collections = allCollections,
    onSave = { title, notes, tags, collectionId ->
        viewModel.onIntent(
            LibraryContract.Intent.UpdateReelDetails(
                id = selectedReel.id,
                title = title,
                notes = notes,
                tags = tags,
                collectionId = collectionId
            )
        )
    }
)
```

### CollectionsScreen

```kotlin
// Add to navigator
navigator.push(CollectionsScreen())

// The screen handles all interactions internally
```

---

## 🗄️ Database Queries

### Get Reels by Collection

```kotlin
// In Repository
fun getReelsByCollection(collectionId: Long): Flow<List<Reel>> {
    return queries.getReelsByCollection(collectionId)
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { it.map { dbReel -> dbReel.toDomainModel() } }
}
```

### Get Collections with Counts

```kotlin
// In Repository
fun getCollections(): Flow<List<Collection>> {
    return queries.getAllCollections()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { results ->
            results.map { result ->
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
```

---

## 🔍 State Management

### Collection State

```kotlin
data class State(
    val isLoading: Boolean = true,
    val collections: List<Collection> = emptyList(),
    val errorMessage: String? = null
) : MviContract.UiState
```

### Library State (Updated)

```kotlin
data class State(
    val isLoading: Boolean = true,
    val reels: List<Reel> = emptyList(),
    val searchQuery: String = "",
    val selectedTags: Set<String> = emptySet(),
    val selectedCollectionId: Long? = null,  // NEW
    val selectedItemIds: Set<String> = emptySet()
) : MviContract.UiState
```

---

## 🎬 MVI Flow Example

### Complete Curation Flow

```kotlin
// 1. USER ACTION: Create collection
Button(onClick = {
    viewModel.onIntent(
        CollectionsContract.Intent.CreateCollection("Travel", "#FFD60A", "✈️")
    )
})

// 2. VIEWMODEL: Handle intent
private fun onCreateCollection(name: String, color: String, icon: String) {
    viewModelScope.launch {
        val result = createCollectionUseCase(name, color, icon)
        if (result.isSuccess) {
            emitEffect(CollectionsContract.Effect.CollectionCreated(name))
        }
    }
}

// 3. USE CASE: Business logic
suspend operator fun invoke(name: String, color: String, icon: String): Result<Long> {
    return try {
        val id = collectionRepository.createCollection(name, color, icon)
        Result.success(id)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// 4. REPOSITORY: Data persistence
override suspend fun createCollection(name: String, color: String, icon: String): Long {
    return withContext(Dispatchers.IO) {
        queries.insertCollection(name, color, icon)
        queries.transactionWithResult {
            database.reelVaultQueries.lastInsertRowId().executeAsOne()
        }
    }
}

// 5. UI: Observe effect
ObserveEffect(viewModel.effect) { effect ->
    when (effect) {
        is CollectionsContract.Effect.CollectionCreated -> {
            snackbarHostState.showSnackbar("Collection '${effect.name}' created")
        }
    }
}

// 6. STATE UPDATE: Collections list auto-updates via Flow
val state by viewModel.uiState.collectAsState()
// state.collections now includes the new collection
```

---

## 🧩 Integration Points

### With Library Screen

```kotlin
// In LibraryScreen.kt
LaunchedEffect(Unit) {
    viewModel.effect.collectLatest { effect ->
        when (effect) {
            is LibraryContract.Effect.NavigateToReelDetail -> {
                // TODO: Navigate to ReelDetailScreen
                navigator.push(ReelDetailScreen(
                    reel = effect.reel,
                    collections = collectionsState.collections,
                    onSave = { title, notes, tags, collectionId ->
                        libraryViewModel.onIntent(
                            LibraryContract.Intent.UpdateReelDetails(
                                id = effect.reel.id,
                                title = title,
                                notes = notes,
                                tags = tags,
                                collectionId = collectionId
                            )
                        )
                    }
                ))
            }
        }
    }
}
```

### With ReelCard

```kotlin
// Update ReelCard onClick to navigate to detail
ReelCard(
    reel = reel,
    onClick = {
        viewModel.onIntent(
            LibraryContract.Intent.NavigateToDetail(reel)
        )
    }
)
```

---

## 🎨 Color Presets

Available in `CollectionsScreen.kt`:

```kotlin
val colorOptions = listOf(
    "#FF6B9D",  // Pink
    "#6B9DFF",  // Blue
    "#9DFF6B",  // Green
    "#FFD76B",  // Yellow
    "#B76BFF",  // Purple
    "#6BFFD7"   // Cyan
)
```

## 🎭 Icon Presets

```kotlin
val iconOptions = listOf(
    "📁",  // Folder
    "💪",  // Fitness
    "🍳",  // Cooking
    "🎬",  // Movies
    "🎨",  // Art
    "📚",  // Books
    "✈️",  // Travel
    "💼"   // Work
)
```

---

## 🔐 Type Safety

### Domain Models

```kotlin
// Collection
data class Collection(
    val id: Long,
    val name: String,
    val color: String,      // Hex string
    val icon: String,       // Emoji
    val reelCount: Int = 0
)

// Updated Reel
data class Reel(
    val id: String,
    val url: String,
    val title: String,
    val thumbnail: String,
    val tags: List<String>,
    val createdAt: Instant,
    val collectionId: Long? = null,  // NEW
    val notes: String? = null         // NEW
)
```

---

## 🧪 Testing Tips

### Unit Test Example

```kotlin
@Test
fun `createCollection should emit success effect`() = runTest {
    // Given
    val useCase = CreateCollectionUseCase(mockRepository)
    coEvery { mockRepository.createCollection(any(), any(), any()) } returns 1L
    
    // When
    val result = useCase("Travel", "#FFD60A", "✈️")
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals(1L, result.getOrNull())
}
```

### Integration Test Example

```kotlin
@Test
fun `filtering by collection should show only related reels`() = runTest {
    // Given
    val viewModel = LibraryViewModel(...)
    
    // When
    viewModel.onIntent(LibraryContract.Intent.FilterByCollection(1L))
    
    // Then
    val state = viewModel.uiState.first()
    assertEquals(1L, state.selectedCollectionId)
}
```

---

## 📊 Performance Tips

1. **Use Flows**: Collections and reels auto-update when data changes
2. **Batch Operations**: Use `moveReelsToCollection` for multiple reels
3. **Lazy Loading**: SQLDelight queries are lazy and efficient
4. **Memoization**: `filteredReels` is a computed property, not stored

---

## 🚨 Common Issues

### Issue: Collection not showing after creation
**Solution**: Ensure you're collecting the `getCollections()` Flow

### Issue: Reel doesn't update after edit
**Solution**: Check that `updateReelDetails` is called with correct ID

### Issue: Colors not rendering correctly
**Solution**: Use `parseHexColor()` helper function for multiplatform support

---

## 📚 Additional Resources

- **Full Implementation**: See `CURATION_LAYER_IMPLEMENTATION.md`
- **Architecture Guide**: See `copilot-instructions.md`
- **Aurora UI**: See `AURORA_UI_QUICK_REFERENCE.md`

---

*Last Updated: February 1, 2026*
