package isucon9.qualify;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import isucon9.qualify.data.DataService;
import isucon9.qualify.dto.ErrorResponse;
import isucon9.qualify.dto.LoginRequest;
import isucon9.qualify.dto.SettingResponse;
import isucon9.qualify.dto.User;
import isucon9.qualify.web.SessionService;

@RestController
public class HomeController {

    private final SessionService sessionService;
    private final DataService dataService;

    public HomeController(SessionService sessionService, DataService dataService) {
        this.sessionService = sessionService;
        this.dataService = dataService;
    }

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping("/settings")
    public SettingResponse getSettings() {
        SettingResponse s = new SettingResponse();
        s.setCsrfToken(sessionService.getCsrfToken());
        getUser().ifPresent(user -> s.setUser(user));
        s.setPaymentServiceUrl(dataService.getPaymentServiceURL());
        s.setCategories(dataService.getCategories());
        return s;
    }

    private Optional<User> getUser() {
        return dataService.getUserById(sessionService.getUserId());
    }

    @PostMapping("/login")
    public User postLogin(@RequestBody @Validated LoginRequest login) {
        Optional<User> row = dataService.getUserByAccountName(login.getAccountName());
        if (!row.isPresent()) {
            throw new ApiException("アカウント名かパスワードが間違えています", HttpStatus.UNAUTHORIZED);
        }
        User u = row.get();
        String plaintext = login.getPassword();
        String hashed = new String(u.getHashedPassword(), StandardCharsets.UTF_8);
        if (!BCrypt.checkpw(plaintext, hashed)) {
            throw new ApiException("アカウント名かパスワードが間違えています", HttpStatus.UNAUTHORIZED);
        }
        sessionService.setUserId(u.getId());
        sessionService.setCsrfToken(secureRandomStr(20));
        return u;
    }

    private String secureRandomStr(int byteLength) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[byteLength];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(byteLength * 2);
        for (int i = 0; i < byteLength; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        ErrorResponse body = new ErrorResponse();
        body.setError(e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleJsonMappingException(MethodArgumentNotValidException e) {
        ErrorResponse body = new ErrorResponse();
        body.setError("all parameters are required");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ErrorResponse body = new ErrorResponse();
        body.setError("json decode error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
