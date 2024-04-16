package com.example.samuraitravel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.entity.VerificationToken;
import com.example.samuraitravel.repository.VerificationTokenRepository;

@Service
public class VerificationTokenService {
	// フィールド設定(依存性の注入：リポジトリのオブジェクトを利用する。依存先のオブジェクトをfinalで初期化(安全性の向上)。）
	private final VerificationTokenRepository verificationTokenRepository;
	
	// 初期化(コンストラクタインジェクションを使う場合@Autowiredを使う、コンストラクタが１つの場合は省略)
	// DIコンテナに登録されたインスタンスをコンストラクタなどに対して提供（注入）するのが@Autowiredアノテーションの役割。
	// DIコンテナ：@Controllerや@Service、@Componentなどのアノテーションがついたクラスのインスタンスを生成し、DIコンテナと呼ばれる領域にそれらのインスタンスを登録する仕組み
	public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {

		this.verificationTokenRepository = verificationTokenRepository;
	}
	
	
	@Transactional
	public void create(User user, String token) {
		VerificationToken verificationToken = new VerificationToken();
		
		verificationToken.setUser(user);
		verificationToken.setToken(token);
		
		// verification_tokensテーブルにUUIDから生成したデータを保存
		verificationTokenRepository.save(verificationToken);
	}
	
	// トークンの文字列で検索した結果を返す
	public VerificationToken getVerificationToken(String token) {
		// verification_tokensテーブルから対象トークンを検索して返す
		return verificationTokenRepository.findByToken(token);
	}
}
