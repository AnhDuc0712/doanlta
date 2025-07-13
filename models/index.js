const Sequelize = require('sequelize');
const sequelize = require('../config/database');

const db = {};
db.Sequelize = Sequelize;
db.sequelize = sequelize;

db.User = require('./user')(sequelize, Sequelize);
db.Task = require('./task')(sequelize, Sequelize);
db.ChatHistory = require('./chatHistory')(sequelize, Sequelize);
db.Payment = require('./payment')(sequelize, Sequelize);
db.Subscription = require('./subscription')(sequelize, Sequelize);
db.Notification = require('./notification')(sequelize, Sequelize);
db.Group = require('./group')(sequelize, Sequelize);
db.GroupMember = require('./groupMember')(sequelize, Sequelize);
db.GroupTask = require('./groupTask')(sequelize, Sequelize);

// Relations
db.User.hasMany(db.Task);
db.Task.belongsTo(db.User);

db.User.hasMany(db.ChatHistory);
db.ChatHistory.belongsTo(db.User);

db.User.hasMany(db.Payment);
db.Payment.belongsTo(db.User);

db.User.hasOne(db.Subscription);
db.Subscription.belongsTo(db.User);

db.User.hasMany(db.Notification);
db.Notification.belongsTo(db.User);

db.User.hasMany(db.Group, { foreignKey: 'OwnerId' });
db.Group.belongsTo(db.User, { foreignKey: 'OwnerId' });

db.Group.belongsToMany(db.User, { through: db.GroupMember, foreignKey: 'GroupId' });
db.User.belongsToMany(db.Group, { through: db.GroupMember, foreignKey: 'MemberId' });

db.Group.belongsToMany(db.Task, { through: db.GroupTask });
db.Task.belongsToMany(db.Group, { through: db.GroupTask });

module.exports = db;
