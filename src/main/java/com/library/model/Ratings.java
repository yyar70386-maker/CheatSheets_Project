package com.library.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ratings {
    private int id;
    private int userId;
    private int sheetId;
    private int rating;
    private Timestamp createdAt;
    private String username;
}
