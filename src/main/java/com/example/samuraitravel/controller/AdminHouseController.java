package com.example.samuraitravel.controller;

//ページネーションを実現するクラス
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//ページの並べ替えを実現するクラス
import org.springframework.data.domain.Sort.Direction;
//ページリンクを実現するクラス
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
// 検索機能
import org.springframework.web.bind.annotation.RequestParam;

// DBフィールドとビュー・アプリ側のフィールドを関連付けるファイル
import com.example.samuraitravel.entity.House;
// フォームビューで入力されたデータを受け取るファイル
import com.example.samuraitravel.form.HouseRegisterForm;
// DBをCRUD処理するリポジトリーファイル
import com.example.samuraitravel.repository.HouseRepository;



// 管理者用　民宿ページ


@Controller		// コントローラー(モデルとビューを制御する。ユーザー、モデル、ビューの橋渡し。)

// @RequestMappinngでこのコントローラー内に書くメソッドが担当するURLは("")で指定したものになる
// 各メソッドに共通のパス（今回の場合は「/admin/houses」）を繰り返し記述する必要がなくなる
@RequestMapping("/admin/houses")	// 下に記述される＠GetMappingはファイル名を直接書くだけでよくなる

public class AdminHouseController {
	// コンストラクタインジェクション
	
	// ➀フィールド設定：依存先のオブジェクトをfinalで宣言することで一度初期化された後は変更されない(安全性が向上する)
	// HouseRepository(インターフェースなのでインスタンスを生成できない)はJPAが提供するJpaRepositoryインターフェースを継承しており、
	// このインターフェースを継承することで、Spring Data JPA側でHouseRepositoryインターフェースを実装した
	// 「プロキシ（代理）」と呼ばれる特殊なクラスを自動作成する。
	// このプロキシのインスタンスをDIコンテナに登録することで依存性の注入を実現。
	private final HouseRepository houseRepository;
	
	// ➁コンストラクター
	// DIコンテナに登録されたインスタンスをコンストラクタに対して依存性(DI)注入する(コンストラクタインジェクション)
	// AdminHouseControllerはHouseRepositoryに依存している
	
	public AdminHouseController(HouseRepository houseRepository) {
		this.houseRepository = houseRepository;
	}
	
	
	@GetMapping
	// findAll()で全ての民宿データを取得し、ビューにデータを渡す
	// コントローラからビューにデータを渡す場合、モデルクラスを使う
	// メソッド内にModel型の引数、メンバー変数modelを指定
	// Pagable、Pageインターフェースを使ってページネーションを実現する、引数にPageable型を設定
	
	// 引数に@RequestParamアノテーションをつける
	public String index(Model model, @PageableDefault(page=0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, @RequestParam(name = "keyword", required = false) String keyword) {
		// HouseRepositoryのインスタンスはコンストラクタインジェクションによって、すでに提供されているので、
		// index()メソッド内でインスタンス生成は不要。
		// 全てのデータを表示させる場合は、findAll()にPageableオブジェクトを渡す
		// Page<House> housePage = houseRepository.findAll(pageable);
		
		Page<House> housePage;	//フィールド設定
		
		// フィールドの初期化：ifを使って、keywordがある場合とない場合で設定を分ける		
		// keywordパラメターが存在するかどうかで処理を分ける
		// keywordパラメターが存在する場合、部分一致を行う
		if(keyword != null && !keyword.isEmpty()) {
			housePage = houseRepository.findByNameLike("%" + keyword + "%", pageable);
		}else{
			housePage = houseRepository.findAll(pageable);
		}
		
		// Modelクラスを使ってViewとコントローラーのデータの橋渡しを作る
		// 第１引数：ビュー側から参照する変数名("housePage")
		// 第２引数：ビューに渡すデータ(housePage)
		model.addAttribute("housePage", housePage); //ページネーションの時にhosePageに変更、houseのままではだめでしたっけ？
		// Modelクラスを使ってViewとコントローラーのデータの橋渡しを作る
		// 第１引数：ビュー側から参照する変数名("housePage")
		// 第２引数：ビューに渡すデータ(housePage)
		model.addAttribute("keyword", keyword);
		
		// 出力したデータをビューに返す
		return "admin/houses/index";
	}
	
	// メソッド：民宿の詳細ページを表示する
	@GetMapping("/{id}")	//URLにあるidの値を取得する
	// 引数に@PathVariableアノテーションを設定：URLの一部をその引数にバインドする（割り当てる）ことができる
	// URLを変数のように扱い、コントローラー内でその値(以下のメソッドの場合"id")を利用することが可能
	public String show(@PathVariable(name = "id") Integer id, Model model){
		
		// URLのidと一致する民宿データを１つ取得する
		// データ取得するために、getReferenceById()メソッドを使う
		House house = houseRepository.getReferenceById(id);
		
		// 取得した民宿データとビューの引数と関連付け
		// Modelクラスを使ってViewとコントローラーのデータの橋渡しを作る
		// 第１引数：ビュー側から参照する変数名("housePage")
		// 第２引数：ビューに渡すデータ(housePage)
		model.addAttribute("house", house);
		
		// 出力したデータをビューに返す
		return "admin/houses/show";
	}
	
	// メソッド：ビューで入力されたデータをHouseRegisterForで受け取る
	@GetMapping("/register")	//@GetMappingはメソッドとGETの処理を行うURLを紐づける役割。
	public String register(Model model) {
		
		// Modelクラスを使ってViewとコントローラーのデータの橋渡しを作る
		// 第１引数：ビュー側から参照する変数名("houseRegistrationForm")
		// 第２引数：ビューに渡すデータはフォームクラスのインスタンスを渡すことになるため、newを行っている
		//　　フォームクラスを利用するにはコントローラーからビューにそのインスタンスを渡す必要がある
		//　　利用する=ビューのフォーム入力項目とフォームクラスのフィールドを関連付けること
		model.addAttribute("houseRegistrationForm", new HouseRegisterForm());
		
		return "admin/houses/register";
		
	}
}
