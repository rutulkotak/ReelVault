# Phase A: Setup & Troubleshooting Guide

## 🚀 Quick Setup

### 1. Sync Gradle Dependencies
After implementing Phase A, you need to sync the new Material Icons Extended dependency:

```bash
cd /path/to/ReelVault
./gradlew clean
./gradlew build
```

**Or in Android Studio:**
1. File → Sync Project with Gradle Files
2. Wait for sync to complete
3. Build → Rebuild Project

### 2. Verify Material Icons Import
The following dependency was added to `composeApp/build.gradle.kts`:

```kotlin
commonMain.dependencies {
    // Material Icons Extended
    implementation("org.jetbrains.compose.material:material-icons-extended:1.6.10")
    // ... other dependencies
}
```

If icons still show errors after sync:
1. Invalidate Caches: File → Invalidate Caches / Restart
2. Clean build folder: `./gradlew clean`
3. Rebuild: `./gradlew build`

---

## 🔧 Common Issues

### Issue 1: "Unresolved reference 'icons'"

**Symptom:**
```kotlin
import androidx.compose.material.icons.Icons  // Error: Unresolved reference
```

**Solution:**
1. Ensure Gradle sync completed successfully
2. Check `build.gradle.kts` has the Material Icons dependency
3. Run: `./gradlew --refresh-dependencies`
4. Restart IDE

**Verification:**
Icons should import without errors:
```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CheckCircle
```

---

### Issue 2: "Unresolved reference 'ErrorRed'"

**Symptom:**
```kotlin
AuroraColors.ErrorRed  // Error: Unresolved reference
```

**Solution:**
The property exists in `Color.kt` as an alias. IDE might need a refresh:
1. File → Invalidate Caches / Restart
2. Or manually restart the IDE

**Verification:**
Check `/presentation/theme/Color.kt` line 37:
```kotlin
val ErrorRed = AuroraRed  // Should exist
```

---

### Issue 3: SQLDelight Generation Errors

**Symptom:**
Queries not recognized after adding `searchReels`

**Solution:**
```bash
./gradlew :composeApp:generateCommonMainReelVaultDatabaseInterface
```

**Verification:**
Generated code should appear in:
`build/generated/sqldelight/code/ReelVaultDatabase/commonMain/`

---

### Issue 4: Koin Injection Errors

**Symptom:**
```
No definition found for DeleteReelsUseCase
```

**Solution:**
Verify `LibraryModule.kt` includes:
```kotlin
factoryOf(::DeleteReelsUseCase)
```

**Rebuild:**
```bash
./gradlew clean build
```

---

## ✅ Verification Checklist

After setup, verify:

- [ ] Project builds successfully: `./gradlew build`
- [ ] No import errors in:
  - `LibraryHeader.kt`
  - `SelectionActionBar.kt`
  - `ReelCard.kt`
- [ ] SQLDelight queries generated
- [ ] Koin modules loaded without errors
- [ ] App runs on device/emulator

---

## 🧪 Testing Phase A Features

### 1. Test Search
**Steps:**
1. Launch app
2. Tap search bar
3. Type "vacation"
4. Verify results filter instantly
5. Tap X to clear
6. Verify all reels show again

**Expected:**
- Results update as you type
- Clear button appears when typing
- Results count shows filtered amount

---

### 2. Test Platform Filtering
**Steps:**
1. Tap "Instagram" chip
2. Verify only Instagram reels show
3. Tap "All" chip
4. Verify all reels show again

**Expected:**
- Selected chip has violet border
- Grid updates immediately
- Combines with search if both active

---

### 3. Test Multi-Selection
**Steps:**
1. Long-press any reel
2. Verify selection overlay appears
3. Tap another reel
4. Verify it also selects
5. Tap X in action bar
6. Verify selections clear

**Expected:**
- Long-press enters selection mode
- Selected cards show checkmark + border
- Action bar slides up from bottom
- Selection count updates

---

### 4. Test Batch Deletion
**Steps:**
1. Long-press to select 3 reels
2. Tap Delete button in action bar
3. Verify snackbar shows "3 item(s) deleted"
4. Verify reels removed from grid
5. Verify action bar slides down

**Expected:**
- Deletion happens immediately
- Grid updates (reels disappear)
- Selection clears automatically
- Snackbar shows count

---

### 5. Test Combined Filters
**Steps:**
1. Type "fun" in search
2. Select "YouTube" platform
3. Verify results match BOTH filters
4. Clear search
5. Verify only YouTube filter remains

