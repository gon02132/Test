var express = require('express');
var app = express();

app.get('/', function(req,res){
  req.send('hello world');
});
app.listen(3000, function(){
  console.log('server on');
});
