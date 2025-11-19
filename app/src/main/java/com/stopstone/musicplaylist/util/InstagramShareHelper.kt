package com.stopstone.musicplaylist.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.TextViewCompat
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.stopstone.musicplaylist.BuildConfig
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.LayoutInstagramStoryShareBinding
import com.stopstone.musicplaylist.domain.model.CalendarDay
import com.stopstone.musicplaylist.ui.model.TrackUiState
import java.io.File
import java.io.FileOutputStream
import java.util.Date

// 상수 정의
private const val INSTAGRAM_PACKAGE_NAME = "com.instagram.android"
private const val INSTAGRAM_SHARE_ACTION = "com.instagram.share.ADD_TO_STORY"
private const val CACHE_DIRECTORY_NAME = "instagram_share"
private const val IMAGE_FILE_NAME = "story_share.png"
private const val PROVIDER_AUTHORITY = "com.stopstone.musicplaylist.fileprovider"

// 인스타그램 스토리 이미지 크기
private const val STORY_WIDTH = 1080

/*
* 인스타그램 스토리 공유 기능을 담당하는 헬퍼 클래스
* 커스텀 View를 생성하여 인스타그램 스토리로 공유
* */
object InstagramShareHelper {
    // 커스텀 스토리 View를 생성하여 인스타그램 스토리로 공유
    fun shareCustomStoryToInstagram(
        activity: Activity,
        dailyTrack: CalendarDay,
        recordedAt: Date?,
        showEmotions: Boolean = true,
        showMemo: Boolean = true,
        showRecordedTime: Boolean = true,
    ) {
        try {
            // 1. 인스타그램 설치여부 확인
            if (!isInstagramInstalled(activity)) {
                activity.showToast(activity.getString(R.string.message_instagram_not_installed))
                redirectToPlayStore(activity)
                return
            }

            // 2. 트랙 정보 가져오기
            val track = dailyTrack.track
            if (track == null) {
                activity.showToast(activity.getString(R.string.message_track_info_not_available))
                return
            }

            // 3. 이미지 로딩 후 스토리 생성
            loadImageAndCreateStory(
                activity,
                track,
                dailyTrack.emotions,
                dailyTrack.comment,
                showEmotions,
                showMemo,
                showRecordedTime,
                recordedAt,
            )
        } catch (e: Exception) {
            activity.showToast(activity.getString(R.string.message_share_error))
        }
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
        val playStoreIntent: Intent =
            Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$INSTAGRAM_PACKAGE_NAME".toUri(),
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(playStoreIntent)
    }