**Expected:**
- Both filters apply simultaneously
- Results count shows combined filtering
- Clearing one filter keeps the other

---

## 📊 Expected Behavior Matrix

| User Action | State Change | UI Update |
|------------|--------------|-----------|
| Type in search | `searchQuery` updated | Grid filters instantly |
| Select platform chip | `selectedPlatform` updated | Grid filters by URL |
| Long-press reel | Add to `selectedItemIds` | Card shows overlay + border |
| Tap selected reel | Remove from `selectedItemIds` | Overlay disappears |
| Tap Delete | Call `DeleteReelsUseCase` | Snackbar + grid updates |
| Tap Clear (X) | Clear `selectedItemIds` | All overlays disappear |

---

## 🐛 Debug Tips

### Enable Logging
Add to ViewModel methods:
```kotlin
private fun onToggleSelection(id: String) {
    println("DEBUG: Toggling selection for: $id")
    updateState {
        val newSelection = if (id in selectedItemIds) {
            selectedItemIds - id
        } else {
            selectedItemIds + id
        }
        println("DEBUG: New selection: $newSelection")
        copy(selectedItemIds = newSelection)
    }
}
```

### Check State
Add to `LibraryScreen.kt`:
```kotlin
LaunchedEffect(state) {
    println("DEBUG State: ${state.selectedItemIds.size} selected, ${state.filteredReels.size} filtered")
}
```

### Verify Intent Dispatch
Add to `LibraryScreen.kt`:
```kotlin
onReelLongClick = { reel ->
    println("DEBUG: Long-press on ${reel.id}")
    onIntent(LibraryContract.Intent.ToggleSelection(reel.id))
}
```

---

## 🔄 Rollback Instructions

If Phase A causes issues:

### 1. Revert Code Changes
```bash
git checkout HEAD~1 -- composeApp/src/
```

### 2. Revert Dependencies
Remove from `build.gradle.kts`:
```kotlin
implementation("org.jetbrains.compose.material:material-icons-extended:1.6.10")
```

### 3. Revert Database
If SQLDelight changes cause issues:
```bash
git checkout HEAD~1 -- composeApp/src/commonMain/sqldelight/
```

### 4. Clean Build
```bash
./gradlew clean
./gradlew build
```

---

## 📱 Platform-Specific Notes

### Android
- Material Icons library works out of the box
- SQLDelight driver: `AndroidSqliteDriver`
- Test on API 24+ (min SDK)

### iOS
- Material Icons work via Compose Multiplatform
- SQLDelight driver: `NativeSqliteDriver`
- Test on iOS 14+ (min version)

**Known iOS Limitations:**
- Long-press gesture might need additional configuration
- Haptic feedback not implemented (can be added)

---

## 🎯 Performance Notes

### Expected Performance
- Search: <16ms per keystroke (instant)
- Platform filter: <16ms (instant)
- Selection toggle: <16ms (instant)
- Deletion: <100ms for batch of 10 items

### If Slow
1. Check if debug mode (use release build)
2. Verify not running on old device/simulator
3. Profile with Android Profiler / Instruments

### Optimization Tips
- Search already uses computed property (efficient)
- Grid uses `key = { reel.id }` (optimized recomposition)
- Selection uses immutable `Set` (efficient lookup)

---

## 🚀 Next Steps After Verification

Once Phase A is working:

### Immediate
1. ✅ Test all features manually
2. ✅ Fix any UI glitches
3. ✅ Test on both Android and iOS
4. ✅ Get user feedback

### Short-term
1. Add unit tests for UseCases
2. Add UI tests for selection flow
3. Add analytics events
4. Improve error messages

### Long-term
1. Implement Phase B (Collections/Organization)
2. Add search history
3. Add bulk export
4. Add undo for deletions

---

## 📞 Support Resources

### Documentation
- `PHASE_A_MANAGEMENT_FOUNDATIONS.md` - Complete implementation guide
- `PHASE_A_VISUAL_GUIDE.md` - UI component specifications
- `.github/copilot-instructions.md` - Architecture guidelines

### Code References
- State management: `LibraryContract.kt`
- Business logic: `DeleteReelsUseCase.kt`
- UI components: `components/` folder
- Data layer: `repository/LibraryRepositoryImpl.kt`

### Community
- Compose Multiplatform: https://www.jetbrains.com/compose-multiplatform/
- Koin DI: https://insert-koin.io/
- SQLDelight: https://cashapp.github.io/sqldelight/

---

**Last Updated**: February 1, 2026  
**Version**: 1.0.0  
**Status**: ✅ Implementation Complete
