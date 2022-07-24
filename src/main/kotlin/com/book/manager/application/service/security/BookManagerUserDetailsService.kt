package com.book.manager.application.service.security

import com.book.manager.application.service.AuthenticationService
import com.book.manager.domain.enum.RoleType
import com.book.manager.domain.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class BookManagerUserDetailsService(
    private val authenticationService: AuthenticationService
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        val user = authenticationService.findUser(username)  // DBからユーザー情報を取得
        return user?.let { BookManagerUserDetails(user) }  // 取得したユーザー情報の認可処理
    }
}

// springframework.securityのUserDetailを継承
// パスワードの比較や認可処理をフレームワーク側で実現してくれる。その際にパスワードの暗号化処理もされる。
data class BookManagerUserDetails(
    val id: Long,
    val email: String,
    val pass: String,
    val roleType: RoleType
) : UserDetails {
    constructor(user: User) : this(user.id, user.email, user.password, user.roleType)

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return AuthorityUtils.createAuthorityList(this.roleType.toString())  // 認可が必要なパスの場合、この関数で取得した権限の情報でチェックされる
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return this.email
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return this.pass
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}