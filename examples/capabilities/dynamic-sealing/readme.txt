See: http://wiki.erights.org/wiki/Walnut/Secure_Distributed_Computing/Capability_Patterns#Sealers_and_Unsealers

More interestingly see Mint here: http://erights.org/elib/capability/ode/ode-capabilities.html

In E:

? def makeBrandPair := <elib:sealing.makeBrand>
# value: <makeBrand>
 
? def [sealer, unsealer] := makeBrandPair("BrandNickName")
# value: [<BrandNickName sealer>, <BrandNickName unsealer>]
 
? def sealedBox := sealer.seal("secret data")
# value: <sealed by BrandNickName>
 
? unsealer.unseal(sealedBox)
# value: "secret data"

// Mint:

# E sample
def makeMint(name) :any {
    def [sealer, unsealer] := makeBrandPair(name)
    def mint {
        to __printOn(out) :void { out.print(`<$name's mint>`) }
 
        to makePurse(var balance :(int >= 0)) :any {
            def decr(amount :(0..balance)) :void {
                balance -= amount
            }
            def purse {
                to __printOn(out) :void {
                    out.print(`<has $balance $name bucks>`)
                }
                to getBalance() :int { return balance }
                to sprout()     :any { return mint.makePurse(0) }
                to getDecr()    :any { return sealer.seal(decr) }

                to deposit(amount :int, src) :void {
                    unsealer.unseal(src.getDecr())(amount)
                    balance += amount
                }
            }
            return purse
        }
    }
    return mint
}

// Example:

? def carolMint := makeMint("Carol")
# value: <Carol's mint>

? def aliceMainPurse := carolMint.makePurse(1000)
# value: <has 1000 Carol bucks>

? def bobMainPurse := carolMint.makePurse(0)
# value: <has 0 Carol bucks>

? def paymentForBob := aliceMainPurse.sprout()
# value: <has 0 Carol bucks>

? paymentForBob.deposit(10, aliceMainPurse)
