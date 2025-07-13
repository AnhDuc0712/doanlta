const db = require('../models');
const Subscription = db.Subscription;

exports.createSubscription = async (req, res) => {
    try {
        const subscription = await Subscription.create(req.body);
        res.status(201).json(subscription);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
};

exports.getSubscriptions = async (req, res) => {
    try {
        const subscriptions = await Subscription.findAll();
        res.json(subscriptions);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
};
