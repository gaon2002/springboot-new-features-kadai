package com.example.samuraitravel.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity					// housesテーブルと紐づけるエンティティーとして機能させる
@Table(name = "houses")	// 対応づける（参照する）テーブル名を指定
@Data					// ゲッターやセッターを自動生成する(Lombokが提供する機能)
public class House {
	@Id													// 主キーには@Idを付けることで指定できる
	@GeneratedValue(strategy = GenerationType.IDENTITY)	// 主キーが自動採番されるようになる
	@Column(name = "id")		// フィールド変数に対応付けるテーブルのカラム名を指定
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "image_name")
	private String imageName;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "price")
	private Integer price;
	
	@Column(name = "capacity")
	private Integer capacity;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
	
	

}
