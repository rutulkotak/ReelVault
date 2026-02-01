package com.reelvault.app.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.reelvault.app.presentation.theme.AuroraColors
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * Settings Screen with Daily Nudge and Heritage Vault configuration.
 */
class SettingsScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: SettingsViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.current
        val snackbarHostState = remember { SnackbarHostState() }

        // Effect handling
        LaunchedEffect(viewModel) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is SettingsContract.Effect.ShowMessage -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                    is SettingsContract.Effect.NavigateToHeritageVaultSetup -> {
                        // Navigate to Heritage Vault setup (future implementation)
                        snackbarHostState.showSnackbar("Heritage Vault Setup - Coming Soon!")
                    }
                    is SettingsContract.Effect.ShowPermissionDialog -> {
                        val message = if (effect.granted) {
                            "Notification permission granted!"
                        } else {
                            "Please enable notifications in system settings"
                        }
                        snackbarHostState.showSnackbar(message)
                    }
                }
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(AuroraColors.MidnightIndigo),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Settings",
                            style = MaterialTheme.typography.headlineMedium,
                            color = AuroraColors.TextPrimary
                        )
                    },
                    navigationIcon = {
                        TextButton(onClick = { navigator?.pop() }) {
                            Text(
                                "←",
                                style = MaterialTheme.typography.headlineMedium,
                                color = AuroraColors.TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AuroraColors.DeepIndigo,
                        titleContentColor = AuroraColors.TextPrimary
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = AuroraColors.MidnightIndigo
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Growth Features Section
                SettingsSectionHeader("Growth Features")

                SettingsCard {
                    SettingsSwitchItem(
                        title = "Daily Nudge",
                        description = "Get reminded to check your saved videos after 24h of inactivity",
                        icon = "📲",
                        checked = state.isDailyNudgeEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.onIntent(SettingsContract.Intent.ToggleDailyNudge(enabled))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stats card
                SettingsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📊 Your Stats",
                            style = MaterialTheme.typography.titleMedium,
                            color = AuroraColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${state.totalVideosSaved} videos saved",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AuroraColors.TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Heritage Vault Section (Hidden feature)
                SettingsSectionHeader("Digital Legacy")

                SettingsCard {
                    SettingsSwitchItem(
                        title = "Heritage Vault",
                        description = "Digital Inheritance - Pass your collection to loved ones (Beta)",
                        icon = "🏛️",
                        checked = state.isHeritageVaultEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.onIntent(SettingsContract.Intent.ToggleHeritageVault(enabled))
                        }
                    )

                    if (state.isHeritageVaultEnabled) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = AuroraColors.LightCharcoal.copy(alpha = 0.3f)
                        )

                        SettingsActionItem(
                            title = "Configure Beneficiaries",
                            description = "Set up who receives your vault",
                            icon = "👥",
                            onClick = {
                                viewModel.onIntent(SettingsContract.Intent.OpenHeritageVaultSetup)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // About section
                SettingsSectionHeader("About")

                SettingsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ReelVault v1.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AuroraColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Your personal knowledge vault",
                            style = MaterialTheme.typography.bodySmall,
                            color = AuroraColors.TextTertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = AuroraColors.SoftViolet,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AuroraColors.MediumCharcoal.copy(alpha = 0.4f)
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    description: String,
    icon: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(end = 16.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = AuroraColors.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = AuroraColors.TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AuroraColors.BrightIndigo,
                checkedTrackColor = AuroraColors.BrightIndigo.copy(alpha = 0.5f),
                uncheckedThumbColor = AuroraColors.LightCharcoal,
                uncheckedTrackColor = AuroraColors.MediumCharcoal
            )
        )
    }
}

@Composable
private fun SettingsActionItem(
    title: String,
    description: String,
    icon: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(end = 16.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = AuroraColors.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = AuroraColors.TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Text(
            text = "→",
            style = MaterialTheme.typography.headlineSmall,
            color = AuroraColors.SoftViolet
        )
    }
}
