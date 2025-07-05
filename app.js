const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const app = express();

app.use(cors());
app.use(bodyParser.json());

app.use('/api/auth', require('./routes/auth'));
app.use('/api/tasks', require('./routes/tasks'));
// Thêm các routes khác tương tự

app.listen(3000, () => {
  console.log('🚀 Server running on http://localhost:3000');
});