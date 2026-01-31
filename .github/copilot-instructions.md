# ReelVault Context
- Package: com.reelvault.app
- Architecture: Modular Clean Architecture (Domain, Data, Presentation)
- Pattern: MVVM + MVI (Model-View-Intent)
- Stack: Voyager, Koin, SQLDelight, Ktor, Compose Multiplatform
# Implementation Guidelines
- ViewModels must use MVI with State, Intent, and Effect.
- Business logic only in Domain UseCases.
- Persistence via Repository pattern.
- Network calls via Ktor in Data layer.