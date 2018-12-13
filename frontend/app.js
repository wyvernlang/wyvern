var debug = require('debug')('node-js-getting-started:server');
var express = require('express')
var path = require('path')
var http = require('http');
var logger = require('morgan');
var bodyParser = require('body-parser');
var session = require('express-session')

var index = require('./routes/index');

var app = express();

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));
app.use(session({
    secret: 'keyboard cat',
    resave: false,
    saveUninitialized: true
}))

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');


app.use('/', index);

module.exports = app;