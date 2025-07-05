const jwt = require("jsonwebtoken");

function authMiddleware(req, res, next) {
    const token = req.headers.authorization?.replace("Bearer ", "");

    if (!token) {
        return res.status(401).json({ message: "Không có token" });
    }

    try {
        const decoded = jwt.verify(token, "SECRET_KEY");
        req.userId = decoded.userId;
        next();
    } catch (error) {
        return res.status(401).json({ message: "Token không hợp lệ" });
    }
}

module.exports = authMiddleware;