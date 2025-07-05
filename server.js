const express = require("express");
const http = require("http");
const setupSocket = require("./socket");
const authMiddleware = require("./middlewares/authMiddleware");
const mongoose = require("mongoose");

const app = express();
const server = http.createServer(app);

// Middleware
app.use(express.json());

// Connect to MongoDB
mongoose.connect("mongodb://localhost:27017/userdb", {
    useNewUrlParser: true,
    useUnifiedTopology: true,
}).then(() => {
    console.log("Connected to MongoDB");
}).catch((err) => {
    console.error("MongoDB connection error:", err);
});

// Routes
app.get("/private", authMiddleware, (req, res) => {
    res.json({ message: "Bạn đã xác thực thành công!" });
});

// Initialize socket
setupSocket(server);

// Start server
const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`🚀 Server running on port ${PORT}`);
});