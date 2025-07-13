-- Tạo cơ sở dữ liệu
CREATE DATABASE dalta;
GO
USE dalta;
GO

-- Bảng Users
CREATE TABLE Users (
    UserId INT IDENTITY(1,1) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE,
    Password NVARCHAR(100) NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    Role NVARCHAR(20) DEFAULT 'User'
);

-- Bảng Tasks
CREATE TABLE Tasks (
    TaskId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT,
    Title NVARCHAR(200) NOT NULL,
    Description NVARCHAR(MAX),
    Date DATE NOT NULL,
    Time TIME,
    IsDone BIT DEFAULT 0,
    PriorityLevel INT DEFAULT 1,
    FOREIGN KEY (UserId) REFERENCES Users(UserId)
);

-- Bảng ChatHistory
CREATE TABLE ChatHistory (
    ChatId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT,
    Message NVARCHAR(MAX) NOT NULL,
    Reply NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserId) REFERENCES Users(UserId)
);

-- Bảng Payments
CREATE TABLE Payments (
    PaymentId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT,
    Amount DECIMAL(10,2) NOT NULL,
    Method NVARCHAR(50) NOT NULL,
    Status NVARCHAR(20) DEFAULT 'Pending',
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserId) REFERENCES Users(UserId)
);

-- Bảng Subscriptions
CREATE TABLE Subscriptions (
    SubscriptionId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT UNIQUE,
    PackageName NVARCHAR(50) NOT NULL,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    IsActive BIT DEFAULT 1,
    FOREIGN KEY (UserId) REFERENCES Users(UserId)
);

-- Bảng Notifications
CREATE TABLE Notifications (
    NotificationId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT,
    Title NVARCHAR(200) NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    NotifyTime DATETIME NOT NULL,
    IsRead BIT DEFAULT 0,
    FOREIGN KEY (UserId) REFERENCES Users(UserId)
);

-- Bảng Groups
CREATE TABLE Groups (
    GroupId NVARCHAR(50) PRIMARY KEY,
    GroupName NVARCHAR(100) NOT NULL,
    OwnerId INT,
    FOREIGN KEY (OwnerId) REFERENCES Users(UserId)
);

-- Bảng GroupMembers
CREATE TABLE GroupMembers (
    GroupId NVARCHAR(50),
    MemberId INT,
    PRIMARY KEY (GroupId, MemberId),
    FOREIGN KEY (GroupId) REFERENCES Groups(GroupId),
    FOREIGN KEY (MemberId) REFERENCES Users(UserId)
);

-- Bảng GroupTasks
CREATE TABLE GroupTasks (
    GroupId NVARCHAR(50),
    TaskId INT,
    PRIMARY KEY (GroupId, TaskId),
    FOREIGN KEY (GroupId) REFERENCES Groups(GroupId),
    FOREIGN KEY (TaskId) REFERENCES Tasks(TaskId)
);
