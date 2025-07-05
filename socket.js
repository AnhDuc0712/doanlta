const { Server } = require("socket.io");
const User = require("./models/User");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");

function setupSocket(server) {
    const io = new Server(server, {
        cors: {
            origin: "*",
            methods: ["GET", "POST"]
        },
        transports: ["websocket"]
    });

    io.on("connection", (socket) => {
        console.log("🔌 Socket connected:", socket.id);

        socket.on("login", async (data) => {
            console.log("📥 login:", data);
            try {
                const { username, password } = data;
                const user = await User.findOne({ username });

                if (!user || !await bcrypt.compare(password, user.password)) {
                    socket.emit("login_response", {
                        success: false,
                        message: "Tên đăng nhập hoặc mật khẩu sai"
                    });
                    return;
                }

                const token = jwt.sign({ userId: user._id }, "SECRET_KEY", { expiresIn: "1h" });

                socket.emit("login_response", {
                    success: true,
                    message: "Đăng nhập thành công",
                    token,
                    user: {
                        name: user.fullName,
                        username: user.username,
                        email: user.email,
                        phone: user.phone
                    }
                });
            } catch (error) {
                socket.emit("login_response", {
                    success: false,
                    message: "Lỗi server: " + error.message
                });
            }
        });

        socket.on("register", async (data) => {
            console.log("📥 register:", data);
            try {
                const { name, username, email, phone, password } = data;

                const existingUser = await User.findOne({ $or: [{ username }, { email }] });
                if (existingUser) {
                    socket.emit("register_response", {
                        success: false,
                        message: "Tên đăng nhập hoặc email đã tồn tại"
                    });
                    return;
                }

                const hashedPassword = await bcrypt.hash(password, 10);
                const user = new User({
                    fullName: name,
                    username,
                    email,
                    phone,
                    password: hashedPassword
                });

                await user.save();

                socket.emit("register_response", {
                    success: true,
                    message: "Đăng ký thành công",
                    user: {
                        name: user.fullName,
                        username: user.username,
                        email: user.email,
                        phone: user.phone
                    }
                });
            } catch (error) {
                socket.emit("register_response", {
                    success: false,
                    message: "Lỗi server: " + error.message
                });
            }
        });

        socket.on("disconnect", () => {
            console.log("⚠️ Socket disconnected:", socket.id);
        });
    });

    return io;
}

module.exports = setupSocket;