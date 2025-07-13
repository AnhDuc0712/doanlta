module.exports = (sequelize, DataTypes) => {
    return sequelize.define('GroupMember', {
        GroupId: { type: DataTypes.STRING, primaryKey: true },
        MemberId: { type: DataTypes.INTEGER, primaryKey: true }
    }, { timestamps: false });
};
  