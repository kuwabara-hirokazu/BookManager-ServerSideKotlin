package com.book.manager.presentation.config

import com.book.manager.application.service.AuthenticationService
import com.book.manager.application.service.security.BookManagerUserDetailsService
import com.book.manager.domain.enum.RoleType
import com.book.manager.presentation.handler.BookManagerAccessDeniedHandler
import com.book.manager.presentation.handler.BookManagerAuthenticationEntryPoint
import com.book.manager.presentation.handler.BookManagerAuthenticationFailureHandler
import com.book.manager.presentation.handler.BookManagerAuthenticationSuccessHandler
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
class SecurityConfig(private val authenticationService: AuthenticationService) : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests() // アクセス権限設定
            .mvcMatchers("/login").permitAll()  // loginAPIに対して未認証ユーザーを含むすべてのアクセスを許可
            .mvcMatchers("/admin/**").hasAuthority(RoleType.ADMIN.toString()) // adminから始まるAPIに対して管理者権限のユーザーのみアクセスを許可
            .anyRequest().authenticated()  // その他のAPIは認証済みユーザーのみアクセスを許可

            .and()
            .csrf()
            .disable()

            .formLogin()  // ユーザー名、パスワードでのログインを有効化
            .loginProcessingUrl("/login")  // ログインAPIのパスを/loginに設定
            .usernameParameter("email")  // ログインAPIに渡すユーザー名のパラメータ名を/emailに設定
            .passwordParameter("pass")  // ログインAPIにパスワードのパラメータ名を/passに設定

            .successHandler(BookManagerAuthenticationSuccessHandler()) // 認証成功時に実行するハンドラーを設定
            .failureHandler(BookManagerAuthenticationFailureHandler()) // 認証失敗時に実行するハンドラーを設定
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(BookManagerAuthenticationEntryPoint()) // 未認証時に実行するハンドラーを設定
            .accessDeniedHandler(BookManagerAccessDeniedHandler()) // 認可失敗時に実行するハンドラーを設定

            .and()
            .cors()
            .configurationSource(corsConfigurationSource()) // CORS(Cross-Origin Resource Sharing)の設定
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(BookManagerUserDetailsService(authenticationService)) // 証処理を実行するクラスの指定
            .passwordEncoder(BCryptPasswordEncoder())  // パスワードの暗号化アルゴリズムをBCryptに指定
    }

    private fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL)  // メソッドをすべて許可
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL)  // ヘッダをすべて許可
        corsConfiguration.addAllowedOrigin("http://localhost:8081")  // アクセス元のドメインを許可
        corsConfiguration.allowCredentials = true

        val corsConfigurationSource = UrlBasedCorsConfigurationSource()
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration)

        return corsConfigurationSource
    }
}