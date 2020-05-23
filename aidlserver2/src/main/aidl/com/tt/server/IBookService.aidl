// IBookService.aidl
package com.tt.server;
// Declare any non-default types here with import statements
import com.tt.server.Book;
import com.tt.server.BookCallBack;

interface IBookService {

    List<Book> getaaBookList();

    void addInBook(in Book book);

    void addOutBook(out Book book);

    void addInOutBook(inout Book book);

    void get4BookName();

    void get3BookName();

    void registerCallback(BookCallBack bc);

    void unregisterCallback(BookCallBack bc);
}
