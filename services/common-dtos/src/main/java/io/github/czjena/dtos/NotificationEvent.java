package io.github.czjena.dtos;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String eventType;
    private String recipientUserId;
    private Map<String, String> contextData;

}