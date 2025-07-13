module.exports = (sequelize, DataTypes) => {
    return sequelize.define('Payment', {
        PaymentId: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
        UserId: { type: DataTypes.INTEGER },
        Amount: { type: DataTypes.DECIMAL(10, 2), allowNull: false },
        Method: { type: DataTypes.STRING, allowNull: false },
        Status: { type: DataTypes.STRING, defaultValue: 'Pending' },
        CreatedAt: { type: DataTypes.DATE, defaultValue: DataTypes.NOW }
    }, { timestamps: false });
};
  