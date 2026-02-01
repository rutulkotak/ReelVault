# Phase A: Visual Component Guide

## 🎨 UI Component Hierarchy

```
LibraryScreen
├── TopAppBar (Existing)
│   ├── Title: "ReelVault"
│   └── Settings Button
│
└── LibraryContent
    ├── LibraryHeader (NEW)
    │   ├── GlassmorphismSearchBar
    │   │   ├── 🔍 Search Icon (leading)
    │   │   ├── TextField ("Search your vault...")
    │   │   └── ❌ Clear Icon (trailing, conditional)
    │   │
    │   ├── PlatformFilterRow
    │   │   ├── [All] Chip
    │   │   ├── [Instagram] Chip
    │   │   ├── [YouTube] Chip
    │   │   └── [TikTok] Chip
    │   │
    │   └── Results Count (Animated)
    │       └── "X result(s)"
    │
    ├── ReelGrid (Updated)
    │   └── ReelCard (Updated) x N
    │       ├── Thumbnail (9:16 aspect)
    │       ├── Gradient Overlay
    │       ├── Selection Overlay (NEW, conditional)
    │       │   └── ✓ CheckCircle Icon
    │       ├── Title
    │       └── Tag Chips
    │
    └── SelectionActionBar (NEW, conditional)
        ├── [❌] Clear Button + "X selected"
        └── [🗑️ Delete] Button
```

---

## 📐 Layout Structure

### Normal Mode (No Selection)
```
┌─────────────────────────────────────┐
│  ReelVault                       ⚙️ │ ← TopAppBar
├─────────────────────────────────────┤
│  🔍 Search your vault...         ❌  │ ← Search Bar
│                                      │
│  [All] [Instagram] [YouTube] [TikTok]│ ← Platform Chips
│                                      │
│  5 results                           │ ← Count (if filtering)
├─────────────────────────────────────┤
│  ┌────────┐  ┌────────┐             │
│  │ Reel 1 │  │ Reel 2 │             │ ← Grid
│  │ [Img]  │  │ [Img]  │             │
│  │ Title  │  │ Title  │             │
│  └────────┘  └────────┘             │
│  ┌────────┐  ┌────────┐             │
│  │ Reel 3 │  │ Reel 4 │             │
│  └────────┘  └────────┘             │
│                                      │
└─────────────────────────────────────┘
```

### Selection Mode (Items Selected)
```
┌─────────────────────────────────────┐
│  ReelVault                       ⚙️ │
├─────────────────────────────────────┤
│  🔍 vacation                      ❌  │
│                                      │
│  [All] [Instagram] [YouTube] [TikTok]│
├─────────────────────────────────────┤
│  ┌────────┐  ┌────────┐             │
│  │ ╔══════╗│  │ Reel 2 │             │ ← Selected (border)
│  │ ║ ✓    ║│  │ [Img]  │             │    with checkmark
│  │ ║ [Img]║│  │ Title  │             │
│  │ ╚══════╝│  └────────┘             │
│  ┌────────┐  ┌────────┐             │
│  │ ╔══════╗│  │ Reel 4 │             │
│  │ ║ ✓    ║│  │ [Img]  │             │
│  │ ╚══════╝│  └────────┘             │
├─────────────────────────────────────┤
│  ❌ 2 selected       [🗑️ Delete]    │ ← Action Bar (bottom)
└─────────────────────────────────────┘
```

---

## 🎨 Component States

### LibraryHeader

#### Search Bar States
1. **Empty**
   ```
   ┌──────────────────────────────────┐
   │ 🔍 Search your vault...          │
   └──────────────────────────────────┘
   ```

2. **Active (typing)**
   ```
   ┌──────────────────────────────────┐
   │ 🔍 vacation                    ❌ │ ← Clear button appears
   └──────────────────────────────────┘
   ```

3. **With Results**
   ```
   ┌──────────────────────────────────┐
   │ 🔍 vacation                    ❌ │
   └──────────────────────────────────┘
   3 results ← Fades in below
   ```

