package net.minpro.myownflashcard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import kotlinx.android.synthetic.main.activity_edit.*
import net.minpro.myownflashcard.R.layout.activity_edit
import net.minpro.myownflashcard.R.string.status_change

class EditActivity : AppCompatActivity() {

    lateinit var realm: Realm
    var strQuestion: String = ""
    var strAnswer: String = ""
    var intPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_edit)

        //画面が開いたとき（onCreateメソッド）
        //1.WordListActivityから渡されたIntentの受け取り

        val bundle = intent.extras
        val strStatus = bundle?.getString(getString(R.string.intent_key_status))
        textViewStatus.text = strStatus

        //=> 修正の場合は問題・こたえの表示も
        if (strStatus == getString(status_change)) {
            strQuestion = bundle.getString(getString(R.string.intent_key_question))!!
            strAnswer = bundle.getString(getString(R.string.intent_key_answer))!!
            editTextQuestion.setText(strQuestion)
            editTextAnswer.setText(strAnswer)

            intPosition = bundle.getInt(getString(R.string.intent_key_position))
            editTextQuestion.isEnabled = false
        }else{
            editTextQuestion.isEnabled = true
        }

        //2.前画面で設定した背景色を設定
        constraintLayoutEdit.setBackgroundResource(intBackGroundColor)


        //登録ボタンを押したとき
        buttonRegister.setOnClickListener {

            if (strStatus == getString(R.string.status_add)) {
                //1.「新しい単語の追加」の場合
                //=> 単語の登録処理(addNewWordメソッド)
                addNewWord()
            } else {
                //Todo 2.「登録した単語の修正」の場合
                //=> 単語の修正処理(changeWordメソッド)
                changeWord()
            }
        }

        //Todo もどるボタンを押したとき
        buttonBack2.setOnClickListener {
            //Todo 1.今の画面を閉じて単語一覧画面に戻る
            finish()

        }
    }

    override fun onResume() {
        super.onResume()
        realm = Realm.getDefaultInstance()

    }

    override fun onPause() {
        super.onPause()
        realm.close()
    }


    private fun changeWord() {
        //単語の修正処理（changeWordメソッド）
        //1.選択した行番号のレコードをDBから取得
        val results  = realm.where(WordDB::class.java).findAll().sort(getString(R.string.db_field_question))
        val selectedDB = results[intPosition]!!

        val dialog = AlertDialog.Builder(this@EditActivity).apply {
            setTitle(selectedDB.strAnswer + "の変更")
            setMessage("変更するぞ？いいのかぁ？")
            setPositiveButton("Yes"){dialog, which->
                //2.入力した問題・こたえで1のレコードを更新
                realm.beginTransaction()
//        selectedDB.strQuestion = editTextQuestion.text.toString()
                selectedDB.strAnswer = editTextAnswer.text.toString()
                selectedDB.boolMemoryFrag = false
                realm.commitTransaction()

                editTextQuestion.setText("")
                editTextAnswer.setText("")

                Toast.makeText(this@EditActivity, "修正完了しました", Toast.LENGTH_SHORT).show()

                finish()
            }
            setNegativeButton("No"){dialog, which ->}
            show()
        }



    }

    private fun addNewWord() {

        //新しい単語の登録処理（addNewWordメソッド）

        val dialog = AlertDialog.Builder(this@EditActivity).apply {
            setTitle("登録")
            setMessage("登録でいいのかぁ？")
            setPositiveButton("はい"){ dialog, which ->

                try {
                    realm.beginTransaction()
                    val wordDB = realm.createObject(WordDB::class.java,editTextQuestion.text.toString())
                    wordDB.strAnswer = editTextAnswer.text.toString()
//        wordDB.boolMemoryFrag = false

                    finish()


                    //2.入力した文字を入力欄から消す


                    //3.登録完了メッセージ表示(Toast)
                    Toast.makeText(this@EditActivity, "登録が完了しました", Toast.LENGTH_SHORT).show()

                }catch (e: RealmPrimaryKeyConstraintException){
                    Toast.makeText(this@EditActivity, "同じ単語はやめんかい", Toast.LENGTH_SHORT).show()

                }finally {
                    realm.commitTransaction()
                    editTextQuestion.setText("")
                    editTextAnswer.setText("")
                }




            }
            setNegativeButton("いいえ"){dialog, which -> }
            show()

        }


    }

}