    // 이미지를 로드하고 스토리 생성
    private fun loadImageAndCreateStory(
        activity: Activity,
        track: TrackUiState,
        emotions: List<String>,
        comment: String?,
        showEmotions: Boolean,
        showMemo: Boolean,
        showRecordedTime: Boolean,
        recordedAt: Date?,
    ) {
        Glide
            .with(activity)
            .asBitmap()
            .load(track.imageUrl)
            .centerCrop()
            .into(
                object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        // 이미지 로딩 완료 후 View 생성
                        val storyView =
                            createStoryView(
                                activity,
                                track,
                                emotions,
                                comment,
                                showEmotions,
                                showMemo,
                                showRecordedTime,
                                recordedAt,
                                resource,
                            )

                        // View를 비트맵으로 변환
                        val bitmap: Bitmap? = captureViewToBitmap(storyView)
                        if (bitmap == null) {
                            activity.showToast(activity.getString(R.string.message_image_generation_failed))
                            return
                        }

                        // 비트맵을 파일로 저장
                        val imageUri: Uri? = saveScreenshotToFile(activity, bitmap)
                        if (imageUri == null) {
                            activity.showToast(activity.getString(R.string.message_file_save_failed))
                            return
                        }

                        // URI 권한 부여
                        grantInstagramPermission(activity, imageUri)

                        // 인스타그램 인텐트 생성 및 실행
                        launchInstagramShareIntent(activity, imageUri)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // 이미지 로딩 취소 시
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        activity.showToast(activity.getString(R.string.message_image_loading_failed))
                    }
                },
            )
    }

    // 인스타그램 스토리용 커스텀 View 생성
    private fun createStoryView(
        context: Context,
        track: TrackUiState,
        emotions: List<String>,
        comment: String?,
        showEmotions: Boolean,
        showMemo: Boolean,
        showRecordedTime: Boolean,
        recordedAt: Date?,
        albumBitmap: Bitmap,
    ): View {
        LayoutInstagramStoryShareBinding
            .inflate(
                LayoutInflater.from(context),
                null,
                false,
            ).apply {
                // 앨범 커버 항상 표시
                ivStoryAlbumCover.setImageBitmap(albumBitmap)

                // 노래 제목 항상 표시
                tvStoryTitle.text = track.title

                // 아티스트 항상 표시
                tvStoryArtist.text = track.artist

                // 감정 태그 표시 (설정에 따라)
                if (showEmotions && emotions.isNotEmpty()) {
                    val displayList = EmotionDisplayMapper.mapToDisplayNames(context, emotions)
                    displayList.forEach { emotion ->
                        val textView =
                            TextView(context).apply {
                                text = emotion
                                background =
                                    AppCompatResources.getDrawable(context, R.drawable.background_gray)
                                setPadding(16, 8, 16, 8)
                                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MusicLog_BodyMedium)
                            }
                        chipGroupStoryEmotions.addView(textView)
                    }
                    chipGroupStoryEmotions.visibility = View.VISIBLE
                } else {
                    chipGroupStoryEmotions.visibility = View.GONE
                }

                // 메모 표시 (설정에 따라)
                if (showMemo && !comment.isNullOrEmpty()) {
                    tvStoryMemo.text = comment
                    tvStoryMemo.visibility = View.VISIBLE
                } else {
                    tvStoryMemo.visibility = View.GONE
                }

                // 저장 시간 표시 (설정에 따라)
                if (showRecordedTime) {
                    val timeText = recordedAt?.let { DateUtils.formatTime(it) } ?: "00:00"
                    val totalMinutes = recordedAt?.let { DateUtils.getTotalMinutes(it) } ?: 0
                    seekbarStoryTime.progress = totalMinutes
                    tvStoryTime.text = "$timeText / 24:00"
                    seekbarStoryTime.visibility = View.VISIBLE
                    tvStoryTime.visibility = View.VISIBLE
                } else {
                    seekbarStoryTime.visibility = View.GONE
                    tvStoryTime.visibility = View.GONE
                }

                // View 크기 설정 및 레이아웃
                val widthSpec = View.MeasureSpec.makeMeasureSpec(STORY_WIDTH, View.MeasureSpec.EXACTLY)
                val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

                root.measure(widthSpec, heightSpec)
                root.layout(0, 0, root.measuredWidth, root.measuredHeight)

                return root
            }
    }

    // View를 비트맵으로 변환
    private fun captureViewToBitmap(view: View): Bitmap? =
        try {
            val bitmap = createBitmap(view.measuredWidth, view.measuredHeight)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
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
        val shareIntent: Intent =
            Intent(INSTAGRAM_SHARE_ACTION).apply {
                type = "image/*"

                putExtra("interactive_asset_uri", imageUri)
                putExtra("source_application", BuildConfig.FACEBOOK_APP_ID)
                putExtra(
                    "top_background_color",
                    String.format(
                        "#%06X",
                        0xFFFFFF and
                            ContextCompat.getColor(
                                activity,
                                R.color.instagram_story_top_background,
                            ),
                    ),
                )
                putExtra(
                    "bottom_background_color",
                    String.format(
                        "#%06X",
                        0xFFFFFF and
                            ContextCompat.getColor(
                                activity,
                                R.color.instagram_story_bottom_background,
                            ),
                    ),
                )

                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage(INSTAGRAM_PACKAGE_NAME)
            }

        activity.startActivity(shareIntent)
    }

    // 비트맵을 파일로 저장하고 URI로 변환
    private fun saveScreenshotToFile(
        context: Context,
        bitmap: Bitmap,
    ): Uri? =
        try {
            val cacheDir = File(context.cacheDir, CACHE_DIRECTORY_NAME)
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val imageFile = File(cacheDir, IMAGE_FILE_NAME)
            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
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
