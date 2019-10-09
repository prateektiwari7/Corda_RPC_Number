package com.template.flows


import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.CommericalPaper
import com.template.states.StatePaper
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateRef
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

@InitiatingFlow
@StartableByRPC
class CommericalFlow(   val owner: AbstractParty,
                        val faceValue: Amount<Currency>) : FlowLogic<SignedTransaction>() {
    override val progressTracker = ProgressTracker()



    @Suspendable
    override fun call() : SignedTransaction {


        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val namestate = StatePaper(ourIdentity,owner,faceValue)
        val issuecommand = Command(CommericalPaper.Commands.Issue(),listOf(ourIdentity.owningKey , owner.owningKey))



        val txBuilder = TransactionBuilder(notary = notary)
                .addOutputState(namestate, CommericalPaper.ID)
                .addCommand(issuecommand)

        txBuilder.verify(serviceHub)
        val tx = serviceHub.signInitialTransaction(txBuilder)

        val sessions = initiateFlow(owner as Party)
        val stx = subFlow(CollectSignaturesFlow(tx, listOf(sessions) , CollectSignaturesFlow.tracker()))
        subFlow(FinalityFlow(stx, listOf( sessions)))
        return stx
    }
}

@InitiatedBy(CommericalFlow::class)
class CommericalFlowResponder(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {

            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow).id
        return subFlow(ReceiveFinalityFlow(counterpartySession, txWeJustSignedId))
    }
}


@InitiatingFlow
@StartableByRPC
class TransferFlow(val tx: StateRef,
                   val newowner: Party) : FlowLogic<SignedTransaction>() {
    override val progressTracker = ProgressTracker()



    @Suspendable
    override fun call() : SignedTransaction {


        val notary = serviceHub.networkMapCache.notaryIdentities.first()


        val InState = serviceHub.toStateAndRef<StatePaper>(tx)

        val output= InState.state.data.withNewOwner(newowner).ownableState

        //val command = Command(CommericalPaper.Commands.Move(), listOf(ourIdentity.owningKey))
        val command = Command(CommericalPaper.Commands.Move(), listOf(ourIdentity.owningKey,newowner.owningKey))


        val txBuilder = TransactionBuilder(notary)
                .addInputState(InState)
                .addOutputState(output, CommericalPaper.ID)
                .addCommand(command)

        //returns ERROR
//        val txBuilder = TransactionBuilder(notary)
//                .addOutputState(output, CommericalPaper.ID)
//                .addCommand(command)


        txBuilder.verify(serviceHub)
        val tx = serviceHub.signInitialTransaction(txBuilder)

        val sessions = initiateFlow(newowner)
        val stx = subFlow(CollectSignaturesFlow(tx, listOf(sessions)))
        return subFlow(FinalityFlow(stx, listOf(sessions)))
    }
}

@InitiatedBy(TransferFlow::class)
class TransferFlowresponder(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {

            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow)
        return subFlow(ReceiveFinalityFlow(counterpartySession, txWeJustSignedId.id))
    }
}