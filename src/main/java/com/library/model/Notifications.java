package com.library.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notifications {
    private int id;
    private int userId;
    private String message;
    private boolean read;
    private Timestamp createdAt;
    private String type;
    private String linkUrl;
}
