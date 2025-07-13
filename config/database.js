require('dotenv').config();
const { Sequelize } = require('sequelize');

const sequelize = new Sequelize(
    process.env.DB_NAME,     // dalta
    process.env.DB_USER,     // sa
    process.env.DB_PASSWORD, // root
    {
        host: process.env.DB_HOST,    // localhost
        dialect: process.env.DB_DIALECT, // mssql
        port: process.env.DB_PORT || 1433,
        dialectOptions: {
            options: {
                encrypt: false, // false khi chạy local, true khi dùng Azure
                trustServerCertificate: true
            }
        },
        logging: false // Tắt log SQL ra console nếu bạn muốn
    }
);

module.exports = sequelize;
