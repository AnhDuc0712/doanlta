module.exports = (sequelize, DataTypes) => {
    return sequelize.define('ChatHistory', {
        ChatId: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
        UserId: { type: DataTypes.INTEGER },
        Message: { type: DataTypes.TEXT, allowNull: false },
        Reply: { type: DataTypes.TEXT },
        CreatedAt: { type: DataTypes.DATE, defaultValue: DataTypes.NOW }
    }, { timestamps: false });
};
  