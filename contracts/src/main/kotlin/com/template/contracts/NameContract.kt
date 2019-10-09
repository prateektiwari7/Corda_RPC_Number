package com.template.contracts

import com.template.states.NameState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction


class NameContract : Contract {

    companion object{
        const val ID = "com.template.contracts.NameContract"
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands.Create>()
        requireThat {
            "No Inputs should be empty" using (tx.inputs.isEmpty())
            "Only one output state should be created" using (tx.outputs.size ==1)
            val out = tx.outputsOfType<NameState>().single()
            "Name cant be 7 character long " using (out.name.length != 7)
            "Name cant be Same as Admin 'Prateek' " using (!(out.name.equals("Prateek")))


        }
    }

    interface Commands : CommandData {
        class Create : NameContract.Commands;
    }
}