# IP-based Email OTP Rate Limiting Implementation

## Overview

This implementation provides IP-based rate limiting for email OTP requests using Bucket4j to prevent abuse, email bombing, and resource exhaustion attacks.

## Features

- **IP-based Rate Limiting**: Limits requests per IP address
- **Configurable Limits**: Adjustable request limits and time windows
- **Bucket4j Implementation**: Uses proven token bucket algorithm
- **Proxy Support**: Handles various proxy headers (X-Forwarded-For, etc.)
- **Graceful Degradation**: Fail-open approach when rate limiting fails
- **Comprehensive Logging**: Detailed logging for monitoring and debugging

## Configuration

### Application Properties

```properties
# Rate Limiting Configuration
app.rate-limit.otp.max-requests=5
app.rate-limit.otp.window-minutes=15
```

### Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `app.rate-limit.otp.max-requests` | `5` | Maximum OTP requests per IP per window |
| `app.rate-limit.otp.window-minutes` | `15` | Time window in minutes |

## Bucket4j Implementation

### Why Bucket4j?

**Advantages:**
- Battle-tested library with proven algorithms
- Token bucket algorithm provides smooth rate limiting
- Built-in optimizations and performance improvements
- Less code to maintain
- Well-documented and actively maintained
- Handles burst traffic gracefully

### Algorithm Details

**Token Bucket Algorithm:**
- Each IP gets a bucket with a fixed number of tokens
- Each request consumes one token
- Tokens are refilled at a fixed rate over time
- Allows for burst handling while maintaining overall rate limits

## API Endpoints

### Send OTP with Rate Limiting

```http
POST /api/otp/send
Content-Type: application/json

{
    "email": "user@example.com"
}
```

**Success Response (200):**
```json
{
    "success": true,
    "message": "OTP sent successfully."
}
```

**Rate Limited Response (429):**
```json
{
    "success": false,
    "message": "Rate limit exceeded. Please try again in 300 seconds.",
    "timeUntilReset": 300
}
```

### Other OTP Endpoints

- `POST /api/otp/send-if-locked` - Send OTP only if account is locked
- `POST /api/otp/send-activate` - Send OTP for account activation
- `POST /api/otp/send-forgot` - Send OTP for password reset

All endpoints now include rate limiting.

## IP Address Detection

The system automatically detects client IP addresses from various headers:

1. `X-Forwarded-For`
2. `X-Real-IP`
3. `X-Client-IP`
4. `CF-Connecting-IP` (Cloudflare)
5. `True-Client-IP` (Akamai)
6. And many more...

This ensures accurate IP detection even behind proxies, load balancers, or CDNs.

## Rate Limiting Algorithm

### Token Bucket (Bucket4j)
- Uses a token bucket algorithm
- Tokens are refilled at a fixed rate
- Provides smooth rate limiting with burst handling
- Each IP gets 5 tokens every 15 minutes
- Tokens can be consumed immediately or saved for bursts

## Monitoring and Logging

### Log Levels
- **INFO**: Successful OTP sends and verifications
- **WARN**: Rate limit exceeded, invalid OTP attempts
- **ERROR**: Failed OTP sends, system errors
- **DEBUG**: IP address detection details

### Key Metrics to Monitor
- Rate limit violations per IP
- OTP send success/failure rates
- Response times for OTP endpoints
- Memory usage (for in-memory buckets)

## Security Considerations

### 1. Fail-Open Strategy
- If rate limiting fails, requests are allowed
- Prevents service disruption due to rate limiting issues
- Logs errors for investigation

### 2. IP Spoofing Protection
- Validates IP address format
- Handles multiple proxy headers
- Falls back to remote address if headers are invalid

### 3. Configuration Security
- Rate limits should be reasonable (not too strict, not too lenient)
- Monitor for abuse patterns
- Adjust limits based on legitimate usage patterns

## Performance Considerations

### In-Memory Storage
- Uses ConcurrentHashMap for thread-safe storage
- Each bucket consumes ~200 bytes per IP
- Automatic cleanup of expired buckets
- Suitable for single-instance deployments

### Memory Management
- Buckets are created on-demand
- No explicit cleanup needed (Bucket4j handles internally)
- Memory usage scales with unique IP addresses

## Testing

### Manual Testing
```bash
# Test rate limiting
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/otp/send \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com"}'
done
```

### Expected Behavior
- First 5 requests: 200 OK
- Subsequent requests: 429 Too Many Requests
- After 15 minutes: Reset to 5 requests

## Troubleshooting

### Common Issues

1. **Rate limiting not working**
   - Check if Bucket4j dependency is included
   - Verify IP address detection
   - Check logs for errors

2. **False positives**
   - Adjust rate limit values
   - Check if legitimate users are behind shared IPs
   - Consider whitelisting certain IPs

3. **Performance issues**
   - Monitor memory usage
   - Check for memory leaks
   - Consider distributed deployment for multiple instances

### Debug Mode
Enable debug logging:
```properties
logging.level.com.template.util.IpAddressUtil=DEBUG
logging.level.com.template.service.impl.Bucket4jRateLimitService=DEBUG
```

## Best Practices

1. **Start Conservative**: Begin with strict limits and adjust based on usage
2. **Monitor Regularly**: Track rate limit violations and adjust accordingly
3. **User Communication**: Provide clear error messages with retry information
4. **Graceful Degradation**: Ensure service remains available even if rate limiting fails
5. **Documentation**: Keep rate limit policies transparent to users

## Dependencies

The implementation requires the following dependency in `pom.xml`:

```xml
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.7.0</version>
</dependency>
```

## Future Enhancements

1. **Distributed Rate Limiting**: Use Redis for multi-instance deployments
2. **Dynamic Rate Limiting**: Adjust limits based on user behavior
3. **Geographic Rate Limiting**: Different limits for different regions
4. **User-based Rate Limiting**: Additional limits per user account
5. **Rate Limit Analytics**: Dashboard for monitoring rate limit usage 