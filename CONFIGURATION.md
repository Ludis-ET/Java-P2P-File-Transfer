# P2P File Transfer System Configuration

## Environment Variables Configuration

The application supports configuration through environment variables using a `.env` file. This is the recommended way to configure the application for security and flexibility.

### Setup Instructions

1. **Copy the example file:**

   ```bash
   cp .env.example .env
   ```

2. **Edit the .env file with your configuration:**
   ```bash
   nano .env
   ```

### Configuration Options

#### Database Configuration

```env
# Database connection URL
DB_URL=jdbc:mysql://localhost:3306/p2p_system

# Database username
DB_USER=your_username

# Database password
DB_PASSWORD=your_password

# JDBC driver class
DB_DRIVER=com.mysql.cj.jdbc.Driver
```

#### Application Configuration

```env
# Default port for P2P file transfers
DEFAULT_PORT=8888

# Maximum file size in bytes (default: 1GB)
MAX_FILE_SIZE=1073741824

# Connection timeout in milliseconds
CONNECTION_TIMEOUT=10000

# Buffer size for file transfers
BUFFER_SIZE=8192

# Directory for downloaded files
DOWNLOADS_DIRECTORY=downloads
```

#### Server Configuration

```env
# Server host (for future use)
SERVER_HOST=localhost

# Server port (for future use)
SERVER_PORT=8888
```

### Configuration Priority

The application loads configuration in the following order (highest to lowest priority):

1. **System Environment Variables** - Set via `export VAR_NAME=value`
2. **`.env` file** - Local environment file
3. **`config.properties` file** - Legacy configuration file
4. **Default values** - Built-in fallback values

### Security Best Practices

1. **Never commit .env files to version control:**

   ```bash
   echo ".env" >> .gitignore
   ```

2. **Use strong passwords for database access**

3. **Restrict file permissions on .env:**

   ```bash
   chmod 600 .env
   ```

4. **Use different configurations for different environments:**
   - `.env.development`
   - `.env.production`
   - `.env.testing`

### Examples

#### Development Setup (Local MySQL)

```env
DB_URL=jdbc:mysql://localhost:3306/p2p_system_dev
DB_USER=dev_user
DB_PASSWORD=dev_password123
DEFAULT_PORT=8888
DOWNLOADS_DIRECTORY=downloads_dev
```

#### Production Setup (Remote MySQL)

```env
DB_URL=jdbc:mysql://production-server:3306/p2p_system
DB_USER=prod_user
DB_PASSWORD=super_secure_password
DEFAULT_PORT=8888
DOWNLOADS_DIRECTORY=/var/app/downloads
MAX_FILE_SIZE=2147483648
```

#### Testing Setup

```env
DB_URL=jdbc:mysql://localhost:3306/p2p_system_test
DB_USER=test_user
DB_PASSWORD=test_password
DEFAULT_PORT=9999
DOWNLOADS_DIRECTORY=test_downloads
```

### Environment Variables in Different Operating Systems

#### Linux/macOS

```bash
# Set environment variable
export DB_PASSWORD=mypassword

# Run application with environment variable
DB_PASSWORD=mypassword ./run.sh
```

#### Windows

```cmd
# Set environment variable
set DB_PASSWORD=mypassword

# Run application
run.bat
```

#### Windows PowerShell

```powershell
# Set environment variable
$env:DB_PASSWORD = "mypassword"

# Run application
.\run.bat
```

### Troubleshooting

1. **Configuration not loading:**

   - Check if `.env` file exists in the project root
   - Verify file permissions
   - Check for syntax errors in .env file

2. **Database connection issues:**

   - Verify database credentials in .env file
   - Ensure MySQL server is running
   - Check database URL format

3. **Port conflicts:**
   - Change DEFAULT_PORT in .env file
   - Check if port is already in use: `netstat -an | grep 8888`

### Migration from config.properties

If you're migrating from the old `config.properties` system:

1. Create a `.env` file using the template
2. Copy your settings from `config.properties` to `.env`
3. Update property names to match the new format:
   - `default.port` → `DEFAULT_PORT`
   - `db.url` → `DB_URL`
   - `db.user` → `DB_USER`
   - etc.

The application will automatically use `.env` values with fallback to `config.properties` for backward compatibility.
