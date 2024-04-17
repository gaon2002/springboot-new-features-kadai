package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;

// リポジトリ(Model処理の一部でデータベースにアクセスし、CRUD処理を行うインターフェース)
// JpaRepositoryインターフェースを継承することで、基本的なCRUD操作を行うためのメソッドが利用可能。

// housesテーブルとやり取りするリポジトリ、ジェネリクスにHouse型(エンティティ)を入れることで関連付けできる
public interface HouseRepository extends JpaRepository<House, Integer>{
	
	// メソッド設定<Entityの型>（インターフェース内に記述するので、抽象メソッド：引数の型・戻り値の型を設定）
	public Page<House> findByNameLike(String keyword, Pageable pageable);
	
	// OrderByキーワード+Desc/Ascで並べ替えができる
	public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);
	public Page<House> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);
	
	public Page<House> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
	public Page<House> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);
	// LessThanEqual：<=
	public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
	public Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);
	
	public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);
	public Page<House> findAllByOrderByPriceAsc(Pageable pageable);
	

	public List<House> findTop10ByOrderByCreatedAtDesc();

	

}
