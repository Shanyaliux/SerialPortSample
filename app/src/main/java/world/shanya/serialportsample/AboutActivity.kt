/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.app
 * 类名：AboutActivity.kt
 * 作者：Shanya
 * 日期：2022/2/23 下午2:49
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialportsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import world.shanya.serialportsample.utils.CheckUpdate
import java.lang.Exception


class AboutActivity : AppCompatActivity() {

    private val checkUpdate = CheckUpdate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val versionElement = Element()
        versionElement.gravity = Gravity.CENTER
        versionElement.title = "版本号: " + packageManager.getPackageInfo(packageName, 0).versionName
        versionElement.setOnClickListener {
                checkUpdate.check(true)
        }

        val authorElement = Element()
        authorElement.gravity = Gravity.CENTER
        authorElement.title = "作者: Shanya"

        val aboutPage: View = AboutPage(this)
            .isRTL(false)
            .setDescription("SerialPort封装库的全功能示例APP")
//            .setCustomFont(String) // or Typeface
            .setImage(R.mipmap.ic_launcher_logo)
//            .addItem(Element().setTitle(packageManager.getPackageInfo(packageName, 0).versionName))
//            .addItem(adsElement)
            .addGroup("Connect with us")
            .addEmail("shanyaliux@qq.com")
            .addWebsite("https://shanyaliux.cn/serialport/")
//            .addFacebook("the.medy")
//            .addTwitter("medyo80")
//            .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
//            .addPlayStore("com.ideashower.readitlater.pro")
            .addGitHub("Shanyaliux\\SerialPortSample")
//            .addInstagram("medyo80")
            .addItem(authorElement)
            .addItem(versionElement)
            .create()
        setContentView(aboutPage)
    }
}