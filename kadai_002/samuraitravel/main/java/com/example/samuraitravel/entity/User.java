package com.example.samuraitravel.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity		// usersテーブルと紐づけるエンティティであることを機能させる
@Table(name = "users")	// 対応づける（参照する）テーブル名を指定
@Data
public class User {
	@Id													// 主キーに@Idを付けることで指定
	@GeneratedValue(strategy = GenerationType.IDENTITY)	// 主キーが自動採番されるようになる
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "furigana")
	private String furigana;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@ManyToOne						// @ManyToOne：多対一のフィールドに付けるアノテーション
									//	この場合、ひとつのidに何名もの人がぶら下がっている
	@JoinColumn(name = "role_id")	// @JoinColumn：外部キーのカラム名(role_id)を指定
	private Role role;				// フィールドは相手エンティティのクラス型(Role型)にする
	
	@Column(name = "enabled")
	private Boolean enabled;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;

}