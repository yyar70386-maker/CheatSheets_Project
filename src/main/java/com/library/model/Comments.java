package com.library.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comments {
    private int id;
    private int userId;
    private int sheetId;
    private Integer parentId;
    private String commentText;
    private int status; // 1 = Active, 0 = Hidden (ထပ်တိုးထားသော field)
    private Timestamp createdAt;
    private String username;
    private String sheetTitle;
    private String parentUsername;
    private int likeCount;
    private String userReaction;
}