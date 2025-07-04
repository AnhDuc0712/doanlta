const express = require('express');
const router = express.Router();
const db = require('../db');
const verifyToken = require('../middleware/verifyToken');

router.get('/', verifyToken, (req, res) => {
  db.query("SELECT * FROM Tasks WHERE UserId = ?", [req.userId], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(results);
  });
});

router.post('/', verifyToken, (req, res) => {
  const { Title, Description, Date, Time, PriorityLevel } = req.body;
  const sql = "INSERT INTO Tasks (UserId, Title, Description, Date, Time, PriorityLevel) VALUES (?, ?, ?, ?, ?, ?)";
  db.query(sql, [req.userId, Title, Description, Date, Time, PriorityLevel], (err, result) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ message: "Tạo Task thành công", TaskId: result.insertId });
  });
});

module.exports = router;