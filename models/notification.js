module.exports = (sequelize, DataTypes) => {
    return sequelize.define('Notification', {
        NotificationId: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
        UserId: { type: DataTypes.INTEGER },
        Title: { type: DataTypes.STRING, allowNull: false },
        Content: { type: DataTypes.TEXT, allowNull: false },
        NotifyTime: { type: DataTypes.DATE, allowNull: false },
        IsRead: { type: DataTypes.BOOLEAN, defaultValue: false }
    }, { timestamps: false });
};
  