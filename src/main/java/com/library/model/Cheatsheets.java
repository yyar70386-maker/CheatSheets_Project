package com.library.model;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
public class Cheatsheets {
	private int id;
	private String title;
	private String content;

	// category_id ကို categoryId လို့ ပြောင်းပါ
	private int categoryId;

	// author_id ကို authorId လို့ ပြောင်းပါ
	private int authorId;

	private int downloadCount;
	private int status = 1;
	private Timestamp createdAt;

	// UI မှာ Category နာမည်ပြဖို့အတွက်
	private String categoryName;
	private String authorName;
	private int authorRole;
	private double averageRating;
	private int ratingCount;

	public boolean isAdminAuthored() {
		return authorRole == 1;
	}
}
