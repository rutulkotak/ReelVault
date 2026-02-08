# Aurora UI Visual Reference

## 🎨 Color Palette

### Primary Colors
```
Deep Indigo:     #1A1B3D  ███ Surface color
Midnight Indigo: #0F1021  ███ Background
Rich Indigo:     #2D2E5F  ███ Container
Bright Indigo:   #4A4DFF  ███ Primary accent
```

### Secondary Colors
```
Soft Violet:     #7B68EE  ███ Secondary accent
Light Violet:    #9B8CFF  ███ Highlights
Dark Violet:     #5B4AC5  ███ Pressed states
Violet Glow:     #B8A4FF  ███ Tag text
```

### Neutral Colors
```
Dark Charcoal:   #1C1C1E  ███ Card background
Medium Charcoal: #2C2C2E  ███ Surface variant
Light Charcoal:  #3A3A3C  ███ Borders
Smoke Gray:      #48484A  ███ Outline
```

### Text Colors
```
Text Primary:    #F5F5F7  ███ Main text
Text Secondary:  #AEAEB2  ███ Supporting text
Text Tertiary:   #636366  ███ Disabled text
```

## 📱 Screen States

### Empty State
```
┌─────────────────────────────────┐
│  ≡  ReelVault                   │ ← Top Bar (Deep Indigo)
├─────────────────────────────────┤
│                                 │
│                                 │
│        ┌─────────┐              │
│        │   🎬    │              │ ← Glassmorphism icon
│        └─────────┘              │
│                                 │
│   Your Vault is Empty           │ ← Headline
│                                 │
│   Start saving your favorite    │
│   reels and build your          │ ← Body text
│   collection                    │
│                                 │
│   ✨ Tap the + button to add    │ ← Hint (Violet Glow)
│      your first reel            │
│                                 │
└─────────────────────────────────┘
```

### Grid View (With Reels)
```
┌─────────────────────────────────┐
│  ≡  ReelVault                   │
├─────────────────────────────────┤
│ ┌─────────┐   ┌─────────┐      │
│ │ [IMAGE] │   │ [IMAGE] │      │
│ │         │   │         │      │
│ │ Reel 1  │   │ Reel 2  │      │
│ │ #tag    │   │ #tag    │      │
│ └─────────┘   └─────────┘      │
│                                 │
│ ┌─────────┐   ┌─────────┐      │
│ │ [IMAGE] │   │ [IMAGE] │      │
│ │         │   │         │      │
│ │ Reel 3  │   │ Reel 4  │      │
│ │ #tag    │   │ #tag    │      │
│ └─────────┘   └─────────┘      │
│                                 │
└─────────────────────────────────┘
```

### Loading State
```
┌─────────────────────────────────┐
│  ≡  ReelVault                   │
├─────────────────────────────────┤
│                                 │
│                                 │
│                                 │
│           ⟳                     │ ← Spinning (Soft Violet)
│                                 │
│                                 │
│                                 │
│                                 │
└─────────────────────────────────┘
```

### Error State
```
┌─────────────────────────────────┐
│  ≡  ReelVault                   │
├─────────────────────────────────┤
│                                 │
│                                 │
│            ⚠️                    │
│                                 │
│   Something went wrong          │
│                                 │
│   [Error message here]          │
│                                 │
│      [ Try Again ]              │ ← Button (Bright Indigo)
│                                 │
└─────────────────────────────────┘
```

## 🃏 ReelCard Component Anatomy

```
┌─────────────────────┐
│  ┌───────────────┐  │ ← Rounded corners (16dp)
│  │               │  │
│  │   THUMBNAIL   │  │ ← 9:16 aspect ratio
│  │               │  │   Gradient overlay
│  │   [  IMAGE  ] │  │   (Glassmorphism effect)
│  │               │  │
│  └───────────────┘  │
│                     │
│  My Amazing Reel    │ ← Title (2 lines max)
│                     │
│  ┌─────┐ ┌──────┐  │ ← Tag chips
│  │#tech│ │#code │  │   (Glassmorphism)
│  └─────┘ └──────┘  │
│                     │
└─────────────────────┘
     Card Background
   (Medium Charcoal)
```

## 🎭 Glassmorphism Effect

The glassmorphism is achieved through:

1. **Overlay Color:** `rgba(255, 255, 255, 0.1)` (10% white)
2. **Border Stroke:** `rgba(255, 255, 255, 0.2)` (20% white)
3. **Blur:** Native backdrop blur or gradient overlay
4. **Usage:**
   - Tag chips
   - Empty state icon container
   - Future: Floating action button
   - Future: Dialog backgrounds

## 📐 Spacing System

```
Extra Small:  4dp   ••
Small:        8dp   ••••
Medium:       12dp  ••••••
Large:        16dp  ••••••••
Extra Large:  24dp  ••••••••••••
Huge:         32dp  ••••••••••••••••
```

## 🔤 Typography Scale

```
Display Large:   57sp / Bold       Headlines
Display Medium:  45sp / Bold       Empty state icons
Display Small:   36sp / Bold

Headline Large:  32sp / SemiBold   Major headings
Headline Medium: 28sp / SemiBold   Screen titles
Headline Small:  24sp / SemiBold   Section titles

Title Large:     22sp / Medium     Card titles
Title Medium:    16sp / Medium     Reel titles
Title Small:     14sp / Medium     Labels

Body Large:      16sp / Normal     Primary content
Body Medium:     14sp / Normal     Secondary content
Body Small:      12sp / Normal     Captions

Label Large:     14sp / Medium     Buttons
Label Medium:    12sp / Medium     Tags
Label Small:     11sp / Medium     Hints
```

## 🎯 Interactive States

### ReelCard
- **Default:** Medium Charcoal background, 4dp elevation
- **Pressed:** Same background, 8dp elevation
- **Hover (Desktop):** Slight scale up animation

### Buttons
- **Default:** Bright Indigo
- **Pressed:** Rich Indigo
- **Disabled:** Light Charcoal

## 🌊 Animations (Future)

- Card tap: Scale down to 0.95, then navigate
- Grid scroll: Parallax effect on images
- Loading: Rotating circular progress
- Empty state: Subtle breathing animation on icon
- Sheet transitions: Slide up with fade

## 🧭 Navigation Flow

```
┌─────────────┐
│  App.kt     │
│  (Entry)    │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ AuroraTheme │
│  (Wrapper)  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Navigator  │
│  (Voyager)  │
└──────┬──────┘
       │
       ▼
┌─────────────┐     Tap Reel
│ LibraryScreen│ ───────────► Open URL in Browser/App
└─────────────┘
```

## 💡 Implementation Notes

1. **Images:** Loaded via Kamel with progressive loading
2. **Grid:** LazyVerticalStaggeredGrid for performance
3. **Theme:** Material3 with custom Aurora colors
4. **State:** MVI pattern (State, Intent, Effect)
5. **Navigation:** Voyager for type-safe routing
6. **DI:** Koin for dependency injection

---

**The Aurora UI brings ReelVault to life with a modern, minimalist, dark-first design that puts content first! 🌌✨**
