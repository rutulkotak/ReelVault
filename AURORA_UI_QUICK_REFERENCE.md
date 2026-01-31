# Phase 3: Aurora UI - Quick Reference

## 🎨 Using the Aurora Theme

```kotlin
import com.reelvault.app.presentation.theme.AuroraTheme

@Composable
fun MyScreen() {
    AuroraTheme {
        // Your UI here
    }
}
```

## 🎨 Using Aurora Colors

```kotlin
import com.reelvault.app.presentation.theme.AuroraColors

// In your composables:
Box(
    modifier = Modifier.background(AuroraColors.MidnightIndigo)
)

Text(
    text = "Hello",
    color = AuroraColors.TextPrimary
)
```

## 🃏 Using ReelCard

```kotlin
import com.reelvault.app.presentation.components.ReelCard
import com.reelvault.app.domain.model.Reel

ReelCard(
    reel = myReel,
    onClick = { /* Handle click */ }
)
```

## 📱 Using ReelGrid

```kotlin
import com.reelvault.app.presentation.components.ReelGrid

ReelGrid(
    reels = listOfReels,
    onReelClick = { reel -> /* Handle click */ },
    columns = 2 // Optional, defaults to 2
)
```

## 🔗 Opening URLs

```kotlin
import com.reelvault.app.utils.PlatformUrlOpener

// From anywhere in your code:
PlatformUrlOpener.openUrl("https://instagram.com/reel/...")
```

## 🏗 Adding New Screens (Voyager)

```kotlin
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class MyScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        // Your UI
        
        // Navigate:
        navigator?.push(AnotherScreen())
    }
}
```

## 📐 Common Modifiers

```kotlin
// Standard padding
modifier = Modifier.padding(16.dp)

// Fill max size with background
modifier = Modifier
    .fillMaxSize()
    .background(AuroraColors.MidnightIndigo)

// Glassmorphism effect
modifier = Modifier
    .clip(RoundedCornerShape(8.dp))
    .background(AuroraColors.GlassOverlay)
    .border(1.dp, AuroraColors.GlassStroke, RoundedCornerShape(8.dp))
```

## 🎭 Typography Usage

```kotlin
Text(
    text = "Headline",
    style = MaterialTheme.typography.headlineMedium,
    color = AuroraColors.TextPrimary
)

Text(
    text = "Body text",
    style = MaterialTheme.typography.bodyMedium,
    color = AuroraColors.TextSecondary
)
```

## 🔄 MVI Pattern (ViewModel)

```kotlin
// In your ViewModel:
override fun onIntent(intent: MyContract.Intent) {
    when (intent) {
        is MyContract.Intent.DoSomething -> {
            updateState { copy(loading = true) }
            emitEffect(MyContract.Effect.ShowMessage("Done"))
        }
    }
}

// In your Screen:
LaunchedEffect(Unit) {
    viewModel.effect.collectLatest { effect ->
        when (effect) {
            is MyContract.Effect.ShowMessage -> {
                snackbarHostState.showSnackbar(effect.message)
            }
        }
    }
}
```

## 📦 Key Files to Know

```
presentation/
├── theme/
│   ├── Color.kt         → AuroraColors object
│   ├── Theme.kt         → AuroraTheme composable
│   └── Typography.kt    → AuroraTypography
├── components/
│   ├── ReelCard.kt      → ReelCard composable
│   ├── ReelGrid.kt      → ReelGrid composable
│   └── EmptyLibraryState.kt → EmptyLibraryState composable
└── library/
    ├── LibraryScreen.kt → Main screen example
    ├── LibraryContract.kt → State/Intent/Effect
    └── LibraryViewModel.kt → MVI ViewModel

utils/
└── PlatformUrlOpener.kt → URL opening utility
```

## 🚀 Quick Start

1. **Sync Gradle** to get Kamel dependency
2. **Run app** - should show empty state
3. **Add test data** to see ReelCards
4. **Tap a card** to open URL

## 💡 Pro Tips

- Use `AuroraColors` for consistency
- Follow MVI pattern for all screens
- Use Voyager for navigation
- Leverage Kamel for image loading
- Apply glassmorphism sparingly
- Keep the dark theme pure (no light mode yet)

---

**Happy coding with Aurora UI! 🌌✨**
