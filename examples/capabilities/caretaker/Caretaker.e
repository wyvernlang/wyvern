def caretakerMaker {
  to make(var target) :any {
    def caretaker {
      match [verb :String, args :any[]] {
        E.call(target, verb, args)
    }   }
    def revoker {
        to revoke() :void {
           target := null
    }   }
    ^[caretaker, revoker]
} }

// So instead of bob.foo(carol) Alice can say:

def [carol2, carol2Rvkr] := caretakerMaker.make(carol)
bob.foo(carol2)
