package com.example.samuraitravel.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.entity.VerificationToken;
import com.example.samuraitravel.event.SignupEventPublisher;
import com.example.samuraitravel.form.SignupForm;
import com.example.samuraitravel.service.UserService;
import com.example.samuraitravel.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
	
	private final UserService userService;
	private final SignupEventPublisher signupEventPublisher;
	private final VerificationTokenService verificationTokenService;
	
	public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, VerificationTokenService verificationTokenService) {
		this.userService = userService;
		this.signupEventPublisher = signupEventPublisher;
		this.verificationTokenService = verificationTokenService;
	}
	
	// auth/loginにアクセスされたときにメソッドが実行される
	@GetMapping("/login")	//HTTPリクエストのGETメソッドをこのメソッドに紐づける
	public String login() {
		return "auth/login";
	}
	
	@GetMapping("/signup")
	public String signup(Model model){
		model.addAttribute("signupForm", new SignupForm());
		return "auth/signup";
		
	}
	
	// 会員登録(signup)画面から入力(post)された情報を処理
	@PostMapping("/signup")
	// SignupFormにエラーが発生する場合、アノテーションで設定したバリデーションエラーをsignup.htmlに表示する
	// 　・@ModelAttribute：フォームから送信されたデータ(フォームクラス インスタンス)をその引数にバインドすることで、フォームのデータを参照する
	// 　・エラーが発生したらBindingResultオブジェクトに格納される
	// 　・BindingResult型 引数： メソッドにこの引数を設定することで、バリデーションエラー内容がその引数に格納される
	// 　・RedirectAttributes redirectAttributes：リダイレクト先にエラー内容を表示する
	// 引数でHttpServletRequestオブジェクトを受け取る：HTTPリクエストに関する情報の取得を可能にする
	public String signup(@ModelAttribute @Validated SignupForm signupForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest){
		// 以下は、上記のエラー表示設定に加え、独自のバリデーションを作成してエラー表示させるメソッド
		// メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
		if(userService.isEmailRegistered(signupForm.getEmail())) {
			// すでにエラー内容が設定されているので、どこに何のエラーを表示させるのか、オブジェクトをインスタンス化して設定
			// FirldErrorに渡す引数；
//			　・第1引数：エラー内容を格納するオブジェクト名
//			　・第2引数：エラーを発生させるフィールド名
//			　・第3引数：エラーメッセージ			
			FieldError fieldError = new FieldError(bindingResult.getObjectName(),"email", "すでに登録済みのメールアドレスです。");
			// addError()メソッドにエラー内容を渡し、BindingResultオブジェクトに独自のエラー内容を追加できる
			bindingResult.addError(fieldError);
		}
		
		// パスワードとパスワード（確認用）の入力が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
		if(!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(),"password", "パスワードが一致しません。");
			bindingResult.addError(fieldError);
		}
		
		if(bindingResult.hasErrors()) {
			return "auth/signup";
		}
		
		
		User createdUser = userService.create(signupForm);
		// getRequestURL()でリクエストURL(https://ドメイン名/signup)を取得
		String requestUrl = new String(httpServletRequest.getRequestURL());
		signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
		redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");
		
		
		return "redirect:/";
	}
	
	// イベントを発行する　(メール認証用URL)
	@GetMapping("/signup/verify")
	// @RequestParamでURLの指定する値(name = "token")を取得し、引数に設定
	public String verify(@RequestParam(name = "token") String token, Model model) {
		
		// メールでクリックされたURLのtokenでverification_tokensテーブルの検索結果を変数に代入(なければ、null)
		VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
		
		// 取得したtokenがverification_tokensテーブルにあるか
		if(verificationToken != null) {
			User user = verificationToken.getUser();
			// 対象のユーザーのenabledを有効にする
			userService.enableUser(user);
			String successMessage = "会員登録が完了しました";
			model.addAttribute("successMessage", successMessage);
		}else {
			String errorMessage = "トークンが無効です";
			model.addAttribute("errorMessage", errorMessage);
		}
			
			
		return "auth/verify";
	}
	
	

}
