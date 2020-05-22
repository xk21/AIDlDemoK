package com.tt.aidlclient

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tt.server.Book
import com.tt.server.BookCallBack
import com.tt.server.IBookService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var isConnectService = false
    private var iBookService: IBookService? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_3.movementMethod = ScrollingMovementMethod()
        bt_1.setOnClickListener(this)
        bt_2.setOnClickListener(this)
        bt_3.setOnClickListener(this)
        bt_4.setOnClickListener(this)
        bt_5.setOnClickListener(this)
        bt_6.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        connectService()
    }


    override fun onClick(v: View?) {
        if (!isConnectService) {
            connectService()
            Toast.makeText(
                this, "当前与服务端处于未连接状态，正在尝试重连，" +
                        "请稍后再试", Toast.LENGTH_SHORT
            ).show()
            return
        }
        when (v!!.id) {
            R.id.bt_1 -> {
                val book1 = Book("客户端书 in")
                try {
                    iBookService?.addInBook(book1)
                    tv_1.text = "in由客户端流向服务端,客户端不能收到服务端具体信息,如下书名:${book1.name}"
                    getBookList(iBookService?.getaaBookList() as List<Book>)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            R.id.bt_2 -> {
                val book2 = Book("客户端书 out")
                try {
                    iBookService?.addOutBook(book2)
                    tv_1.text = "out由服务端流向客户端,服务端不能收到客户端具体信息,如下书名:${book2.name}"
                    getBookList(iBookService?.getaaBookList() as List<Book>)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            R.id.bt_3 -> {
                val book3 = Book("客户端书 inOut")
                try {
                    iBookService?.addInOutBook(book3)
                    tv_1.text = " inout客户端和服务端双向流通,如下书名:${book3.name}"
                    getBookList(iBookService?.getaaBookList() as List<Book>)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            R.id.bt_4 -> try {
                iBookService?.registerCallback(bookCallBack)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            R.id.bt_5 -> try {
                iBookService?.registerCallback(bookCallBack)
                iBookService?.get4BookName()
                getBookList(iBookService?.getaaBookList() as List<Book>)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            R.id.bt_6 -> try {
                iBookService?.registerCallback(bookCallBack)
                iBookService?.get3BookName()
                getBookList(iBookService?.getaaBookList() as List<Book>)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }
    fun getBookList(bookList: List<Book>) {
        if (bookList.size > 0) {
            val buffer = StringBuffer()
            for (i in bookList.indices) {
                buffer.append("${i + 1}  号: ${bookList[i].name} \n")
            }
            tv_3.text = buffer
        }
    }

    //链接服务端
    private fun connectService() {
        Log.d("szjjyh", "jjyh connectService")
        val intent = Intent()
        intent.component =
            ComponentName("com.tt.server", "com.tt.server.BookService")

        intent.action = "com.tt.server.BookService"
//        intent.setPackage("com.tt.server")
        val bindService = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d("szjjyh", "jjyh connectService: "+bindService)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d("szjjyh", "jjyh 完成AIDLService服务")
            iBookService = IBookService.Stub.asInterface(service)
            isConnectService = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d("szjjyh", "jjyh 无法绑定aidlserver的AIDLService服务")
            isConnectService = false
            iBookService =null
        }
    }

    //实现回调接口获取相关数据
    private val bookCallBack = object : BookCallBack.Stub() {
        override fun get4BookName(bookName: String?) {
            tv_1.text = "获取到第4号书名:$bookName"
        }

        override fun get3BookName(bookName: String?) {
            tv_1.text = "获取到删除的第3号书名:$bookName"
        }

        override fun getCount(count: Int) {
            runOnUiThread { tv_2.text = "收到服务端数据:$count" }
        }

    }
}
