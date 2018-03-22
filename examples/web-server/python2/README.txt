A very simple HTTP server.

Usage:
    wypy webserver.wyv
    python webserver.wyv.py

Send a GET request::
    curl http://localhost:8888

Send a HEAD request::
    curl -I http://localhost:8888

Send a POST request::
    curl -d "foo=bar&bin=baz" http://localhost:8888
