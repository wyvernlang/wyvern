all: bootstrap node_modules test

test: test.sh
	./test.sh

bootstrap: ../backend/boot.js
	cd ../backend && sh bootstrap.sh && cd ../selfhost

node_modules: ../native/javascript/node_modules
	cd ../native/javascript && npm install && cd ../../selfhost
