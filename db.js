const mysql = require('mysql2');

const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '', // chỉnh lại nếu có mật khẩu
  database: 'dalta'
});

db.connect(err => {
  if (err) throw err;
  console.log('✅ MySQL Connected');
});

module.exports = db;