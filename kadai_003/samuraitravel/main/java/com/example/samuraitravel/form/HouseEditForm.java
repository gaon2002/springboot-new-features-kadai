package com.example.samuraitravel.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

// 民宿更新用データ(フォームクラス)

@Data
@AllArgsConstructor		// 全フィールドに値をセットするための引数付きコンストラクタを自動生成できる
//	コンストラクタが必要な理由：
//		新規にデータを登録するフォームクラスはコントローラーに空のインスタンスを生成し、渡すだけでよい
//		更新の場合はすでに存在するデータを更新するので、どのデータを更新するかという情報が必要
//		UI向上のため、現行データを表示したいので、各フィールドに更新前の値がセットされたインスタンスを生成しビューに渡すため
//		そのため、全フィールドに値をセットするための引数付きコンストラクタが必要となっている
public class HouseEditForm {

	// 編集用のフォームクラスではどのデータを更新するか明確にするため、id用のフィールド設定も必要
	// ※HouseRegistraterFormクラスにはidを設定していない
	@NotNull
	private Integer id;
	
	@NotBlank(message = "民宿名を入力してください")
	private String name;
	
	private MultipartFile imageFile;
	
	@NotBlank(message = "説明を入力してください")
	private String description;
	
	@NotNull(message = "宿泊料金を入力してください")
	@Min(value = 1, message = "宿泊料金は1円以上に設定してください")
	private Integer price;
	
	@NotNull(message = "定員を入力してください")
	@Min(value = 1, message = "定員は1任以上に設定してください")
	private Integer capacity;
	
	@NotBlank(message = "郵便番号を入力してください")
	private String postalCode;
	
	@NotBlank(message = "住所を入力してください")
	private String address;
	
	@NotBlank(message = "電話番号を入力してください")
	private String phoneNumber;
		
}
