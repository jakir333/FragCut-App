package com.fragcut.data

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface VideoEditorRepository {
    suspend fun trimVideo(inputPath: String, outputPath: String, startTime: String, endTime: String): Boolean
    suspend fun exportVideo(inputPath: String, outputPath: String, width: Int, height: Int, fps: Int): Boolean
}

@Singleton
class VideoEditorRepositoryImpl @Inject constructor() : VideoEditorRepository {

    override suspend fun trimVideo(inputPath: String, outputPath: String, startTime: String, endTime: String): Boolean = withContext(Dispatchers.IO) {
        // FFmpeg command to trim video: -ss [start] -to [end] -i [input] -c copy [output]
        // Using -c copy for fast trimming without re-encoding
        val command = "-ss $startTime -to $endTime -i \"$inputPath\" -c copy \"$outputPath\""
        
        Log.d("FragCut", "Executing FFmpeg command: $command")
        val session = FFmpegKit.execute(command)
        
        if (ReturnCode.isSuccess(session.returnCode)) {
            Log.d("FragCut", "Trim successful")
            true
        } else {
            Log.e("FragCut", "Trim failed: ${session.failStackTrace}")
            false
        }
    }

    override suspend fun exportVideo(inputPath: String, outputPath: String, width: Int, height: Int, fps: Int): Boolean = withContext(Dispatchers.IO) {
        // FFmpeg command for export with scaling and fps: -i [input] -vf scale=[w]:[h],fps=[fps] [output]
        val command = "-i \"$inputPath\" -vf scale=$width:$height,fps=$fps -c:v libx264 -preset ultrafast \"$outputPath\""

        Log.d("FragCut", "Executing FFmpeg command: $command")
        val session = FFmpegKit.execute(command)

        if (ReturnCode.isSuccess(session.returnCode)) {
            Log.d("FragCut", "Export successful")
            true
        } else {
            Log.e("FragCut", "Export failed: ${session.failStackTrace}")
            false
        }
    }
}
