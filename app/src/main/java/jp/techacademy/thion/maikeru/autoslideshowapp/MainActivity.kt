package jp.techacademy.thion.maikeru.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ImageView
import java.util.*


class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE=100
    private var mTimer: Timer?=null
    private var mTimerSec=0.0
    private var mHandler= Handler()
    private var Times=0
    private var haveBeenClicked=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button.setOnClickListener {
            // パーミッションの許可状態を確認する
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // 許可されている
                    Log.d("ANDROID", "許可されている")
                } else {
                    Log.d("ANDROID", "許可されていない")
                    // 許可されていないので許可ダイアログを表示する
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
                }
            }
        }

        val resolver = contentResolver
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目（null = 全項目）
                null, // フィルタ条件（null = フィルタなし）
                null, // フィルタ用パラメータ
                null // ソート (nullソートなし）
        )
        var idFst:Long=0
        var idLst:Long=0
        if (cursor!!.moveToFirst()){
            val fieldIndexFirst=cursor.getColumnIndex(MediaStore.Images.Media._ID)
            idFst = cursor.getLong(fieldIndexFirst)
            Log.d("ANDROID", "URI : " + idFst.toString())
        }
        if (cursor!!.moveToLast()){
            val fieldIndexLast=cursor.getColumnIndex(MediaStore.Images.Media._ID)
            idLst = cursor.getLong(fieldIndexLast)
            Log.d("ANDROID", "URI : " + idLst.toString())
        }

        button1.setOnClickListener(){
            if (haveBeenClicked==0){
                val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                if(id > idLst-1){
                    Log.d("UI_PARTS","here")
                    cursor!!.moveToFirst()
                    val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                    Log.d("ANDROID", "URI : " + id.toString())
                }else{
                    cursor!!.moveToNext()
                    val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                    Log.d("ANDROID", "URI : " + id.toString())
                }
            }


        }
        button2.setOnClickListener(){
            if(haveBeenClicked==0){
                Log.d("UI_PARTS","Backward")
                val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                Log.d("ANDROID", "URI B: " + id.toString())
                if((idFst+1)>id) {
                    Log.d("UI_PARTS","I am here")
                    cursor!!.moveToLast()
                }else{
                    cursor!!.moveToPrevious()
                    val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    Log.d("ANDROID", "URI : " + id.toString())
                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                }
            }

        }
        button3.setOnClickListener(){
            Log.d("UI_PARTS","Play/Stop")
            haveBeenClicked=1
            Log.d("UI_PARTS","HaveBeenClick=$haveBeenClicked")
            if(Times==0){
                button3.text="停止"
                if(mTimer==null) {
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mTimerSec += 2.0
                            mHandler.post {
                                timer.text = String.format("%.1f", mTimerSec)
                            }
                            val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor.getLong(fieldIndex)
/*                            Log.d("UI_PARTS","here$idLst")*/
                            if(id > (idLst-1)){
                                Log.d("UI_PARTS","here")
                                cursor!!.moveToFirst()
                                val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor.getLong(fieldIndex)
                                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                imageView.setImageURI(imageUri)
                                Log.d("ANDROID", "URI : " + id.toString())
                            }else{
                                cursor!!.moveToNext()
                                val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor.getLong(fieldIndex)
                                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                imageView.setImageURI(imageUri)
                                Log.d("ANDROID", "URI : " + id.toString())
                            }
                            Log.d("UI_PARTS","GoThruhere")
                        }
                    }, 2000, 2000)
                }
                Times=1
            }else if(Times==1){
                button3.text="再生"
                if(mTimer!=null){
                    mTimer!!.cancel()
                    mTimer=null
                }
                Times=0
                haveBeenClicked=0
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("ANDROID", "許可された")
                } else {
                    Log.d("ANDROID", "許可されなかった")
                }
        }
    }

}