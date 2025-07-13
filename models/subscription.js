module.exports = (sequelize, DataTypes) => {
    return sequelize.define('Subscription', {
        SubscriptionId: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
        UserId: { type: DataTypes.INTEGER, unique: true },
        PackageName: { type: DataTypes.STRING, allowNull: false },
        StartDate: { type: DataTypes.DATEONLY, allowNull: false },
        EndDate: { type: DataTypes.DATEONLY, allowNull: false },
        IsActive: { type: DataTypes.BOOLEAN, defaultValue: true }
    }, { timestamps: false });
};
  