package com.book.manager.presentation.aop

import com.book.manager.application.service.security.BookManagerUserDetails
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

private val logger = LoggerFactory.getLogger(LoggingAdvice::class.java)

@Aspect  // AOPにおいて「横断的関心事」の処理を定義
@Component
class LoggingAdvice {

    @Before("execution(* com.book.manager.presentation.controller..*.*(..))")  // 対象の関数を指定。execution(戻り値 パッケージ名.クラス名.関数名(引数の型))
    fun beforeLog(joinPoint: JoinPoint) {  // JoinPointに@Beforeの処理が呼び出される対象の処理(controllerの処理)情報が含まれている
        val user = SecurityContextHolder.getContext().authentication.principal as BookManagerUserDetails
        logger.info("Start: ${joinPoint.signature} userId=${user.id})")  // シグネチャの情報
        logger.info("Class: ${joinPoint.target.javaClass}")
        logger.info("Session: ${(RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request.session.id}")
    }

    @After("execution(* com.book.manager.presentation.controller..*.*(..))")
    fun afterLog(joinPoint: JoinPoint) {
        val user = SecurityContextHolder.getContext().authentication.principal as BookManagerUserDetails
        logger.info("End: ${joinPoint.signature} userId=${user.id}")
    }

    @Around("execution(* com.book.manager.presentation.controller..*.*(..))")
    fun aroundLog(joinPoint: ProceedingJoinPoint): Any? {
        // 前処理
        val user = SecurityContextHolder.getContext().authentication.principal as BookManagerUserDetails
        logger.info("Start Proceed: ${joinPoint.signature} userId=${user.id}")

        // 本処理の実行
        val result = joinPoint.proceed()  // AOP対象の処理を実行

        // 後処理
        logger.info("End Proceed: ${joinPoint.signature} userId=${user.id}")

        // 本処理の結果の返却
        return result
    }

    // 戻り値に応じて実行する後処理
    @AfterReturning("execution(* com.book.manager.presentation.controller..*.*(..))", returning = "returnValue")
    // returningで指定した名前で対象処理の戻り値を扱える
    fun afterReturningLog(joinPoint: JoinPoint, returnValue: Any?) {
        logger.info("End: ${joinPoint.signature} returnValue=${returnValue}")
    }

    // 例外の種類に応じて実行する後処理
    @AfterThrowing("execution(* com.book.manager.presentation.controller..*.*(..))", throwing = "e")
    //  throwing で指定した名前で、関数の引数に例外を渡せる
    fun afterThrowingLog(joinPoint: JoinPoint, e: Throwable) {  // Throwableを変えれば特定のException発生時のみログ出力することも可能
        logger.error("Exception: ${e.javaClass} signature=${joinPoint.signature} message=${e.message}")
    }

}