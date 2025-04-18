﻿# LabKotlinMockup
```
var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var mysql = require('mysql');
require('dotenv').config();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.get('/', function (req, res) {  
  return res.send({ error: true, message: 'hello' })
}
);

var dbConn = mysql.createConnection({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database : process.env.DB_NAME
});

dbConn.connect();

app.get('/orders', function (req, res) {
  dbConn.query('SELECT * FROM orders', function (error, results, fields) {
    if (error) throw error;
    return res.send(results);
  });
}
);

app.get('/order/:id', function (req, res) {
  let order_id = req.params.id;

  if (!order_id) {
    return res.status(400).send({ error: true, message: 'Please provide order_id' });
  }

  dbConn.query('SELECT * FROM orders where id=?', order_id, function (error, results, fields) {
    if (error) throw error;
    return res.send(results[0]);
  });
}
);

app.post('/order', function (req, res) {
  let order = req.body;

  if (!order) {
    return res.status(400).send({ error: true, message: 'Please provide order' });
  }

  dbConn.query("INSERT INTO orders SET ? ",  order , function (error, results, fields) {
    if (error) throw error;
    return res.send({ error: false, data: results, message: 'New order has been created successfully.' });
  });
}
);

app.delete('/order/:id', function (req, res) {
  let order_id = req.params.id;

  if (!order_id) {
    return res.status(400).send({ error: true, message: 'Please provide order_id' });
  }

  dbConn.query('DELETE FROM orders WHERE id = ?', [order_id], function (error, results, fields) {
    if (error) throw error;
    return res.send({ error: false, data: results, message: 'Order has been deleted successfully.' });
  });
}
);


app.put('/order/:id', function (req, res) {
    let order_id = req.params.id;
    let order = req.body;
  
    if (!order_id || !order) {
      return res.status(400).send({ error: true, message: 'Please provide order and order_id' });
    }
  
    dbConn.query("UPDATE orders SET ? WHERE id = ?", [order, order_id], function (error, results, fields) {
      if (error) throw error;
      return res.send({ error: false, data: results, message: 'Order has been updated successfully.' });
    });
  });

app.listen(3000, function () {
  console.log('Node app is running on port 3000');
}
);
```
