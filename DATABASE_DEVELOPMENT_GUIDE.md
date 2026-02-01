# Database Schema Changes - Development Guide

## 🔧 During Development

When you make schema changes (add/remove columns, tables, etc.), simply:

### Android
1. **Uninstall the app** from your device/emulator
2. **Reinstall** by running the app again

Or via command line:
```bash
# Uninstall
adb uninstall com.reelvault.app

# Then rebuild and run
./gradlew installDebug
```

### iOS
1. **Delete the app** from simulator/device
2. **Run again** from Xcode

Or clean simulator:
```bash
# For simulator
xcrun simctl uninstall booted com.reelvault.app

# Or erase all data
xcrun simctl erase all
```

---

## 🚀 Before Production Release

When you're ready to ship to users, you'll need to implement proper database migrations. For future reference:

### SQLDelight Migration Strategy

1. **Version your schema** in `build.gradle.kts`:
```kotlin
sqldelight {
    databases {
        create("ReelVaultDatabase") {
            packageName.set("com.reelvault.app.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            version = 2  // Increment when schema changes
        }
    }
}
```

2. **Create migration files** in `sqldelight/migrations/`:
```sql
-- 1.sqm (from version 1 to 2)
ALTER TABLE Reel ADD COLUMN collectionId INTEGER;
ALTER TABLE Reel ADD COLUMN notes TEXT;

CREATE TABLE Collection (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    color TEXT NOT NULL,
    icon TEXT NOT NULL
);
```

3. **Implement migration callback** in DatabaseDriverFactory:
```kotlin
AndroidSqliteDriver(
    schema = ReelVaultDatabase.Schema,
    context = context,
    name = "reelvault.db",
    callback = AndroidSqliteDriver.Callback(
        schema = ReelVaultDatabase.Schema
    )
)
```

---

## ⚠️ Current State

**Status**: Development mode - No migrations implemented

**Action Required**: Uninstall and reinstall app after schema changes

**Reason**: SQLDelight creates fresh database on first run, which includes all new columns/tables automatically.

---

## 📋 Schema Change Checklist

When you modify the schema:

1. ✅ Update `.sq` file with new tables/columns
2. ✅ Update domain models (e.g., `Reel.kt`, `Collection.kt`)
3. ✅ Update repository implementations
4. ✅ Run `./gradlew generateCommonMainReelVaultDatabaseInterface`
5. ✅ **Uninstall app from device/emulator**
6. ✅ Rebuild and run app
7. ✅ Test new features

---

## 🎯 Current Schema (Version 2)

### Tables

**Collection**:
```sql
CREATE TABLE Collection (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    color TEXT NOT NULL,
    icon TEXT NOT NULL
);
```

**Reel**:
```sql
CREATE TABLE Reel (
    id TEXT PRIMARY KEY,
    url TEXT UNIQUE NOT NULL,
    title TEXT NOT NULL,
    thumbnailUrl TEXT NOT NULL,
    tags TEXT NOT NULL,
    createdAt INTEGER NOT NULL,
    collectionId INTEGER,
    notes TEXT,
    FOREIGN KEY (collectionId) REFERENCES Collection(id) ON DELETE SET NULL
);
```

---

## 💡 Pro Tips

### Quick Clean Install (Android)
```bash
# One-liner to uninstall, build, and install
adb uninstall com.reelvault.app && ./gradlew installDebug && adb shell am start -n com.reelvault.app/.MainActivity
```

### Force SQLDelight Regeneration
```bash
./gradlew clean
./gradlew :composeApp:generateCommonMainReelVaultDatabaseInterface
```

### Check Current Database Schema
```bash
# Android
adb shell
run-as com.reelvault.app
cd databases
sqlite3 reelvault.db
.schema
```

### Export Database for Inspection
```bash
# Android
adb pull /data/data/com.reelvault.app/databases/reelvault.db ~/Desktop/
```

---

## 🔮 Future: Production Migration Template

When you need migrations for production users:

```kotlin
// DatabaseDriverFactory.android.kt
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = ReelVaultDatabase.Schema,
            context = context,
            name = "reelvault.db",
            callback = object : AndroidSqliteDriver.Callback(ReelVaultDatabase.Schema) {
                override fun onMigrate(
                    driver: SqlDriver,
                    oldVersion: Long,
                    newVersion: Long
                ) {
                    when {
                        oldVersion < 2L && newVersion >= 2L -> {
                            // Migration from version 1 to 2
                            driver.execute(null, "ALTER TABLE Reel ADD COLUMN collectionId INTEGER", 0)
                            driver.execute(null, "ALTER TABLE Reel ADD COLUMN notes TEXT", 0)
                            driver.execute(null, """
                                CREATE TABLE Collection (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    name TEXT NOT NULL,
                                    color TEXT NOT NULL,
                                    icon TEXT NOT NULL
                                )
                            """.trimIndent(), 0)
                        }
                    }
                }
            }
        )
    }
}
```

---

*Last Updated: February 1, 2026*
*Status: Development - Schema Version 2*