#### Platform Chip States
1. **All Selected** (default)
   ```
   [All] [Instagram] [YouTube] [TikTok]
    ^^^
   violet border + glow
   ```

2. **Platform Selected**
   ```
   [All] [Instagram] [YouTube] [TikTok]
           ^^^^^^^^^
         violet border + glow
   ```

---

### ReelCard

#### Normal State
```
┌──────────────┐
│              │
│   [Image]    │  ← 9:16 aspect ratio
│              │
│ ────────────  │  ← Gradient overlay
│ Reel Title   │
│ #tag1 #tag2  │
└──────────────┘
4dp elevation
```

#### Selected State
```
╔══════════════╗  ← 3dp violet border
║              ║
║      ✓       ║  ← Checkmark overlay
║   [Image]    ║     (64dp, centered)
║              ║
║ ────────────  ║
║ Reel Title   ║
║ #tag1 #tag2  ║
╚══════════════╝
8dp elevation (lifted)
```

#### Hover/Press State
```
┌──────────────┐
│              │  ← Slightly darker
│   [Image]    │
│              │
│ ────────────  │
│ Reel Title   │
│ #tag1 #tag2  │
└──────────────┘
8dp elevation
```

---

### SelectionActionBar

#### Hidden (no selection)
```
(No bar visible)
```

#### Visible (items selected)
```
┌────────────────────────────────────┐
│ ❌ 3 selected       [🗑️ Delete]    │  ← Slides up from bottom
└────────────────────────────────────┘
Glassmorphism: DeepIndigo 95% opacity
Border: SoftViolet 50% opacity
```

---

## 🎨 Color Mapping

### LibraryHeader
- **Search Bar Background**: `MediumCharcoal.copy(alpha = 0.6f)`
- **Search Bar Border**: `SoftViolet.copy(alpha = 0.3f)`
- **Placeholder Text**: `TextTertiary`
- **Input Text**: `TextPrimary`
- **Icons**: `SoftViolet`

### Platform Chips
- **Unselected Background**: `MediumCharcoal.copy(alpha = 0.4f)`
- **Unselected Border**: `LightCharcoal`
- **Unselected Text**: `TextSecondary`
- **Selected Background**: `SoftViolet.copy(alpha = 0.3f)`
- **Selected Border**: `SoftViolet`
- **Selected Text**: `TextPrimary`

### ReelCard (Normal)
- **Card Background**: `MediumCharcoal`
- **Gradient Overlay**: `Transparent → MidnightIndigo 70%`
- **Title**: `TextPrimary`
- **Tags**: `VioletGlow`

### ReelCard (Selected)
- **Border**: `SoftViolet` (3dp)
- **Overlay**: `SoftViolet.copy(alpha = 0.4f)`
- **Checkmark Background**: `SoftViolet`
- **Checkmark Icon**: `TextPrimary`

### SelectionActionBar
- **Background**: `DeepIndigo.copy(alpha = 0.95f)`
- **Border**: `SoftViolet.copy(alpha = 0.5f)`
- **Text**: `TextPrimary`
- **Clear Button Icon**: `TextSecondary`
- **Delete Button Background**: `ErrorRed.copy(alpha = 0.2f)`
- **Delete Button Text/Icon**: `ErrorRed`

---

## 📏 Spacing & Sizing

### LibraryHeader
- Outer Padding: `16.dp`
- Component Gaps: `12.dp`
- Search Bar Height: ~`56.dp` (default TextField)
- Search Bar Corner Radius: `16.dp`
- Chip Corner Radius: `20.dp`
- Chip Padding: Horizontal `16.dp`, Vertical `8.dp`

### ReelCard
- Card Corner Radius: `16.dp`
- Content Padding: `12.dp`
- Tag Chip Spacing: `6.dp`
- Normal Elevation: `4.dp`
- Selected Elevation: `8.dp`
- Selected Border: `3.dp`
- Checkmark Icon Size: `64.dp`

