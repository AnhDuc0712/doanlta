// controllers/chatController.js
const { ChatHistory } = require('../models');

exports.sendMessage = async (req, res) => {
    try {
        const { senderId, receiverId, message } = req.body;

        if (!senderId || !receiverId || !message) {
            return res.status(400).json({ success: false, message: 'Thiếu thông tin bắt buộc.' });
        }

        const newMessage = await ChatHistory.create({
            senderId,
            receiverId,
            message
        });

        return res.status(201).json({
            success: true,
            data: newMessage
        });
    } catch (error) {
        console.error('❌ Lỗi gửi tin nhắn:', error);
        return res.status(500).json({ success: false, message: 'Lỗi server.' });
    }
};

exports.getMessages = async (req, res) => {
    try {
        const { userId, friendId } = req.query;

        if (!userId || !friendId) {
            return res.status(400).json({ success: false, message: 'Thiếu userId hoặc friendId.' });
        }

        const messages = await ChatHistory.findAll({
            where: {
                [Op.or]: [
                    { senderId: userId, receiverId: friendId },
                    { senderId: friendId, receiverId: userId }
                ]
            },
            order: [['createdAt', 'ASC']]
        });

        return res.status(200).json({
            success: true,
            data: messages
        });
    } catch (error) {
        console.error('❌ Lỗi lấy tin nhắn:', error);
        return res.status(500).json({ success: false, message: 'Lỗi server.' });
    }
};
