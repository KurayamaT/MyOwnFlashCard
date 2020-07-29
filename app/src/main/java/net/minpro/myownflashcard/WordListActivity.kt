package net.minpro.myownflashcard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_word_list.*

class WordListActivity : AppCompatActivity(), AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    lateinit var realm: Realm
    lateinit var results: RealmResults<WordDB>
    lateinit var wordList: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)

//    Todo 画面が開いたとき
//    2.前画面で設定した背景色を設定
        constraintLayoutWordList.setBackgroundResource(intBackGroundColor)

//    「新しい単語の追加」ボタンを押した場合
//    「EditActivity」へ（ステータスをIntentで渡す）
        buttonAddNewWord.setOnClickListener {
            val intent = Intent(this@WordListActivity, EditActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_status),getString(R.string.status_add))
            startActivity(intent)
        }

//    「もどる」ボタンを押した場合
//     今の画面を閉じて「MainActivity」へ
        buttonBuck.setOnClickListener {
            finish()
        }
        listView.onItemClickListener = this
        listView.onItemLongClickListener = this
    }

    override fun onResume() {
        super.onResume()
        realm= Realm.getDefaultInstance()
        //    1.DBに登録している単語を一覧表示(ListView)
        results = realm.where(WordDB::class.java).findAll().sort(getString(R.string.db_field_answer))


        wordList = ArrayList<String>()
        val length = results.size

//        for (i in 0..length - 1) {
//            if (results[i]!!.boolMemoryFrag)
//                wordList.add(results[i]!!.strAnswer + " : " + results[i]!!.strQuestion+" 【暗記済】 ")
//            } else {
//                wordList.add(results[i]!!.strAnswer + " : " + results[i]!!.strQuestion)
//            }
//
//        }

        results.forEach {
            if(it.boolMemoryFrag) {
                wordList.add(it.strAnswer + " : " + it.strQuestion + " 【暗記済】 ")
            }else{
                wordList.add(it.strAnswer + " : " + it.strQuestion)
            }
        }

        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wordList)
        listView.adapter = adapter
    }



    override fun onPause() {
        super.onPause()
        realm.close()

    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val selectedDB = results[p2]
        val strSelectedQuestion = selectedDB?.strQuestion
        val strSelectedAnswer = selectedDB?.strAnswer
        val intent = Intent(this@WordListActivity, EditActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_question),strSelectedQuestion)
        intent.putExtra(getString(R.string.intent_key_answer),strSelectedAnswer)
        intent.putExtra(getString(R.string.intent_key_position),p2)
        intent.putExtra(getString(R.string.intent_key_status),getString(R.string.status_change))
        startActivity(intent)

    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, p2: Int, id: Long): Boolean {

        val selectedDB = results[p2]
        realm.beginTransaction()
        selectedDB!!.deleteFromRealm()
        realm.commitTransaction()
        wordList.removeAt(p2)
        listView.adapter = adapter

        return true

    }

}

