var express = require('express');
var router = express.Router();
var https = require('https');
var pg = require('pg');
var bodyParser = require('body-parser');
var request = require('request');
var jsdom = require("jsdom");
var session = require('express-session')
https.post = require('https-post');

var result = "dsfaasdf";

/* GET home page. */
router.get('/', function(req, res, next) {
    res.render('index', {
        test: result,
        sessionId: req.session.id,
    });

});




module.exports = router;