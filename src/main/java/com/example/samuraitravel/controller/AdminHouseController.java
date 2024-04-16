package com.example.samuraitravel.controller;


//ページネーションを実現するクラス
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//ページの並べ替えを実現するクラス
import org.springframework.data.domain.Sort.Direction;
//ページリンクを実現するクラス
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
// Modelオブジェクト: Springが用意するMapオブジェクトで、Viewに渡すオブジェクトを設定。
// Model: クライアント側のリクエスト情報に含まれるブラウザからの情報(沢山ある中から選べるようにする)
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// 検索機能
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// DBフィールドとビュー・アプリ側のフィールドを関連付けるファイル
import com.example.samuraitravel.entity.House;
// 民宿データの更新で入力されたデータを受け取るファイル
import com.example.samuraitravel.form.HouseEditForm;
// フォームビューで入力されたデータを受け取るファイル
import com.example.samuraitravel.form.HouseRegisterForm;
// DBをCRUD処理するリポジトリーファイル
import com.example.samuraitravel.repository.HouseRepository;
// Serviceクラス
import com.example.samuraitravel.service.HouseService;



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
	
	private final HouseService houseService;
	
	// ➁コンストラクター
	// DIコンテナに登録されたインスタンスをコンストラクタに対して依存性(DI)注入する(コンストラクタインジェクション)
	
	// AdminHouseControllerはHouseRepositoryに依存している

	public AdminHouseController(HouseRepository houseRepository, HouseService houseService) {
		this.houseRepository = houseRepository;
		this.houseService = houseService;
	}
	
	
	@GetMapping		// パスを書かなくてよい
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
	
	// メソッド：ビューで入力されたデータをHouseRegisterFormで受け取る
	@GetMapping("/register")	//@GetMappingはメソッドとGETの処理を行うURLを紐づける役割。
	public String register(Model model) {
		
		// Modelクラスを使ってViewとコントローラーのデータの橋渡しを作る
		// 第１引数：ビュー側から参照する変数名("houseRegistrationForm")
		// 第２引数：ビューに渡すデータはフォームクラスのインスタンスを渡すことになるため、newを行っている
		//　　フォームクラスを利用するにはコントローラーからビューにそのインスタンスを渡す必要がある
		//　　利用する=ビューのフォーム入力項目とフォームクラスのフィールドを関連付けること
		model.addAttribute("houseRegisterForm", new HouseRegisterForm());
		
		return "admin/houses/register";
		
	}
	
	
	@PostMapping("/create")	// HTTPリクエストのPOSTメソッド送信先パスをメソッドに指定する
	
	// メソッド：フォームの入力内容をcreate.htmlに返す
	// @ModelAttribute：フォームから送信されたデータ(フォームクラス インスタンス)をその引数にバインドすることで、フォームのデータを参照することができる
	// @Validated： 引数(フォームクラス インスタンス)に対してバリデーションができる
	// エラーが発生したらBindingResultオブジェクトに格納される
	// BindingResult型 引数： メソッドにこの引数を設定することで、バリデーションエラー内容がその引数に格納される
	public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
			// if文の条件式にBindingResultインタフェースが提供するhasErrors()メソッドを使う
		if (bindingResult.hasErrors()) {
			// エラーが存在する場合は民宿登録ページを表示する
			return "admin/houses/register";
		}
		
		// エラーがなければ、houseServiceのcreate()メソッドを実行し、民宿を登録する
		houseService.create(houseRegisterForm);
		// redirectAttributesインタフェースのaddFlashAttribute()メソッド：リダイレクト先にデータを渡す
		// 以下はリダイレクトさせるときにメッセージを表示させるケース：
		// 　第1引数：リダイレクト先から参照する変数名
		// 　第2引数：リダイレクト先に渡すデータ
		redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました");
		// 民宿一覧にリダイレクトする
		// リダイレクト：WebサイトやページのURLを変更した際、古いURLにアクセスしたユーザーを自動的に新しいURLに転送すること
		return "redirect:/admin/houses";
	}
	
	@GetMapping("/{id}/edit")
	
	// メソッド：民宿データを編集する（edit.htmlへデータを渡すまで）
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		// URLのidと一致する民宿データを取得する
		// getReferenceById()メソッドの引数にidを使い、該当する民宿データを取得し、変数houseに代入
		House house= houseRepository.getReferenceById(id);
		
		// 民宿画像のファイル名(画像ファイル)を取得する
		String imageName = house.getImageName();
		
		// Editフォームクラスをインスタンス化する
		HouseEditForm houseEditForm = new HouseEditForm(house.getId(), house.getName(), null, house.getDescription(), house.getPrice(), house.getCapacity(), house.getPostalCode(), house.getAddress(), house.getPhoneNumber());
				
		// 民宿画像のファイル名をビューに渡す　(ファイル名を渡せばよいので、MultpartFile型のファイルを直接渡す必要はない)
		model.addAttribute("imageName", imageName);
		// インスタンス化したhouseEditFormデータをビュー(edit.html)に渡す
		model.addAttribute("houseEditForm", houseEditForm);
		
		return "admin/houses/edit";
		
	}
	
	@PostMapping("/{id}/update")	// HTTPリクエストのPOSTメソッド送信先パスをメソッドに指定する
	
	// メソッド：編集されたデータを送信し、DB更新する
	// @ModelAttribute：フォームから送信されたデータ(フォームクラス インスタンス)をその引数にバインドすることで、フォームのデータを参照することができる
	// @Validated： 引数(フォームクラス インスタンス)に対してバリデーションができる
	// エラーが発生したらBindingResultオブジェクトに格納される
	// BindingResult型 引数： メソッドにこの引数を設定することで、バリデーションエラー内容がその引数に格納される
	public String update(@ModelAttribute @Validated HouseEditForm houseEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
			// if文の条件式にBindingResultインタフェースが提供するhasErrors()メソッドを使う
		if (bindingResult.hasErrors()) {
			// エラーが存在する場合は民宿登録ページを表示する
			return "admin/houses/edit";
		}
		
		// エラーがなければ、houseServiceのcreate()メソッドを実行し、民宿を登録する
		houseService.update(houseEditForm);
		// redirectAttributesインタフェースのaddFlashAttribute()メソッド：リダイレクト先にデータを渡す
		// 以下はリダイレクトさせるときにメッセージを表示させるケース：
		// 　第1引数：リダイレクト先から参照する変数名
		// 　第2引数：リダイレクト先に渡すデータ
		redirectAttributes.addFlashAttribute("successMessage", "民宿情報を編集しました");
		// 民宿一覧にリダイレクトする
		// リダイレクト：WebサイトやページのURLを変更した際、古いURLにアクセスしたユーザーを自動的に新しいURLに転送すること
		return "redirect:/admin/houses";
	}
	
	// 民宿データの削除（DBからも削除する）
	@PostMapping("/{id}/delete")
	// index.htmlで削除が選択されたデータのidを引数で受け取る
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
//		houseRepositoryを使ってデータのCRUD処理を行う、deleteById(受け取った引数)メソッドで削除
		houseRepository.deleteById(id);
		
		redirectAttributes.addFlashAttribute("successMessage", "民宿を削除しました。");
		
		return "redirect:/admin/houses";
	}
	
}
