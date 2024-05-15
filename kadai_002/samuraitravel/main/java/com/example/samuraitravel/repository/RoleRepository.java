package com.example.samuraitravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Role;

// rolesテーブルとやり取りするリポジトリ、ジェネリクスにRole型(エンティティ)を入れることで関連付けできる
public interface RoleRepository extends JpaRepository<Role, Integer>{
	// ロール名でロールを検索するメソッド
	public Role findByName(String name);


}
