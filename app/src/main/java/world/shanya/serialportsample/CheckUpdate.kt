/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.app
 * 类名：CheckUpdate.kt
 * 作者：Shanya
 * 日期：2022/3/2 下午6:51
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialportsample

import android.os.Looper
import com.azhon.appupdate.config.UpdateConfiguration

import com.ejlchina.okhttps.HTTP
import com.ejlchina.okhttps.gson.GsonMsgConvertor
import com.ejlchina.okhttps.HttpResult
import com.ejlchina.okhttps.OnCallback
import android.app.Activity
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.utils.ApkUtil


private const val jsonUrl = "https://gitee.com/Shanya/serialportappupdate/raw/master/update.json"

class CheckUpdate(private val activity: Activity) {
    private val http = HTTP.builder()
        .baseUrl(jsonUrl)
        .addMsgConvertor(GsonMsgConvertor())
        .build()

    fun check() {
        http.async("") //  http://api.example.com/users/1
            .setOnResponse { res: HttpResult ->
                // 得到目标数据
//                val user: User = res.body.toBean(User::class.java)
                val updateBean = res.body.toBean(UpdateBean::class.java)
                updateBean?.let {
                    update(it.apkName, it.downloadUrl, it.versionCode, it.updateDescription)
                    val b = ApkUtil.deleteOldApk(
                        activity,
                        activity.externalCacheDir?.path.toString() + "/${it.apkName}"
                    )
                }
            }
            .get()
    }

    private fun update(
        apkName: String,
        downloadUrl: String,
        versionCode: Int,
        updateDescription: String
    ) {
        val manager: DownloadManager = DownloadManager.getInstance(activity)
        manager.setApkName(apkName)
            .setApkUrl(downloadUrl)
            .setSmallIcon(R.mipmap.ic_launcher_logo)
//            .setConfiguration(configuration) //设置了此参数，那么会自动判断是否需要更新弹出提示框
            .setApkVersionCode(versionCode)
            .setApkDescription(updateDescription)
            .download()
    }

}

data class UpdateBean(
    var apkName: String,
    var downloadUrl: String,
    var versionCode: Int,
    var updateDescription: String
)