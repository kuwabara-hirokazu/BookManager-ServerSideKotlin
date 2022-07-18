package com.book.manager.application.service

import com.book.manager.domain.model.Book
import com.book.manager.domain.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminBookService(
    private val bookRepository: BookRepository
) {
    // トランザクションを貼り、処理が正常に終わればコミット、 例外が発生した場合はロールバックする
    @Transactional
    fun register(book: Book) {
        bookRepository.findWithRental(book.id)?.let { throw IllegalArgumentException("既に存在する書籍ID: ${book.id}") }
        bookRepository.register(book)
    }
}