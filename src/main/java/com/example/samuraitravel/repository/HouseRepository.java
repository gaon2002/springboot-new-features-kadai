package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;

// リポジトリ(Model処理の一部でデータベースにアクセスし、CRUD処理を行うインターフェース)

// JpaRepositoryインターフェースを継承することで、基本的なCRUD操作を行うためのメソッドが利用可能。
public interface HouseRepository extends JpaRepository<House, Integer>{
	
	// メソッド設定（インターフェース内に記述するので、抽象メソッド：引数の型・戻り値の型を設定）
	public Page<House> findByNameLike(String keyword, Pageable pageable);

}
