package `in`.devco.logger

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.*

object LogWriter {
    const val REQUEST_WRITE_PERMISSION = 901

    fun startLogging(activity: AppCompatActivity) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_PERMISSION
            )
            return
        }
        val logCatViewModel by activity.viewModels<LoggerViewModel>()

        logCatViewModel.logCatOutput().observe(activity, { logMessage ->
            appendLog(logMessage, activity)
        })
    }

    private fun appendLog(text: String, context: Context) {
        val logFile = File(context.getExternalFilesDir(null), "app.log")
        var append = true
        if (!logFile.exists()) {
            logFile.createNewFile()
        } else {
            val bufferedReader = BufferedReader(FileReader(logFile))
            append = bufferedReader.readLines().size < 10000
            bufferedReader.close()
        }

        val buf = BufferedWriter(FileWriter(logFile, append))
        buf.append(text)
        buf.newLine()
        buf.close()
    }
}