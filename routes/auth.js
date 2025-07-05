const express = require('express');
const router = express.Router();
const db = require('../db');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

router.post('/register', async (req, res) => {
  const { FullName, Email, Password } = req.body;
  const hashed = await bcrypt.hash(Password, 10);
  const sql = "INSERT INTO Users (FullName, Email, Password) VALUES (?, ?, ?)";
  db.query(sql, [FullName, Email, hashed], (err, result) => {
    if (err) return res.status(500).json({ error: "Email đã tồn tại!" });
    res.json({ message: "Đăng ký thành công", userId: result.insertId });
  });
});

router.post('/login', (req, res) => {
  const { Email, Password } = req.body;
  db.query("SELECT * FROM Users WHERE Email = ?", [Email], async (err, results) => {
    if (err || results.length === 0) return res.status(401).json({ error: "Sai email hoặc mật khẩu" });
    const user = results[0];
    const isMatch = await bcrypt.compare(Password, user.Password);
    if (!isMatch) return res.status(401).json({ error: "Sai mật khẩu" });
    const token = jwt.sign({ userId: user.UserId }, "SECRET_KEY");
    res.json({ message: "Đăng nhập thành công", token, user });
  });
});

module.exports = router;