package net.minpro.myownflashcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.Intent as Intent

var intBackGroundColor = 0

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var timer: Timer = Timer()

    //      click process of button
    //    「単語を編集」ボタンを押した場合
    //    単語一覧画面(WordListActivity)へ
        buttonEdit.setOnClickListener {
            val intent = Intent(this@MainActivity, WordListActivity::class.java)
            startActivity(intent)
        }
    //    「色」ボタンを押した場合
    //    画面の背景色をボタンの色に設定

        buttonColor01.setOnClickListener {
            intBackGroundColor = R.color.color01
            constraintlayoutMain.setBackgroundResource(R.color.color01)
        }
        buttonColor02.setOnClickListener {
            intBackGroundColor = R.color.color02
            constraintlayoutMain.setBackgroundResource(R.color.color02)
        }
        buttonColor03.setOnClickListener {
            intBackGroundColor = R.color.color03
            constraintlayoutMain.setBackgroundResource(R.color.color03)
        }
        buttonColor04.setOnClickListener {
            intBackGroundColor = R.color.color04
            constraintlayoutMain.setBackgroundResource(R.color.color04)
        }
        buttonColor05.setOnClickListener {
            intBackGroundColor = R.color.color05
            constraintlayoutMain.setBackgroundResource(R.color.color05)
        }
        buttonColor06.setOnClickListener {
            intBackGroundColor = R.color.color06
            constraintlayoutMain.setBackgroundResource(R.color.color06)
        }

        buttonTest.setOnClickListener {
            val intent = Intent(this@MainActivity, TestActivity::class.java)
            when(radioGroup.checkedRadioButtonId){
                R.id.radioButton -> intent.putExtra(getString(R.string.intent_key_memory_flag), true)
                R.id.radioButton2 -> intent.putExtra(getString(R.string.intent_key_memory_flag), false)

            }
            startActivity(intent)
        }


    }
}