package com.example.samuraitravel.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.UserEditForm;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	private final UserRepository userRepository;
	private final UserService userService;
	
	public UserController(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}
	
	// メソッド：詳細情報の表示
	@GetMapping
	// Spring Securityが提供する@AuthenticationPrincipal：このアノテーションを引数につけ、現在ログイン中のユーザー情報を取得できます
	// ・@AuthenticationPrincipalを付ける引数はuserDetailsインターフェイスを実装したクラスのオブジェクト
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		// ログイン中のuser情報の閲覧(メソッドの引数で受け取る)
		// ただし、UserDetailsはログイン時の情報しか入っていないため、ユーザーIDを取得し、getReference()に渡すことで最新情報が取得できる
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		
		model.addAttribute("user", user);
		
		return "user/index";
	}
	
	// 元情報の表示
	@GetMapping("/edit")	
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		// userDetailsImplの保持情報をedit.htmlに渡す。　getReferenceById()で情報を最新にする。
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		// すでに情報が登録されているため、UserEditFormをインスタンス化し、どこに何の情報を入れるかをクリアにする
		// どこに入れるかはedit.htmlの*{}で指定(表示も入力もできるthymeleaf要素)
		UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getFurigana(),user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail());
		
		// 
		model.addAttribute("userEditForm", userEditForm);
		
		return "user/edit";
	}
	
	// 情報更新メソッド：エラーがなければ更新情報をuserService(Repositoryを使ってデータ更新)に送信、エラーがあればビューに表示する
	@PostMapping("/update")
	public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		// メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
		if(userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}
		
		if(bindingResult.hasErrors()) {
			return "user/edit";
		}
		
		userService.update(userEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
		
		return "redirect:/user";
	}
	
}
