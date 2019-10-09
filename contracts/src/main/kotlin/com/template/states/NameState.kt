package com.template.states

import com.template.contracts.NameContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

@BelongsToContract(NameContract::class)
data class NameState(val name: String,
                     val lastname : String,
                     val current: Party,
                     val party: Party,
                     val linearId: UniqueIdentifier) : ContractState {

    override val participants: List<AbstractParty> = listOf(party,current);
    //To change initializer of created properties use File | Settings | File Templates.

}