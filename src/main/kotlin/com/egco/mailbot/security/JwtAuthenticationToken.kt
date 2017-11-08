package com.egco.mailbot.security

import com.egco.mailbot.domain.User
import com.egco.mailbot.security.model.token.RawAccessJwtToken
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken : AbstractAuthenticationToken {

    /**
     * Creates a token with the supplied array of authorities.

     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     * *                    represented by this authentication object.
     */

    private var rawAccessToken: RawAccessJwtToken? = null
    private var user: User? = null

    constructor(unsafeToken: RawAccessJwtToken) : super(null) {
        this.rawAccessToken = unsafeToken
        this.isAuthenticated = false
    }

    constructor(user: User, authorities: Collection<GrantedAuthority>?) : super(authorities) {
        this.eraseCredentials()
        this.user = user
        super.setAuthenticated(true)
    }

    override fun setAuthenticated(authenticated: Boolean) {
        if (authenticated) {
            throw IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead")
        }
        super.setAuthenticated(false)
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
        this.rawAccessToken = null
    }

    override fun getCredentials(): Any? {
        return this.rawAccessToken
    }

    override fun getPrincipal(): Any? {
        return this.user
    }
}