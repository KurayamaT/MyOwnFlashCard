package net.minpro.myownflashcard

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*
import kotlin.collections.ArrayList

class TestActivity : AppCompatActivity(), View.OnClickListener {

    //テスト条件（暗記済みの単語を除外するか：する＝＞true）
    var boolStatusMemory: Boolean = false

    //問題を暗記済みにするかどうか
    var boolMemorized: Boolean = false

    //テストの状態
    var intState: Int = 0
    val BEFORE_START: Int = 1       //テスト開始前
    val RUNNING_QUESTION: Int = 2   //問題を出した段階
    val RUNNING_ANSWER: Int = 3     //こたえを出した段階
    val TEST_FINISHED: Int = 4      //テスト終了

    //realm関係
    lateinit var realm: Realm
    lateinit var results: RealmResults<WordDB>
    lateinit var wordList: ArrayList<WordDB>

    var intLength: Int = 0   //レコードの数(テストの問題数）
    var intCount: Int = 0    //今何問めを示すカウンター

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // 画面が開いたとき
        // 1.MainActivityからのIntent(テスト条件)受け取り
        val bundle = intent.extras
        boolStatusMemory = bundle!!.getBoolean(getString(R.string.intent_key_memory_flag))

        // 2.前画面で設定した背景色を設定
        constraintLayoutTest.setBackgroundResource(intBackGroundColor)

        // 3.テスト状態を「開始前」に+カード画像非表示
        intState = BEFORE_START
        imageViewFlashQuestion.visibility = View.INVISIBLE
        imageViewFlashAnswer.visibility = View.INVISIBLE

        // 4.ボタン①を「テストをはじめる」に
        buttonNext.setBackgroundResource(R.drawable.image_button_test_start)

        //5.ボタン②を「かくにんテストをやめる」に
        buttonEndTest.setBackgroundResource(R.drawable.image_button_end_test)

        //クリックリスナー
        buttonNext.setOnClickListener(this)
        buttonEndTest.setOnClickListener(this)
        checkBox.setOnClickListener {
            //暗記済みのチェックボックスを押したときは暗記済みのステータスを変える
            boolMemorized = checkBox.isChecked
        }
    }


    override fun onResume() {
        super.onResume()

        //realmインスタンスの取得
        realm = Realm.getDefaultInstance()

        //5.DBからテストデータ取得(テスト条件で処理分岐)
        if (boolStatusMemory) {
            //暗記済みの単語を除外する
            results = realm.where(WordDB::class.java).equalTo(getString(R.string.db_field_memory_flag), false).findAll()
        } else {
            //暗記済みの単語を除外しない（含める）
            results = realm.where(WordDB::class.java).findAll()
        }

        //残り問題数の表示
        intLength = results.size
        textViewRemaining.text = intLength.toString()

        //6. 5で取得したテストデータをシャッフル
        wordList = ArrayList(results)
        Collections.shuffle(wordList)
    }

    override fun onPause() {
        super.onPause()

        //realmの後片付け
        realm.close()
    }

    override fun onClick(v: View) {

        when (v.id) {
            // ボタン①（上のボタン）を押したとき
            //＝＞テスト状態によって処理を変える
            R.id.buttonNext ->

                when (intState) {
                    BEFORE_START -> {
                        //1. 「テスト開始前」の場合
                        //=> 「問題を出した段階」に+問題表示(showQuestion)
                        intState = RUNNING_QUESTION
                        showQuestion()
                    }

                    RUNNING_QUESTION -> {
                        //2.「問題を出した段階」の場合
                        //=> 「こたえを出した段階」に+答え表示(showAnswer)
                        intState = RUNNING_ANSWER
                        showAnswer()
                    }

                    RUNNING_ANSWER -> {
                        // 3.「こたえを出した段階」の場合
                        //=> 「問題を出した段階」に+問題表示(showQuestion)
                        intState = RUNNING_QUESTION
                        showQuestion()
                    }

                }

            R.id.buttonEndTest -> {
                // ボタン②（下のボタン）を押したとき：確認ダイアログ
                val dialog = AlertDialog.Builder(this@TestActivity).apply {
                    setTitle("テストの終了")
                    setMessage("テストを終了してもいいですか？")
                    setPositiveButton("はい") { dialog, which ->
                        // 1. テスト画面を閉じてMainActivityに戻る
                        //＝＞ テスト状態が「テスト終了」の場合
                        //=> 最後の問題の暗記済フラグをDBに登録(更新)
                        if (intState == TEST_FINISHED) {
                            val selectedDB = realm.where(WordDB::class.java).equalTo(getString(R.string.db_field_question)
                                , wordList[intCount - 1].strQuestion).findFirst()
                            realm.beginTransaction()
                            selectedDB?.boolMemoryFrag = boolMemorized
                            realm.commitTransaction()
                        }
                        finish()
                    }
                    setNegativeButton("いいえ") { dialog, which -> }
                    show()

                }
            }
        }
    }

    private fun showAnswer() {
        // こたえ表示処理(showAnswerメソッド)
        // 1. こたえの表示（画像・文字）
        imageViewFlashAnswer.visibility = View.VISIBLE
        textViewFlashAnswer.text = wordList[intCount - 1].strAnswer

        // 2. ボタン①を「次の問題にすすむ」に
        buttonNext.setBackgroundResource(R.drawable.image_button_go_next_question)

        // 3. 最後の問題まで来たら
        if (intLength == intCount) {
            //=>  1. テスト状態を「終了」にしてメッセージ表示
            intState = TEST_FINISHED
            textViewMessage.text = "テスト修了"

            //=>  2. ボタン①を見えない＆使えないように
            buttonNext.isEnabled = false
            buttonNext.visibility = View.INVISIBLE

            //=>  3. ボタン②を「もどる」に
            buttonEndTest.setBackgroundResource(R.drawable.image_button_back)

        }
    }

    private fun showQuestion() {
        // 問題表示処理(showQuestionメソッド)


        // Todo 1. 前の問題の暗記済フラグをDB登録（更新）
        if (intCount > 0) {  //２問目以降のときのみ発生
            val selectedDB = realm.where(WordDB::class.java).equalTo(getString(R.string.db_field_question),
                wordList[intCount - 1].strQuestion).findFirst()
            realm.beginTransaction()
            selectedDB?.boolMemoryFrag = boolMemorized
            realm.commitTransaction()
        }


        // 2. のこり問題数を１つ減らして表示
        intCount++
        textViewRemaining.text = (intLength - intCount).toString()


        // 3. 今回の問題表示・前のこたえ消去（画像と文字）
        imageViewFlashAnswer.visibility = View.INVISIBLE
        textViewFlashAnswer.text = ""
        imageViewFlashQuestion.visibility = View.VISIBLE
        textViewFlashQuestion.text = wordList[intCount - 1].strQuestion

        // 4. ボタン①を「こたえを見る」ボタンに
        buttonNext.setBackgroundResource(R.drawable.image_button_go_answer)

        // 5. 問題の単語が暗記済の場合はチェックを入れる
        checkBox.isChecked = wordList[intCount - 1].boolMemoryFrag
        boolMemorized = checkBox.isChecked
    }
}

















