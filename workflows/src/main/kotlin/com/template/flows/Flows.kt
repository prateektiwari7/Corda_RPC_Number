package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.*
import net.corda.core.flows.*
import net.corda.core.identity.PartyAndCertificate
import net.corda.core.serialization.CordaSerializable
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker
import java.util.*
import net.corda.confidential.IdentitySyncFlow
import net.corda.core.identity.Party
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.seconds
import net.corda.core.utilities.unwrap
import net.corda.finance.contracts.utils.sumCashBy
import net.corda.finance.flows.TwoPartyTradeFlow
import net.corda.finance.workflows.asset.CashUtils
import org.intellij.lang.annotations.Flow
import java.security.PublicKey

class View(
        val number: Int
)
// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator(var party1 : Party, var number: Int) : FlowLogic<Int>() {


    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): Int {
        // Initiator flow logic goes here.

        val party11: FlowSession

        party11= initiateFlow(party1)


        party11.send(number)

        val rece : Int

        rece = party11.receive<Int>().unwrap { it }


        return rece;
    }
}

@InitiatedBy(Initiator::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        // Responder flow logic goes here.

        var number: Int

        number = counterpartySession.receive<Int>().unwrap { it }


        number = number+ 1

        counterpartySession.send(number);

        val signedTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {

            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow).id
        return subFlow(ReceiveFinalityFlow(counterpartySession, txWeJustSignedId))
    }
}


