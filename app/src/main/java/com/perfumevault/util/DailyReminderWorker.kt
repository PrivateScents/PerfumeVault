package com.perfumevault.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.perfumevault.data.PerfumeDatabase
import com.perfumevault.repository.PerfumeRepository
import kotlinx.coroutines.flow.first

class DailyReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = PerfumeDatabase.getDatabase(applicationContext)
        val repo = PerfumeRepository(db.perfumeDao(), db.usageLogDao(), db.catalogDao(), db.perfumeSizeDao())
        val notificationHelper = NotificationHelper(applicationContext)

        val perfumes = repo.allPerfumes.first()
        if (perfumes.isEmpty()) return Result.success()

        // Einfache Erinnerung am Morgen
        notificationHelper.sendNotification(
            "Guten Morgen! ✨",
            "Welchen Duft trägst du heute? Vergiss nicht, dein Tagebuch zu füllen.",
            notificationId = 100
        )

        // Check for empty perfumes
        val emptyPerfumes = perfumes.filter { it.remainingMl < 5.0 || it.remainingMl < (it.bottleSize * 0.1) }
        if (emptyPerfumes.isNotEmpty()) {
            val p = emptyPerfumes.random()
            notificationHelper.sendNotification(
                "Fast leer! 🧪",
                "Dein Duft '${p.name}' ist fast aufgebraucht. Zeit für Nachschub?",
                notificationId = 101
            )
        }

        return Result.success()
    }
}
