module.exports = (sequelize, DataTypes) => {
    return sequelize.define('Task', {
        TaskId: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
        UserId: { type: DataTypes.INTEGER },
        Title: { type: DataTypes.STRING, allowNull: false },
        Description: { type: DataTypes.TEXT },
        Date: { type: DataTypes.DATEONLY, allowNull: false },
        Time: { type: DataTypes.TIME },
        IsDone: { type: DataTypes.BOOLEAN, defaultValue: false },
        PriorityLevel: { type: DataTypes.INTEGER, defaultValue: 1 }
    }, { timestamps: false });
};
  