package co.com.crediya.api.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private T data;
    private String timestamp;

    public ApiResponse(int status, T data) {
        this.status = status;
        this.data = data;
        this.timestamp = Instant.now().toString();
    }

    public ApiResponse() {}


}