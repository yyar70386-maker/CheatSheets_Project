package com.library.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditLogs {
    private int id;
    private Integer userId;
    private String username;
    private String action;
    private String entityName;
    private Integer entityId;
    private Timestamp createdAt;
}
