// models/user.js
module.exports = (sequelize, DataTypes) => {
    return sequelize.define('User', {
        UserId: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
        FullName: { type: DataTypes.STRING, allowNull: false },
        Email: { type: DataTypes.STRING, allowNull: false, unique: true },
        Password: { type: DataTypes.STRING, allowNull: false },
        Username: { type: DataTypes.STRING, allowNull: false, unique: true },
        Phone: { type: DataTypes.STRING },
        Photo: { type: DataTypes.STRING },
        CreatedAt: { type: DataTypes.DATE, defaultValue: DataTypes.NOW },
        Role: { type: DataTypes.STRING, defaultValue: 'User' }
    }, { timestamps: false });
};
