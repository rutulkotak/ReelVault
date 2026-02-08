# Vault Capacity - Quick Reference

## 🎯 What Was Implemented

Added a **50-reel capacity limit** with validation **before** metadata extraction.

---

## 📝 Key Changes

### 1. Repository Method (Domain Layer)
```kotlin
interface LibraryRepository {
    fun getTotalReelsCount(): Flow<Int>
}
```

### 2. Use Case Check (Before Extraction)
```kotlin
// SaveReelFromUrlUseCase.invoke()
val currentCount = libraryRepository.getTotalReelsCount().first()
if (currentCount >= MAX_VAULT_CAPACITY) {
    throw VaultFullException(currentCount)
}
```

### 3. Custom Exception
```kotlin
class VaultFullException(val currentCount: Int) : Exception(...)
```

### 4. Error Handling
```kotlin
catch (e: VaultFullException) {
    SaveResult.Error("Vault is full (${e.currentCount}/50). Delete some reels to save new ones.")
}
```

---

## 🔄 Execution Order

1. ✅ URL validation
2. ✅ **Capacity check (NEW)** ← Stops here if full
3. ✅ Duplicate check
4. ✅ Metadata scraping
5. ✅ Save to database

---

## 📂 Files Modified

1. `ReelVault.sq` → Added `getTotalReelsCount` query
2. `LibraryRepository.kt` → Added interface method
3. `LibraryRepositoryImpl.kt` → Implemented count query
4. `SaveReelFromUrlUseCase.kt` → Added capacity check + exception

---

## 🎨 UI Usage Example

```kotlin
// Observe vault capacity
val count by libraryRepository
    .getTotalReelsCount()
    .collectAsState(initial = 0)

// Display in UI
Text("$count/50 reels")
```

---

## ✅ Status

- [x] SQLDelight query added
- [x] Repository interface updated
- [x] Repository implementation complete
- [x] Use case refactored with check
- [x] Custom exception created
- [x] Builds successfully
- [x] No breaking changes

---

## 🔗 Full Documentation

See [VAULT_CAPACITY_IMPLEMENTATION.md](./VAULT_CAPACITY_IMPLEMENTATION.md) for detailed implementation guide.

---

**Implementation Date:** February 8, 2026  
**Status:** ✅ Complete

