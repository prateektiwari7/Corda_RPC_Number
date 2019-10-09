package com.template.contracts


import com.template.states.StatePaper
import net.corda.core.contracts.*
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction


class CommericalPaper : Contract {

    companion object{
        const val ID = "com.template.contracts.CommericalPaper"
    }

    override fun verify(tx: LedgerTransaction) {
        //To change body of created functions use File | Settings | File Templates.

        val groups = tx.groupStates(StatePaper::withoutOwner)

        val command = tx.commands.requireSingleCommand<CommericalPaper.Commands>()




        for((inputs,outputs,_) in groups)         {
            when (command.value) {
                is Commands.Move -> requireThat {
                    val input = inputs.single()
                    requireThat {
                        "the transaction is signed by the owner of the CP" using (input.owner.owningKey in command.signers)
                        "the state is propagated" using (outputs.size == 1)
                        // Don't need to check anything else, as if outputs.size == 1 then the output is equal to
                        // the input ignoring the owner field due to the grouping.
                    }

                }

                is Commands.Redeem -> {
                    // Redemption of the paper requires movement of on-ledger cash.
                    val input = inputs.single()
                    // val received = tx.outputs.map { it.data }.sumCashBy(input.owner)
                    requireThat {

                        //   "the received amount equals the face value" using (received == input.faceValue)
                        "the paper must be destroyed" using outputs.isEmpty()
                        "the transaction is signed by the owner of the CP" using (input.owner.owningKey in command.signers)
                    }
                }

                is Commands.Issue -> {
                    val output = outputs.single()

                    requireThat {
                        // Don't allow people to issue commercial paper under other entities identities.
                        "output states are issued by a command signer" using (output.issuance.owningKey in command.signers  )
                        "output values sum to more than the inputs" using (output.faceValue.quantity > 0)
                        // Don't allow an existing CP state to be replaced by this issuance.
                        "can't reissue an existing state" using inputs.isEmpty()
                    }
                }

                is Commands.testing -> {
                    requireThat {
                        "can't reissue an existing state" using inputs.isEmpty()

                    }
                }





            }
        }



    }

    interface Commands : CommandData {
        class Move : TypeOnlyCommandData(), Commands
        class Redeem : TypeOnlyCommandData(), Commands
        class Issue : TypeOnlyCommandData(), Commands
        class testing: TypeOnlyCommandData(),Commands
    }


}