package com.puskal.data.source

import com.puskal.data.model.TemplateModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Puskal Khadka on 4/3/2023.
 */
object TemplateDataSource {
    private val templates = listOf(
        TemplateModel(
            id = 2001,
            name = "城市模式",
            hint = "请上传 1-2 张照片",
            mediaUrl = "img1.jpg"
        ),
        TemplateModel(
            id = 2002,
            name = "海边滤镜",
            hint = "请上传 1-5 张照片",
            mediaUrl = "img2.jpg"
        ),
        TemplateModel(
            id = 2003,
            name = "极速星河",
            hint = "上传 1-3 张照片",
            mediaUrl = "img3.jpg"
        ),
        TemplateModel(
            id = 2004,
            name = "生活一天",
            hint = "上传 2-6 张同一主题照片",
            mediaUrl = "img4.jpg"
        ),

        TemplateModel(
            id = 2005,
            name = "漫画转场",
            hint = "上传 2 张照片展示变化",
            mediaUrl = "img5.jpg"
        ),
        TemplateModel(
            id = 2006,
            name = "黑白影像",
            hint = "请上传 1-6 张照片",
            mediaUrl = "img6.jpg"
        ),
        TemplateModel(
            id = 2007,
            name = "油彩转场",
            hint = "上传 4 张照片",
            mediaUrl = "img7.jpg"
        ),

        )

    fun fetchTemplates(): Flow<List<TemplateModel>> = flow {
        emit(templates.shuffled())
    }
}
