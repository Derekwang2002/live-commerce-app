package com.puskal.data.source

import com.puskal.data.model.SocialMediaType
import com.puskal.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Puskal Khadka on 3/18/2023.
 */
object UsersDataSource {
    val kylieJenner = UserModel(
        userId = 1,
        uniqueUserName = "xiaolin",
        fullName = "小林",
        following = 23,
        followers = 52000000,
        likes = 1100000000,
        bio = "分享日常好物",
        profilePic = "https://c4.wallpaperflare.com/wallpaper/887/659/808/kylie-jenner-2018-wallpaper-preview.jpg",
        isVerified = true,
        pinSocialMedia = UserModel.SocialMedia(
            type = SocialMediaType.YOUTUBE,
            link = "https://www.youtube.com/@kyliejenner"
        )
    )

    val charliePuth = UserModel(
        userId = 2,
        uniqueUserName = "ayi_music",
        fullName = "阿一音乐",
        following = 35,
        followers = 21000000,
        likes = 45400000,
        bio = "音乐和生活",
        profilePic = "https://c4.wallpaperflare.com/wallpaper/240/757/317/singers-charlie-puth-wallpaper-preview.jpg",
        isVerified = true,
        pinSocialMedia = UserModel.SocialMedia(
            type = SocialMediaType.INSTAGRAM,
            link = "https://www.instagram.com/charlieputh"
        )
    )

    val taylor = UserModel(
        userId = 3,
        uniqueUserName = "xiaotang001",
        fullName = "小唐",
        following = 1200,
        followers = 21000,
        likes = 32000,
        bio = "记录生活",
        profilePic = "https://c4.wallpaperflare.com/wallpaper/328/810/463/digital-digital-art-artwork-illustration-simple-hd-wallpaper-preview.jpg",
        isVerified = false,
    )

    val duaLipa = UserModel(
        userId = 4,
        uniqueUserName = "lili_official",
        fullName = "莉莉",
        following = 23,
        followers = 87000000,
        likes = 3300000000,
        bio = "舞台与日常",
        profilePic = "https://c4.wallpaperflare.com/wallpaper/702/297/121/dua-lipa-2018-4k-wallpaper-preview.jpg",
        isVerified = true,
        pinSocialMedia = UserModel.SocialMedia(
            type = SocialMediaType.YOUTUBE,
            link = "https://www.youtube.com/@dualipa/"
        )
    )

    val imagineDragons = UserModel(
        userId = 5,
        uniqueUserName = "fengbao_band",
        fullName = "风暴乐队",
        following = 400,
        followers = 32000000,
        likes = 4300000000,
        bio = "乐队现场",
        profilePic = "https://c4.wallpaperflare.com/wallpaper/516/72/957/imagine-dragons-4k-pc-full-hd-wallpaper-preview.jpg",
        isVerified = true,
        pinSocialMedia = UserModel.SocialMedia(
            type = SocialMediaType.YOUTUBE,
            link = "https://www.youtube.com/@ImagineDragons"
        )
    )

    val google = UserModel(
        userId = 6,
        uniqueUserName = "kejihao",
        fullName = "科技号",
        following = 300,
        followers = 8970000,
        likes = 54000000,
        bio = "科技新鲜事",
        profilePic = "https://cdn1.iconfinder.com/data/icons/google-s-logo/150/Google_Icons-09-512.png",
        isVerified = true,
    )

    val daniel = UserModel(
        userId = 7,
        uniqueUserName = "daniel_cn",
        fullName = "大牛",
        following = 1400,
        followers = 1200,
        likes = 14000,
        bio = "城市探索",
        profilePic = "https://c4.wallpaperflare.com/wallpaper/429/125/144/shingeki-no-kyojin-levi-erwin-smith-sword-wallpaper-preview.jpg",
        isVerified = false,
    )

    val shana = UserModel(
        userId = 8,
        uniqueUserName = "shana_cn",
        fullName = "莎娜",
        following = 400,
        followers = 9870008,
        likes = 98700000,
        bio = "穿搭分享",
        profilePic = "https://c4.wallpaperflare.com/wallpaper/178/256/912/face-blue-eyes-women-model-wallpaper-preview.jpg",
        isVerified = false,
    )
    val jeremy = UserModel(
        userId = 9,
        uniqueUserName = "jielimi",
        fullName = "杰里米",
        following = 1200,
        followers = 800000,
        likes = 3900000,
        bio = "随手拍",
        profilePic = "https://c4.wallpaperflare.com/wallpaper/549/801/215/linkin-park-mike-shinoda-chester-bennington-photo-wallpaper-preview.jpg",
        isVerified = false,
    )

    val zoya = UserModel(
        userId = 10,
        uniqueUserName = "zuoya",
        fullName = "佐雅",
        following = 200,
        followers = 40000,
        likes = 670000,
        bio = "旅行与海风",
        profilePic = "https://c0.wallpaperflare.com/preview/781/436/846/saipan-northern-mariana-islands-island-ocean.jpg",
        isVerified = false,
    )

    val userList = listOf(
        kylieJenner,
        charliePuth,
        taylor,
        duaLipa,
        imagineDragons,
        google,
        daniel,
        shana,
        jeremy,
        zoya
    )

    fun fetchSpecificUser(userId: Long): Flow<UserModel?> {
        return flow {
            val user = userList.firstOrNull { it.userId == userId }
            emit(user)
        }
    }

}


