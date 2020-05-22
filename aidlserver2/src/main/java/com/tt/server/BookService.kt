package com.tt.server

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import java.util.*

class BookService : Service() {
    private var bookList = ArrayList<Book>()
    private var isCount = true

    override fun onCreate() {
        super.onCreate()
        Log.d("szjjyh", "aaaa")
        for (i in 1..5) {
            val book = Book("小兵传奇:$i")
            val add = bookList.add(book)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("szjjyh", "onBind")
        return iBinder
    }

    override fun onDestroy() {
        Log.d("szjjyh", "onDestroy")
        super.onDestroy()
    }

    private val remoteCallbackList =
        RemoteCallbackList<BookCallBack>()
    private val iBinder = object : IBookService.Stub() {
        override fun addInBook(book: Book) {
            Log.d("szjjyh", "addInBook")
            book.name = "${book.name}- 服务端改名 in"
            bookList.add(book)
            Log.d("szjjyh", "addInBook 1")
        }

        override fun addOutBook(book: Book) {
            Log.d("szjjyh", "addOutBook")
            book.name = "${book.name}- 服务端改名 out"
            bookList.add(book)
            Log.d("szjjyh", "addOutBook 1")
        }

        override fun addInOutBook(book: Book) {
            Log.d("szjjyh", "addInOutBook")
            book.name = "${book.name}- 服务端改名 inout"
            bookList.add(book)
            Log.d("szjjyh", "addInOutBook 1")
        }

        override fun getaaBookList(): MutableList<Book> {
            Log.d("szjjyh", "getBookList")
            return bookList
        }


        override fun get4BookName() {
            Log.d("szjjyh", "get4BookName")
            query4Book()
        }

        override fun get3BookName() {
            Log.d("szjjyh", "get3BookName")
            query3Book()
        }

        override fun registerCallback(bc: BookCallBack?) {
            val register = remoteCallbackList.register(bc)
            if (isCount) {
                startCount()
            }
        }

        override fun unregisterCallback(bc: BookCallBack?) {
            remoteCallbackList.unregister(bc)
        }

    }

    private fun startCount() {
        isCount = false
        Thread(Runnable {
            try {
                for (i in 1..19) {
                    Thread.sleep(1000)
                    val message = Message()
                    message.what = i
                    timeHandler.sendMessage(message)
                }
                isCount = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    private val timeHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            Log.i(javaClass.simpleName, "发送消息：" + msg.what)
            callBack(msg.what)
            super.handleMessage(msg)
        }
    }

    private fun callBack(info: Int) {
        val N = remoteCallbackList.beginBroadcast()
        try {
            for (i in 0 until N) {
                remoteCallbackList.getBroadcastItem(i).getCount(info)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
            Log.e("szjjyh", "", e)
        }
        remoteCallbackList.finishBroadcast()
    }

    private fun query4Book() {
        val N: Int = remoteCallbackList.beginBroadcast()
        if (bookList.size >= 4) {
            for (i in 0..bookList.size) {
                if (i == 3) {
                    try {
                        for (j in 0 until N) {
                            remoteCallbackList.getBroadcastItem(j)
                                .get4BookName(bookList[i].name)
                        }
                    } catch (e: RemoteException) {
                        Log.e("szjjyh", "jjyh", e)
                    }
                }
            }
        } else {
            try {
                for (j in 0 until N) {
                    remoteCallbackList.getBroadcastItem(j).get4BookName("不存在该书")
                }
            } catch (e: RemoteException) {
                Log.e("szjjyh", "jjyh", e)
            }
        }
        remoteCallbackList.finishBroadcast()
    }

    private fun query3Book() {
        val N: Int = remoteCallbackList.beginBroadcast()
        if (bookList.size >= 3) {
            for (i in 0..bookList.size) {
                if (i == 2) {
                    try {
                        for (j in 0 until N) {
                            remoteCallbackList.getBroadcastItem(j)
                                .get3BookName(bookList[i].name)
                            bookList.removeAt(i)
                        }
                    } catch (e: RemoteException) {
                        Log.e("szjjyh", "jjyh", e)
                    }
                }
            }
        } else {
            try {
                for (j in 0 until N) {
                    remoteCallbackList.getBroadcastItem(j).get3BookName("书籍低于三本")
                }
            } catch (e: RemoteException) {
                Log.e("szjjyh", "jjyh", e)
            }
        }
        remoteCallbackList.finishBroadcast()
    }
}