### SelectionActionBar
- Outer Padding: `16.dp` (all sides)
- Inner Padding: Horizontal `16.dp`, Vertical `12.dp`
- Corner Radius: `16.dp`
- Border Width: `1.dp`
- Icon Size: `24.dp` (default)

---

## 🎬 Animations

### Search Results Count
```kotlin
AnimatedVisibility(
    visible = searchQuery.isNotEmpty() || selectedPlatform != null,
    enter = fadeIn(),
    exit = fadeOut()
)
```
**Duration**: ~300ms (default)
**Effect**: Smooth fade in/out

### SelectionActionBar
```kotlin
AnimatedVisibility(
    visible = selectedItemIds.isNotEmpty(),
    enter = slideInVertically(initialOffsetY = { it }),  // From bottom
    exit = slideOutVertically(targetOffsetY = { it })   // To bottom
)
```
**Duration**: ~300ms (default)
**Effect**: Slide up from bottom edge

### ReelCard Selection
- **Border**: Instant appearance (0ms)
- **Overlay**: Instant appearance (0ms)
- **Elevation**: Smooth transition (200ms default)
- **Checkmark**: Scales in with overlay

---

## 🎯 Interactive Elements

### Tap Targets
All interactive elements meet minimum 48dp tap target:
- Search bar: Full width x 56dp height ✓
- Platform chips: ~80-100dp width x 40dp height ✓
- ReelCard: Full card width/height ✓
- Action bar buttons: 48dp minimum ✓

### Gestures
1. **Search Bar**
   - Tap → Focus & show keyboard
   - Type → Update search query
   - Tap X → Clear search

2. **Platform Chips**
   - Tap → Toggle filter

3. **ReelCard (Normal Mode)**
   - Tap → Open URL
   - Long-press → Enter selection mode + select card

4. **ReelCard (Selection Mode)**
   - Tap → Toggle selection
   - Long-press → Toggle selection

5. **SelectionActionBar**
   - Tap X → Clear all selections
   - Tap Delete → Delete selected items

---

## 🔄 State Flow

### Search Flow
```
User types in search bar
    ↓
UpdateSearchQuery Intent
    ↓
ViewModel updates state.searchQuery
    ↓
filteredReels computed property re-evaluates
    ↓
ReelGrid receives new filtered list
    ↓
UI updates (recomposes)
```

### Selection Flow
```
User long-presses ReelCard
    ↓
ToggleSelection Intent (reel.id)
    ↓
ViewModel updates state.selectedItemIds
    ↓
ReelGrid receives new selectedItemIds set
    ↓
ReelCard shows selection overlay
    ↓
SelectionActionBar slides up
```

### Deletion Flow
```
User taps Delete in ActionBar
    ↓
DeleteSelectedItems Intent
    ↓
ViewModel calls DeleteReelsUseCase
    ↓
UseCase calls Repository.deleteReels()
    ↓
Repository deletes from SQLDelight
    ↓
Success: ItemsDeleted Effect emitted
    ↓
LibraryScreen shows snackbar
    ↓
State clears selectedItemIds
    ↓
ActionBar slides down
```

---

## 🎨 Design Philosophy

### Glassmorphism
- Semi-transparent backgrounds
- Subtle borders for definition
- Layered depth with elevation
- Blur effect (simulated with opacity)

### Aurora Theme
- Deep purples and indigos for depth
- Soft violet for interactive elements
- Charcoal for cards and surfaces
- White text with hierarchy

### Motion
- Quick, purposeful animations (300ms)
- Slide for entrance/exit
- Fade for content changes
- No unnecessary motion

### Accessibility
- Minimum 48dp touch targets
- High contrast text (WCAG AA+)
- Clear visual feedback
- Semantic labels for screen readers

---

**Created**: February 1, 2026  
**For**: Phase A - Management Foundations  
**Framework**: Compose Multiplatform
