package com.example.samuraitravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.User;

// usersテーブルとやり取りするリポジトリ、ジェネリクスにUser型(エンティティ)を入れることで関連付けできる
public interface UserRepository extends JpaRepository<User, Integer>{
	
	// ユーザー名で検索する
	public User findByEmail(String email);


}
