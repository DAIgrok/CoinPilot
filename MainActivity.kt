package com.coinpilot

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoinPilotWorker.scheduleDaily(this)

        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        lifecycleScope.launch {
            CoinPilotDatabase.get(this@MainActivity).taskDao().seedDefaults()
        }

        setContent { MaterialTheme { CoinPilotScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoinPilotScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dao = remember { CoinPilotDatabase.get(context).taskDao() }
    val tasks by dao.observeAll().collectAsState(initial = emptyList())
    var feedback by remember { mutableStateOf("Pronto. Escolha uma plataforma para começar.") }

    Scaffold(topBar = { TopAppBar(title = { Text("CoinPilot") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Plataformas", style = MaterialTheme.typography.headlineSmall)
                Text("Abrir o app não significa que uma moeda foi resgatada.")
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        feedback = PlatformLauncher.open(context, Platform.SHOPEE).message
                    }) { Text("Abrir Shopee") }
                    Button(onClick = {
                        feedback = PlatformLauncher.open(context, Platform.ALIEXPRESS).message
                    }) { Text("Abrir AliExpress") }
                }
                Spacer(Modifier.height(8.dp))
                Text(feedback, style = MaterialTheme.typography.bodyMedium)
            }

            item {
                Text("Tarefas", style = MaterialTheme.typography.headlineSmall)
            }

            items(tasks, key = { it.id }) { task ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(task.platform, style = MaterialTheme.typography.titleMedium)
                        Text(task.taskType)
                        Text("Status: ${task.status}")
                        if (task.details.isNotBlank()) Text(task.details)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = {
                                kotlinx.coroutines.MainScope().launch {
                                    dao.updateStatus(
                                        task.id,
                                        "Requer ação",
                                        "Abra a plataforma e conclua a tarefa manualmente."
                                    )
                                }
                            }) { Text("Pendente") }
                            Button(onClick = {
                                kotlinx.coroutines.MainScope().launch {
                                    dao.updateStatus(
                                        task.id,
                                        "Confirmada pelo usuário",
                                        "Confirmação manual; crédito de moedas não verificado pelo CoinPilot."
                                    )
                                }
                            }) { Text("Registrei") }
                        }
                    }
                }
            }

            item {
                Text(
                    "A rotina diária envia um lembrete. Jogos e resgates permanecem assistidos até existir uma integração autorizada.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
