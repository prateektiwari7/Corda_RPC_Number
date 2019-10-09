package com.template.states

import com.template.contracts.CommericalPaper

import net.corda.core.contracts.*
import net.corda.core.crypto.NullKeys
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.AnonymousParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.util.*


@BelongsToContract(CommericalPaper::class)
data class StatePaper(        val issuance: Party,
                              override val owner: AbstractParty,
                              val faceValue: Amount<Currency>

) : OwnableState{
    override val participants get() = listOf(owner,issuance)
    fun withoutOwner() = copy(owner = AnonymousParty(NullKeys.NullPublicKey))
    override fun withNewOwner(newOwner: AbstractParty) = CommandAndState(CommericalPaper.Commands.Move(), copy(owner = newOwner))


}