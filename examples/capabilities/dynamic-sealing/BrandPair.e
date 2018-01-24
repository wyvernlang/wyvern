# E sample
 def makeBrandPair(nickname) {
     def noObject{}
     var shared := noObject
     def makeSealedBox(obj) {
         def box {
             to shareContent() {shared := obj}
         }
         return box
     }
     def sealer {
         to seal(obj) {return makeSealedBox(obj)}
     }
     def unsealer {
         to unseal(box) {
             shared := noObject
             box.shareContent()
             if (shared == noObject) {throw("invalid box")}
             def contents := shared
             shared := noObject
             return contents
         }
     }
     return [sealer, unsealer]
 }
 
