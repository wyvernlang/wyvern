lambda: bootstrap node_modules
	wyby lambda.wyv
	node ../backend/boot.js lambda.wyb > lambda.js

bootstrap: ../backend/boot.js
	cd ../backend && sh bootstrap.sh && cd ../selfhost

node_modules: ../native/javascript/node_modules
	cd ../native/javascript && npm install && cd ../../selfhost
