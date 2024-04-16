package com.example.samuraitravel.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Role;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.SignupForm;
import com.example.samuraitravel.form.UserEditForm;
import com.example.samuraitravel.repository.RoleRepository;
import com.example.samuraitravel.repository.UserRepository;

@Service
public class UserService {
//	Spring Frameworkにおける依存性注入(Dependency Injection)の概念を利用
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
//		UserServiceのコンストラクターで初期化
//		コンストラクターの引数にUserRepository、RoleRepository、PasswordEncoderを受け取り、それぞれの値をフィールドにセット
//		クラス内で直接インスタンスを生成せずに、外部からの依存関係を注入する方法。
//		これにより、UserServiceが外部のリソースやサービスと疎結合になり、テストや拡張性が向上する。
		this.userRepository = userRepository;
		this.roleRepository =roleRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	// メソッド：フォームから入力された情報をUserエンティティに登録
	public User create(SignupForm signupForm) {
		// エンティティ(Userクラス)をインスタンス化し、値をセット
		User user = new User();
		
		// Roleフィールドに設定するため、ロール名が"ROLE_GENERAL"のRoleオブジェクト（Roleエンティティのオブジェクト）を取得
		Role role = roleRepository.findByName("ROLE_GENERAL");
		
		// signup.htmlから受け取った値をゲッターで取得
		// セッターを使ってエンティティー(Userクラス)の各フィールドにセットする
        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
        //パスワードはHash化してからセット
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        // RoleフィールドにRoleオブジェクトをセット
        user.setRole(role);
        // メール認証まではenabledフィールドにfalseをデフォルトでセット
        user.setEnabled(false); 
        
        // userテーブルの保存
        return userRepository.save(user);
	}
	
	@Transactional
	public void update(UserEditForm userEditForm) {
		User user = userRepository.getReferenceById(userEditForm.getId());
		
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPostalCode(userEditForm.getPostalCode());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());
        
        // userテーブルの保存
        userRepository.save(user);
	}
	
	
	
	// メソッド：メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegistered(String email) {
		// フォームに入力されたメールアドレスが存在するかどうかを検索し、アドレスがあればuserに代入される
		User user = userRepository.findByEmail(email);
		// メールアドレスが存在していたら、trueを返す
		return user != null;
	}
	
	// パスワードとパスワード（確認用）の入力が一致するかを判断する
	public boolean isSamePassword(String password, String passwordConfirmation) {
		// passwordとpasswordConfirmationが一致すれば、trueを返す
		return password.equals(passwordConfirmation);
	}
	
	// ユーザーを有効にする
	@Transactional
	public void enableUser(User user) {
		// enabledカラムをtrueにする
		user.setEnabled(true);
		// userテーブル保存
		userRepository.save(user);
	}
	
	// メールアドレスが変更されたかどうかをチェックする
	public boolean isEmailChanged(UserEditForm userEditForm) {
		User currentUser = userRepository.getReferenceById(userEditForm.getId());
		return !userEditForm.getEmail().equals(currentUser.getEmail());
	}

}
