// Something very simple that is like an editable wiki page(s) that just does GET and POST?
// NB! We are writing something similar in Grace for taint tracking...

import platform.restful // Needs implementation?!

def get (req) {
   res = ...
   restful.asHTML(res)
}

def post (req) {
   key = req.user
   data = ...
   restful.store(key, res)
}
