package com.coinpilot

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

enum class Platform(val displayName: String, val packageName: String, val webUrl: String) {
    SHOPEE(
        "Shopee",
        "com.shopee.br",
        "https://shopee.com.br/"
    ),
    ALIEXPRESS(
        "AliExpress",
        "com.alibaba.aliexpresshd",
        "https://www.aliexpress.com/"
    )
}

data class LaunchResult(
    val opened: Boolean,
    val message: String
)

object PlatformLauncher {
    fun open(context: Context, platform: Platform): LaunchResult {
        val pm = context.packageManager
        val appIntent = pm.getLaunchIntentForPackage(platform.packageName)

        if (appIntent != null) {
            return try {
                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(appIntent)
                LaunchResult(true, "${platform.displayName} aberto. Conclua a tarefa no app e volte ao CoinPilot.")
            } catch (e: Exception) {
                LaunchResult(false, "Não foi possível abrir ${platform.displayName}: ${e.localizedMessage ?: "erro desconhecido"}")
            }
        }

        return try {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(platform.webUrl))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(webIntent)
            LaunchResult(true, "${platform.displayName} não foi encontrado como app; abri o site no navegador.")
        } catch (e: Exception) {
            LaunchResult(false, "Não consegui abrir ${platform.displayName} nem o navegador.")
        }
    }

    fun isInstalled(context: Context, platform: Platform): Boolean {
        return try {
            context.packageManager.getPackageInfo(platform.packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (_: Exception) {
            false
        }
    }
}
