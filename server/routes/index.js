var express = require('express');
var router = express.Router();

/* GET home page. */
router.post('/', function(req, res, next) {
  var body = req.body;
  console.log(body);
  res.send(body);
});

router.get('/', function (req, res, next) {
  res.send("A thing");
});

module.exports = router;
