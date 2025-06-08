# P2P File Transfer System

A Java-based Peer-to-Peer (P2P) File Transfer Application that allows users to register, log in, and send or receive files over a network. The system logs all file transfer activities into a MySQL database, enabling traceability and accountability. It features a user-friendly Swing GUI for ease of interaction.

## 🛠️ Core Features

### User Authentication

- Register new users with username, email, and password
- Secure user login system
- Password validation and user session management
- User status tracking (online/offline)

### P2P File Transfer

- Send files to peers over the network using IP address and port
- Receive files via socket connection
- Real-time transfer progress tracking
- Asynchronous transfer using Java threads
- Automatic file conflict resolution (duplicate naming)

### Transfer Logging

- Complete transfer history in MySQL database
- Log file name, size, transfer direction (sent/received), timestamp, and user ID
- Real-time activity logs in the application
- Transfer status tracking (pending, in progress, completed, failed)

### Database Integration

- MySQL database for user accounts and transfer history
- Automatic database and table creation
- JDBC connectivity with connection pooling
- Data persistence and retrieval

### GUI Features

- Intuitive Swing-based user interface
- Login and registration windows
- Main dashboard with file transfer controls
- Transfer history table with sorting and filtering
- Real-time activity log display
- Progress bars for file transfers

## 📂 Technologies Used

- **Java SE 8+** - Core application development
- **Java Swing** - Graphical user interface
- **MySQL** - Relational database for data storage
- **JDBC** - Java Database Connectivity
- **Java Sockets** - Network communication
- **ExecutorService/Threads** - Asynchronous operations
- **Java NIO** - Efficient file I/O operations

## 🚀 Getting Started

### Prerequisites

1. **Java Development Kit (JDK) 8 or higher**

   ```bash
   java -version
   javac -version
   ```

2. **MySQL Server 5.7 or higher**

   - Install MySQL Server
   - Create a database user with appropriate permissions
   - Note the connection details (host, port, username, password)

3. **MySQL Connector/J**
   - Download from: https://dev.mysql.com/downloads/connector/j/
   - Place the JAR file in the `lib/` directory
   - Rename it to `mysql-connector-j-8.2.0.jar` or update the scripts accordingly

### Installation

1. **Clone or download the project**

   ```bash
   git clone <repository-url>
   cd p2p-file-transfer
   ```

2. **Download MySQL Connector**

   - Download the MySQL Connector/J JAR file
   - Place it in the `lib/` directory

3. **Configure Database Connection**
   - Copy the environment template:
     ```bash
     cp .env.example .env
     ```
   - Edit the `.env` file with your database credentials:
     ```env
     DB_URL=jdbc:mysql://localhost:3306/p2p_system
     DB_USER=your_username
     DB_PASSWORD=your_password
     ```
   - See `CONFIGURATION.md` for detailed configuration options

### Compilation

**Windows:**

```bash
compile.bat
```

**Linux/macOS:**

```bash
chmod +x compile.sh
./compile.sh
```

### Running the Application

**Windows:**

```bash
run.bat
```

**Linux/macOS:**

```bash
chmod +x run.sh
./run.sh
```

## 📱 Usage

### First Time Setup

1. **Start the Application**

   - Run the application using the run script
   - The database will be automatically created if it doesn't exist

2. **Register a New User**

   - Click "Register" on the login screen
   - Fill in username, email, and password
   - Click "Register" to create the account

3. **Login**
   - Enter your username and password
   - Click "Login" to access the dashboard

### Sending Files

1. **Select a File**

   - Click "Select File" button
   - Choose the file you want to send
   - File information will be displayed

2. **Enter Peer Information**

   - Enter the peer's IP address
   - Enter the peer's port number (default: 8888)

3. **Send File**
   - Click "Send File" button
   - Monitor progress in the progress bar
   - Check the activity log for status updates

### Receiving Files

1. **Server Status**

   - The application automatically starts a file server
   - Other peers can connect to your IP address and port 8888

2. **Automatic Reception**
   - Incoming files are automatically saved to the `downloads/` directory
   - Transfer progress is shown in real-time
   - Completed transfers appear in the transfer history

### Transfer History

- View all sent and received files in the transfer history table
- See file names, sizes, transfer types, peer information, and timestamps
- Click "Refresh" to update the history
- Transfer status shows: PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED

## 🏗️ Project Structure

```
├── src/main/java/com/p2p/
│   ├── Main.java                 # Application entry point
│   ├── model/                    # Data models
│   │   ├── User.java            # User entity
│   │   ├── Transfer.java        # Transfer record entity
│   │   └── TransferType.java    # Transfer type enumeration
│   ├── database/                 # Database layer
│   │   └── DatabaseManager.java # MySQL operations
│   ├── gui/                      # User interface
│   │   ├── LoginFrame.java      # Login window
│   │   ├── RegistrationFrame.java # Registration window
│   │   └── DashboardFrame.java  # Main application window
│   ├── network/                  # Network layer
│   │   ├── FileServer.java      # Server for receiving files
│   │   ├── FileClient.java      # Client for sending files
│   │   └── PeerDiscovery.java   # Peer discovery utilities
│   └── utils/                    # Utility classes
│       ├── FileUtils.java       # File operation utilities
│       └── ConfigUtils.java     # Configuration management
├── lib/                          # External libraries
│   └── mysql-connector-j-*.jar  # MySQL JDBC driver
├── downloads/                    # Received files directory
├── build/                        # Compiled classes
├── compile.bat/compile.sh        # Compilation scripts
├── run.bat/run.sh               # Execution scripts
└── README.md                     # This file
```

## ⚙️ Configuration

The application uses a configuration file (`config.properties`) for various settings:

- `default.port` - Default server port (8888)
- `max.file.size` - Maximum file size limit (1GB)
- `connection.timeout` - Connection timeout in milliseconds
- `buffer.size` - File transfer buffer size
- `downloads.directory` - Directory for received files

## 🔒 Security Considerations

**Current Implementation:**

- Passwords are stored in plain text (for demonstration)
- No encryption for file transfers
- Basic input validation

**Production Recommendations:**

- Implement password hashing (BCrypt, PBKDF2, etc.)
- Add SSL/TLS encryption for secure file transfers
- Implement user authentication tokens
- Add file integrity verification (checksums)
- Network access controls and firewall configuration

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**

   - Check MySQL server is running
   - Verify database credentials in DatabaseManager.java
   - Ensure MySQL Connector JAR is in lib/ directory

2. **Compilation Errors**

   - Verify JDK is installed and JAVA_HOME is set
   - Check MySQL Connector JAR is present in lib/
   - Ensure all source files are present

3. **File Transfer Issues**

   - Check firewall settings allow connections on port 8888
   - Verify peer IP addresses are correct
   - Ensure sufficient disk space for file transfers

4. **GUI Issues**
   - Verify Java Swing is supported on your system
   - Check display settings and screen resolution
   - Try running with different Java versions

### Error Messages

- "Failed to connect to database" - Check MySQL configuration
- "Port already in use" - Change the default port or stop conflicting applications
- "Connection refused" - Verify the peer is online and port is accessible
- "File not found" - Check file permissions and path

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is created for educational purposes. Feel free to use and modify as needed.

## 📞 Support

For questions, issues, or contributions:

- Create an issue in the repository
- Contact the development team
- Check the troubleshooting section above

---

**Note:** This application is designed for educational and demonstration purposes. For production use, implement proper security measures, error handling, and performance optimizations.
