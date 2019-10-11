package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap

@CordaSerializable
class View1(
        val number: Int,
        val name: String
)
// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator1(var party1 : Party, var number: Int, var name: String) : FlowLogic<View1>() {


    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): View1 {
        // Initiator flow logic goes here.

        val party11: FlowSession

        val notary = serviceHub.networkMapCache.notaryIdentities.first()

        party11= initiateFlow(party1)


        party11.send(View1(number,name))

        val rece : View1

        rece = party11.receive<View1>().unwrap { it }


        return rece;
    }
}

@InitiatedBy(Initiator1::class)
class Responder1(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        // Responder flow logic goes here.

        val number: View1

        number = counterpartySession.receive<View1>().unwrap { it }

        val number1 : Int


        number1 = number.number + 1

        counterpartySession.send(View1(number1,"Prateek"));

        val signedTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {

            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow).id
        return subFlow(ReceiveFinalityFlow(counterpartySession, txWeJustSignedId))
    }
}

