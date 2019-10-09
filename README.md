# Rpc flow list Corda


1. This is the rpc end point protocol of corda.

2. Check the **Flows.kt** code and its endpoint at **Responder.java**

3. **Flows.kt** contains the Party and number as input.(Party, number);

4. Check the **build.gradle** and run its deploynodes test to build all the nodes.

5. ##### output of build.gradle


```ruby
Generating contract implementations whitelist 
New NetworkParameters {
      minimumPlatformVersion=4  
      notaries=[NotaryInfo(identity=O=Notary, L=London, C=GB, validating=false)]
      maxMessageSize=10485760
      maxTransactionSize=524288000 
      whitelistedContractImplementations {
      }
      eventHorizon=PT720H
      packageOwnership {

      }
      modifiedTime=2019-03-25T11:12:19.184Z
      epoch=1
  } 
Bootstrapping complete!
BUILD SUCCESSFUL in 1m 42s
11 actionable tasks: 11 executed
16:42:20: Task execution finished 'deployNodes'.
```

6. ##### Under /build/nodes run ./runnodes


7. **Output of command**

``` ruby 
Mon Mar 25 16:46:31 IST 2019>>> flow list
com.template.flows.Initiator
net.corda.core.flows.ContractUpgradeFlow$Authorise
net.corda.core.flows.ContractUpgradeFlow$Deauthorise
net.corda.core.flows.ContractUpgradeFlow$Initiate
```

9. **Use the command** 

``` ruby
start Initiator counterparty: PartyA , number: 2

 ✅   Starting
➡️   Done
Flow completed with result: 3

```






