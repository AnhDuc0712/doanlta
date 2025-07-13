const db = require('../models');
const User = db.User;

exports.register = async (req, res) => {
    try {
        const { FullName, Email, Password } = req.body;
        const user = await User.create({ FullName, Email, Password });
        res.status(201).json(user);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
};

exports.login = async (req, res) => {
    try {
        const { Email, Password } = req.body;
        const user = await User.findOne({ where: { Email } });
        if (!user || user.Password !== Password) {
            return res.status(401).json({ message: 'Invalid credentials' });
        }
        res.json(user);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
};
