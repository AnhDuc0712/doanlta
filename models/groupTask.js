module.exports = (sequelize, DataTypes) => {
    return sequelize.define('GroupTask', {
        GroupId: { type: DataTypes.STRING, primaryKey: true },
        TaskId: { type: DataTypes.INTEGER, primaryKey: true }
    }, { timestamps: false });
};
  