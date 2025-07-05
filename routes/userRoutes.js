const express = require("express");
const router = express.Router();
const User = require("./models/User");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const multer = require("multer");
const path = require("path");

// Configure multer for avatar uploads
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, "uploads/");
    },
    filename: (req, file, cb) => {
        cb(null, Date.now() + path.extname(file.originalname));
    }
});
const upload = multer({ storage });

// Upload avatar
router.post("/upload-avatar", upload.single("avatar"), async (req, res) => {
    try {
        const user = await User.findById(req.body.userId);
        if (!user) {
            return res.status(404).json({ message: "Người dùng không tồn tại" });
        }

        user.avatar = `/uploads/${req.file.filename}`;
        await user.save();

        res.json({ avatarUrl: user.avatar });
    } catch (error) {
        res.status(500).json({ message: "Lỗi server: " + error.message });
    }
});

module.exports = router;