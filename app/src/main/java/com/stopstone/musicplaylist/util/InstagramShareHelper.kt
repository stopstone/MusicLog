package com.stopstone.musicplaylist.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import com.stopstone.musicplaylist.BuildConfig
import com.stopstone.musicplaylist.R
import java.io.File
import java.io.FileOutputStream


// 상수 정의
private const val INSTAGRAM_PACKAGE_NAME = "com.instagram.android"
private const val INSTAGRAM_SHARE_ACTION = "com.instagram.share.ADD_TO_STORY"
private const val CACHE_DIRECTORY_NAME = "instagram_share"
private const val IMAGE_FILE_NAME = "story_share.jpg"
private const val PROVIDER_AUTHORITY = "com.stopstone.musicplaylist.fileprovider"


/*
* 인스타그램 스토리 공유 기능을 담당하는 헬퍼 클래스
* 현재 화면을 스크린샷으로 가져가 인스타그램 스토리로 공유
* TODO 스토리 전용 View를 만들어 공유
* */
object InstagramShareHelper {

    // 현재 화면을 스크린샷으로 캡쳐해서 인스타그램 스토리로 공유
    fun shareCurrentScreenToInstagram(
        activity: Activity,
        rootView: View,
    ) {
        try {
            // 1. 인스타그램 설치여부 확인
            if (!isInstagramInstalled(activity)) {
                activity.showToast(activity.getString(R.string.message_instagram_not_installed))
                redirectToPlayStore(activity)
                return
            }

            // 2. 현재 화면을 스크린샷으로 캡처
            val screenshot: Bitmap? = captureScreenshot(rootView)
            if (screenshot == null) {
                activity.showToast("화면 캡쳐에 실패했습니다.")
                return
            }

            // 3. 비트맵을 파일로 저장
            val imageUri: Uri? = saveScreenshotToFile(activity, screenshot)
            if (imageUri == null) {
                activity.showToast("파일 저장에 실패했습니다.")
                return
            }

            // 4. URI 권한 부여
            grantInstagramPermission(activity, imageUri)

            // 5. 인스타그램 인텐트 생성 및 실행
            launchInstagramShareIntent(activity, imageUri)

        } catch (e: Exception) {
            activity.showToast("$e")
        }
    }

    // 인스타그램에 URI 권한 부여
    private fun grantInstagramPermission(
        context: Context,
        uri: Uri,
    ) {
        context.grantUriPermission(
            INSTAGRAM_PACKAGE_NAME,
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
    }

    // 인스타그램 스토리 공유 인텐트 실행
    private fun launchInstagramShareIntent(
        activity: Activity,
        imageUri: Uri,
    ) {
        val shareIntent = Intent(INSTAGRAM_SHARE_ACTION).apply {
            setDataAndType(imageUri, "image/jpeg")
            putExtra("source_application", BuildConfig.FACEBOOK_APP_ID)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        activity.startActivity(shareIntent)
    }

    // 인스타그램 설치여부 확인
    private fun isInstagramInstalled(context: Context): Boolean =
        try {
            context.packageManager.getApplicationInfo(INSTAGRAM_PACKAGE_NAME, 0)
            true
        } catch (e: Exception) {
            false
        }

    // 인스타그램이 설치되지 않았을 경우 GooglePlayStore로 이동
    private fun redirectToPlayStore(context: Context) {
        val playStoreIntent: Intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$INSTAGRAM_PACKAGE_NAME")
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(playStoreIntent)
    }

    // View를 비트맵으로 캡쳐
    private fun captureScreenshot(view: View): Bitmap? = try {
        val bitmap = createBitmap(view.width, view.height)
        view.draw(Canvas(bitmap))
        bitmap
    } catch (e: Exception) {
        null
    }

    // 비트맵을 파일로 저장하고 URI로 변환
    private fun saveScreenshotToFile(
        context: Context,
        bitmap: Bitmap,
    ): Uri? = try {
        val cacheDir = File(context.cacheDir, CACHE_DIRECTORY_NAME)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        val imageFile = File(cacheDir, IMAGE_FILE_NAME)
        FileOutputStream(imageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
        }

        FileProvider.getUriForFile(
            context,
            PROVIDER_AUTHORITY,
            imageFile,
        )
    } catch (e: Exception) {
        null
    }
}