package com.example.samuraitravel.service;

import java.io.IOException;
import java.nio.file.Files;
// 特定のファイルやディレクトリのパスを表現するためのクラス。
import java.nio.file.Path;
// Pathクラスのインスタンス生成を補助するクラス。
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.form.HouseEditForm;
import com.example.samuraitravel.form.HouseRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;


@Service	// Serviceクラスとして機能するように指定
public class HouseService {
	
	private final HouseRepository houseRepository;
	
	public HouseService(HouseRepository houseRepository) {
		this.houseRepository = houseRepository;
	}
	
	@Transactional	// Transactionalアノテーションをつけることで、以下のメソッドをトランザクション化できる
	
	// メソッド：データの登録処理。　データベースの操作(更新、変更、取り消し)を行う。
	// 		引数にフォームクラスのインスタンスをセットし、register.htmlから受け取った値を取得できるようにする。
	public void create(HouseRegisterForm houseRegisterForm) {
		// エンティティ(Houseクラス)をインスタンス化し、値をセット
		House house = new House();
		
		
		// register.htmlから受け取った値をゲッターで取得、MultipartFileクラス型の変数imageFileに代入
		   MultipartFile imageFile = houseRegisterForm.getImageFile();
		
		// 送信された画像ファイルをstorageに保存する
		if(!imageFile.isEmpty()) {
			// 元のファイル名を取得する　getOriginalFileName()
			String imageName = imageFile.getOriginalFilename();
			// ファイル名の変更処理(ファイル名が重複しないように、generateNewFileName()で自動起生)
			String hashedImageName = generateNewFileName(imageName);
			
			// コピーを保管するパスを設定、Pathクラス型の変数filePathに代入
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			// ファイルのコピー処理
			copyImageFile(imageFile, filePath);
			// セッターを使って、エンティティにファイル名をセットする
			house.setImageName(hashedImageName);
		}
		
		// register.htmlから受け取った値をゲッターで取得
		// セッターを使ってエンティティー(Houseクラス)の各フィールドにセットする
		house.setName(houseRegisterForm.getName());
		house.setDescription(houseRegisterForm.getDescription());
		house.setPrice(houseRegisterForm.getPrice());
		house.setCapacity(houseRegisterForm.getCapacity());
		house.setPostalCode(houseRegisterForm.getPostalCode());
		house.setAddress(houseRegisterForm.getAddress());
		house.setPhoneNumber(houseRegisterForm.getPhoneNumber());
		
		// save()：エンティティを保存する
		houseRepository.save(house);
		
	}
	
@Transactional	// Transactionalアノテーションをつけることで、以下のメソッドをトランザクション化できる
	
	// メソッド：データの更新処理。　データベースの操作(更新、変更、取り消し)を行う。
	// 		引数にEditフォームクラスのインスタンスをセットし、edit.htmlから受け取った値を取得できるようにする。
	public void update(HouseEditForm houseEditForm) {
		// idを使って更新するエンティティを取得し、値をセット
		// RepositoryのCRUD処理で、DBから情報を取得
		House house = houseRepository.getReferenceById(houseEditForm.getId());
				
		// register.htmlから受け取った値をゲッターで取得、MultipartFileクラス型の変数imageFileに代入
		   MultipartFile imageFile = houseEditForm.getImageFile();
		
		// 送信された画像ファイルをstorageに保存する
		if(!imageFile.isEmpty()) {
			// 元のファイル名を取得する　getOriginalFileName()
			String imageName = imageFile.getOriginalFilename();
			// ファイル名の変更処理(ファイル名が重複しないように、generateNewFileName()で自動起生)
			String hashedImageName = generateNewFileName(imageName);
			
			// コピーを保管するパスを設定、Pathクラス型の変数filePathに代入
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			// ファイルのコピー処理
			copyImageFile(imageFile, filePath);
			// セッターを使って、エンティティにファイル名をセットする
			house.setImageName(hashedImageName);
		}
		
		// register.htmlから受け取った値をゲッターで取得
		// セッターを使ってエンティティー(Houseクラス)の各フィールドにセットする
		house.setName(houseEditForm.getName());
		house.setDescription(houseEditForm.getDescription());
		house.setPrice(houseEditForm.getPrice());
		house.setCapacity(houseEditForm.getCapacity());
		house.setPostalCode(houseEditForm.getPostalCode());
		house.setAddress(houseEditForm.getAddress());
		house.setPhoneNumber(houseEditForm.getPhoneNumber());
		
		// save()：エンティティを保存する
		houseRepository.save(house);
		
	}
	
	// UUIDを使ってファイル名を別名に変更する(ファイル名の重複を回避する)。　生成したファイル名を～～に返す
	// ファイル名をUUIDで別名に変更する処理はgenerateNewFileName()メソッドに任せている
		public String generateNewFileName(String fileName) {
			// split()：文字列を指定した区切り文字で分割する
			String[] fileNames = fileName.split("\\.");
			for ( int i = 0; i < fileNames.length -1; i++) {
				// UUIDで重複しないファイル名を生成している
				fileNames[i] = UUID.randomUUID().toString();
			}
			// join()メソッド：指定した区切り文字で複数の文字列を結合する。
			String hashedFileName = String.join(".", fileNames);
			// 
			return hashedFileName;
			
		}
		
		//System.out.println(hashedFileName);
	
	// 画像ファイルを指定したファイルにコピーする
		public void copyImageFile(MultipartFile imageFile, Path filePath) {
			try {
				// JavaのFileクラスが提供しているcopy()メソッドでコピー
				// 保存先はfilePath(上記でオブジェクト生成済)：コピー処理は上記のcopyImageFile()に任せている
				// 第1引数に指定したファイルを第2引数に指定したパスにコピーする。
				// getInputStream()：ファイルの内容を読み取るためのInputStreamオブジェクトを取得する。
				Files.copy(imageFile.getInputStream(), filePath);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	
}
