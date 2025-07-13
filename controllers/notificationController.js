// controllers/notificationController.js
const { Notification } = require('../models');

exports.createNotification = async (req, res) => {
    try {
        const { userId, title, body } = req.body;

        if (!userId || !title || !body) {
            return res.status(400).json({ success: false, message: 'Thiếu dữ liệu.' });
        }

        const newNotification = await Notification.create({
            userId,
            title,
            body
        });

        return res.status(201).json({
            success: true,
            data: newNotification
        });
    } catch (error) {
        console.error('❌ Lỗi tạo thông báo:', error);
        return res.status(500).json({ success: false, message: 'Lỗi server.' });
    }
};

exports.getNotifications = async (req, res) => {
    try {
        const { userId } = req.query;

        if (!userId) {
            return res.status(400).json({ success: false, message: 'Thiếu userId.' });
        }

        const notifications = await Notification.findAll({
            where: { userId },
            order: [['createdAt', 'DESC']]
        });

        return res.status(200).json({
            success: true,
            data: notifications
        });
    } catch (error) {
        console.error('❌ Lỗi lấy thông báo:', error);
        return res.status(500).json({ success: false, message: 'Lỗi server.' });
    }
};
