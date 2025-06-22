# Refresh Token Implementation Guide

## What is a Refresh Token?

A **refresh token** is a special authentication token used to obtain new access tokens without requiring the user to log in again. This implementation provides enhanced security through:

### Key Features:
- **Short-lived Access Tokens**: 15 minutes (configurable)
- **Long-lived Refresh Tokens**: 7 days (configurable)
- **Token Rotation**: New refresh token issued on each refresh
- **Database Storage**: Refresh tokens stored securely in database
- **Device Tracking**: Each refresh token tracks device/IP information
- **Automatic Cleanup**: Expired tokens are automatically cleaned up

## Architecture Components

### 1. Enhanced JWT Utility (`JwtUtil`)
- Generates both access and refresh tokens
- Validates token types (ACCESS vs REFRESH)
- Extracts token information

### 2. Refresh Token Entity (`RefreshToken`)
- Stores refresh tokens in database
- Tracks expiration, device info, and revocation status

### 3. Refresh Token Service (`RefreshTokenService`)
- Manages refresh token lifecycle
- Implements token rotation
- Handles cleanup and validation

### 4. Updated Authentication Controller (`AuthController`)
- Enhanced login endpoint
- New refresh token endpoint
- Improved logout functionality

## API Endpoints

### 1. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "message": "Login successful",
  "accessTokenExpiresIn": 900
}
```

### 2. Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "deviceInfo": "Mobile App v1.0"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "message": "Token refreshed successfully",
  "accessTokenExpiresIn": 900
}
```

### 3. Logout (Single Device)
```http
POST /api/auth/logout
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 4. Logout All Devices
```http
POST /api/auth/logout-all
Authorization: Bearer {accessToken}
```

## Client-Side Implementation Examples

### JavaScript/TypeScript Example

```typescript
class AuthService {
  private accessToken: string | null = null;
  private refreshToken: string | null = null;

  async login(username: string, password: string): Promise<boolean> {
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });

      if (response.ok) {
        const data = await response.json();
        this.accessToken = data.accessToken;
        this.refreshToken = data.refreshToken;
        
        // Store tokens securely (consider using secure storage)
        localStorage.setItem('accessToken', this.accessToken);
        localStorage.setItem('refreshToken', this.refreshToken);
        
        return true;
      }
      return false;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  }

  async refreshTokens(): Promise<boolean> {
    if (!this.refreshToken) return false;

    try {
      const response = await fetch('/api/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          refreshToken: this.refreshToken,
          deviceInfo: navigator.userAgent 
        })
      });

      if (response.ok) {
        const data = await response.json();
        this.accessToken = data.accessToken;
        this.refreshToken = data.refreshToken;
        
        localStorage.setItem('accessToken', this.accessToken);
        localStorage.setItem('refreshToken', this.refreshToken);
        
        return true;
      }
      return false;
    } catch (error) {
      console.error('Token refresh failed:', error);
      return false;
    }
  }

  async apiCall(url: string, options: RequestInit = {}): Promise<Response> {
    // Add access token to request
    options.headers = {
      ...options.headers,
      'Authorization': `Bearer ${this.accessToken}`
    };

    let response = await fetch(url, options);

    // If token expired, try to refresh
    if (response.status === 401) {
      const refreshSuccess = await this.refreshTokens();
      if (refreshSuccess) {
        // Retry with new token
        options.headers['Authorization'] = `Bearer ${this.accessToken}`;
        response = await fetch(url, options);
      }
    }

    return response;
  }
}
```

### Mobile App (React Native Example)

```typescript
import AsyncStorage from '@react-native-async-storage/async-storage';

class MobileAuthService {
  async login(username: string, password: string): Promise<boolean> {
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });

      if (response.ok) {
        const data = await response.json();
        
        // Store tokens securely
        await AsyncStorage.setItem('accessToken', data.accessToken);
        await AsyncStorage.setItem('refreshToken', data.refreshToken);
        
        return true;
      }
      return false;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  }

  async refreshTokens(): Promise<boolean> {
    try {
      const refreshToken = await AsyncStorage.getItem('refreshToken');
      if (!refreshToken) return false;

      const response = await fetch('/api/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          refreshToken,
          deviceInfo: `Mobile App - ${Platform.OS}` 
        })
      });

      if (response.ok) {
        const data = await response.json();
        await AsyncStorage.setItem('accessToken', data.accessToken);
        await AsyncStorage.setItem('refreshToken', data.refreshToken);
        return true;
      }
      return false;
    } catch (error) {
      console.error('Token refresh failed:', error);
      return false;
    }
  }
}
```

## Security Considerations

### Best Practices Implemented:

1. **Token Rotation**: New refresh token issued on each refresh
2. **Database Storage**: Refresh tokens stored in database for revocation
3. **Expiration Tracking**: Both JWT expiration and database expiration
4. **Device Tracking**: Tracks device/IP for security monitoring
5. **Rate Limiting**: Max refresh tokens per user (configurable)
6. **Automatic Cleanup**: Scheduled cleanup of expired tokens

### Client-Side Security:

1. **Secure Storage**: Store tokens in secure storage (not localStorage in production)
2. **HTTPS Only**: Always use HTTPS in production
3. **Token Validation**: Check token expiration before API calls
4. **Logout Handling**: Properly clear tokens on logout

## Configuration Options

### Application Properties:
```properties
# Access token expires in 15 minutes
jwt.access-token.expiration=900000

# Refresh token expires in 7 days  
jwt.refresh-token.expiration=604800000

# Maximum refresh tokens per user
auth.max-refresh-tokens-per-user=5
```

## Error Handling

### Common Error Responses:

1. **Invalid Refresh Token**:
```json
{
  "accessToken": null,
  "refreshToken": null,
  "username": null,
  "message": "Invalid refresh token",
  "accessTokenExpiresIn": 0
}
```

2. **Expired Refresh Token**:
```json
{
  "accessToken": null,
  "refreshToken": null,
  "username": null,
  "message": "Refresh token is expired",
  "accessTokenExpiresIn": 0
}
```

## Benefits of This Implementation

1. **Enhanced Security**: Short-lived access tokens reduce attack surface
2. **Better UX**: No frequent re-logins required
3. **Scalability**: Database-backed token management
4. **Flexibility**: Configurable expiration times
5. **Monitoring**: Device tracking and audit capabilities
6. **Spring Security Integration**: Works seamlessly with existing security

## Migration from Simple JWT

If you're upgrading from a simple JWT setup:

1. Existing access tokens will continue to work
2. Update client applications to handle refresh tokens
3. Configure new token expiration times
4. Run database migration to create refresh_tokens table

The implementation maintains backward compatibility while adding refresh token capabilities. 