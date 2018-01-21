See: http://wiki.erights.org/wiki/Walnut/Secure_Distributed_Computing/Capability_Patterns#Sealers_and_Unsealers

In E:

? def makeBrandPair := <elib:sealing.makeBrand>
# value: <makeBrand>
 
? def [sealer, unsealer] := makeBrandPair("BrandNickName")
# value: [<BrandNickName sealer>, <BrandNickName unsealer>]
 
? def sealedBox := sealer.seal("secret data")
# value: <sealed by BrandNickName>
 
? unsealer.unseal(sealedBox)
# value: "secret data"
