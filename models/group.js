module.exports = (sequelize, DataTypes) => {
    return sequelize.define('Group', {
        GroupId: { type: DataTypes.STRING, primaryKey: true },
        GroupName: { type: DataTypes.STRING, allowNull: false },
        OwnerId: { type: DataTypes.INTEGER }
    }, { timestamps: false });
};
  