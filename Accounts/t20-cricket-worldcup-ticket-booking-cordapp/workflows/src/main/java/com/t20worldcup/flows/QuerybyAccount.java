package com.t20worldcup.flows;
import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.services.vault.QueryCriteria;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * This will be run by the BCCI node and it will issue a nonfungible token represnting each ticket to the dealer account.
 * Buyers can then buy tickets from the dealer account.
 */
@StartableByRPC
public class QuerybyAccount extends FlowLogic<String> {
    private final String whoAmI;
    public QuerybyAccount(String whoAmI) {
        this.whoAmI = whoAmI;
    }
    @Override
    @Suspendable
    public String call() throws FlowException {
        AccountInfo myAccount = UtilitiesKt.getAccountService(this).accountInfo(whoAmI).get(0).getState().getData();
        UUID id = myAccount.getIdentifier().getId();
        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria().withExternalIds(Arrays.asList(id));
        List<StateAndRef<NonFungibleToken>> ticketList = getServiceHub().getVaultService().queryBy(NonFungibleToken.class,criteria).getStates();
        NonFungibleToken nonFungibleToken = ticketList.get(0).getState().getData();
        TokenPointer tokenPointer = (TokenPointer) nonFungibleToken.getIssuedTokenType().getTokenType();
        LinearPointer linearPointer = tokenPointer.getPointer();

        List<StateAndRef<FungibleToken>> Asset = getServiceHub().getVaultService().queryBy(FungibleToken.class,criteria).getStates();
        List<String> myMoney = Asset.stream().map(
                it -> it.getState().getData().getAmount().toString()
        ).collect(Collectors.toList());
        String tks = "I have tickets for: ";
//        for(String ticket: myTickets){
//            tks = tks + ticket + ", ";
//        }
//        String wallets = "\nI have money of: ";
//        for(String mo: myMoney){
//            wallets = wallets + mo + ", ";
//        }
        return null;
    }
}