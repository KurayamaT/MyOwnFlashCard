package net.minpro.myownflashcard

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*
import kotlin.collections.ArrayList

class TestActivity : AppCompatActivity(), View.OnClickListener {

    var boolStatusMemory: Boolean = false

    // test situation
    var intState: Int = 0
    val BEFORE_START: Int = 1
    val RUNNING_QUESTION = 2
    val RUNNING_ANSWER: Int = 3
    val TEST_FINISHED: Int = 4

    //realm 関連
    lateinit var realm: Realm
    lateinit var results: RealmResults<WordDB>
    lateinit var word_list: ArrayList<WordDB>

    var intLength:Int = 0 //number of test records

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
//Todo 画面が開いたとき
//Todo 1.MainActivityからのIntent(テスト条件)受け取り
    val bundle = intent.extras
    boolStatusMemory = bundle!!.getBoolean(getString(R. string.intent_key_memory_flag))

//Todo 2.前画面で設定した背景色を設定
    constraintLayoutTest.setBackgroundResource(intBackGroundColor)

//Todo 3.テスト状態を「開始前」に+カード画像非表示
        intState = BEFORE_START
        imageViewFlashQuestion.visibility = View.INVISIBLE
        imageViewFrashAnswer.visibility = View.INVISIBLE

//Todo 4.ボタン①を「テストをはじめる」に
        buttonNext.setBackgroundResource(R.drawable.image_button_test_start)

//Todo 5.ボタン②を「かくにんテストをやめる」に
        buttonEndTest.setBackgroundResource(R.drawable.image_button_end_test)

//click listener
        buttonNext.setOnClickListener(this)
        buttonEndTest.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()

        //realm get instance
        realm = Realm.getDefaultInstance()

        //5.get data from DB
        if (boolStatusMemory) {
            // reject remarked words
            results = realm.where(WordDB::class.java)
                .equalTo(getString(R.string.db_field_memory_flag), false).findAll()
        }else{
            // include remarked words
            results = realm.where(WordDB::class.java).findAll()

        }

        //
        intLength = results.size
        textViewRemaining.text = intLength.toString()


        //6. shuffle data of 5
        word_list = ArrayList(results)
        Collections.shuffle(word_list)
    }

    override fun onPause() {
        super.onPause()

        //realm
        realm.close()

    }


    override fun onClick(v: View?){

//Todo ボタン①（上のボタン）を押したとき
//＝＞テスト状態によって処理を変える
//Todo 1. 「テスト開始前」の場合
//=> 「問題を出した段階」に+問題表示(showQuestion)
        showQuestion()
// Todo 2.「問題を出した段階」の場合
//=> 「こたえを出した段階」に+答え表示(showAnswer)
        showAnswer()
//Todo 3.「こたえを出した段階」の場合
//=> 「問題を出した段階」に+問題表示(showQuestion)
        showQuestion()

//Todo ボタン②（下のボタン）を押したとき：確認ダイアログ
//Todo 1. テスト画面を閉じてMainActivityに戻る
//＝＞Todo テスト状態が「テスト終了」の場合
//=> 最後の問題の暗記済フラグをDBに登録(更新)

    }



    private fun showAnswer() {
//Todo こたえ表示処理(showAnswerメソッド)
//Todo 1. こたえの表示（画像・文字）
//Todo 2. ボタン①を「次の問題にすすむ」に
//Todo 3. 最後の問題まで来たら
//=> Todo 1. テスト状態を「終了」にしてメッセージ表示
//=> Todo 2. ボタン①を見えない＆使えないように
//=> Todo 3. ボタン②を「もどる」に    }
    }

        private fun showQuestion() {

//Todo 問題表示処理(showQuestionメソッド)
//Todo 1. 前の問題の暗記済フラグをDB登録（更新）
//Todo 2. のこり問題数を１つ減らして表示
//Todo 3. 今回の問題表示・前の問題消去（画像と文字）
//Todo 4. ボタン①を「こたえを見る」ボタンに
//Todo 5. 問題の単語が暗記済の場合はチェックを入れる}

        }


    }

