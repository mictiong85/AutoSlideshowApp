package jp.techacademy.thion.maikeru.autoslideshowapp

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
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
    private var TimesToCheck=0
    private var haveBeenClicked=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            // パーミッションの許可状態を確認する
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                Log.d("ANDROID", "許可されていない")
                    // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
        }else{
            getContentsInfo()
        }

    }

    private fun getContentsInfo(){
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
        if (cursor.moveToLast()){
            val fieldIndexLast=cursor.getColumnIndex(MediaStore.Images.Media._ID)
            idLst = cursor.getLong(fieldIndexLast)
            Log.d("ANDROID", "URI : " + idLst.toString())
        }

        button1.setOnClickListener(){
            if (haveBeenClicked==0){
                val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                if(id > (idLst-1)){
                    cursor.moveToFirst()
                    setImageHere(cursor)
                }else{
                    cursor.moveToNext()
                    setImageHere(cursor)
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
                    cursor.moveToLast()
                    setImageHere(cursor)
                }else{
                    cursor.moveToPrevious()
                    setImageHere(cursor)
                }
            }

        }
        button3.setOnClickListener(){
            haveBeenClicked=1

            if(TimesToCheck==0){
                button3.text="停止"
                if(mTimer==null) {
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor.getLong(fieldIndex)
                            Log.d("UI_PARTS","Ini now is $id")

                            mTimerSec += 2.0

                            if(id > (idLst-1)){
                                cursor.moveToFirst()
                                val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor.getLong(fieldIndex)
                                Log.d("UI_PARTS","1sthere")
                                Log.d("UI_PARTS","Reset ID is $id")
                                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                mHandler.post{
                                    imageView.setImageURI(imageUri)
                                }
                            }else{
                                cursor.moveToNext()
                                val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor.getLong(fieldIndex)
                                Log.d("UI_PARTS","2ndhere")
                                Log.d("UI_PARTS","Next ID is $id")
                                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                mHandler.post{
                                    imageView.setImageURI(imageUri)
                                }
                            }
                        }
                    }, 2000, 2000)
                }
                TimesToCheck=1
            }else if(TimesToCheck==1){
                button3.text="再生"
                if(mTimer!=null){
                    mTimer!!.cancel()
                    mTimer=null
                }
                TimesToCheck=0
                haveBeenClicked=0
            }

        }

    }

    fun setImageHere(cursor: Cursor){
        val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor.getLong(fieldIndex)
        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        imageView.setImageURI(imageUri)
        Log.d("ANDROID", "URI : " + id.toString())
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("ANDROID", "許可された")
                    getContentsInfo()
                }
        }
    }

}

