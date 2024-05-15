package com.example.samuraitravel.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.service.VerificationTokenService;

@Component	// ListenerクラスのインスタンスがDIコンテナに登録されるようにする
			//	・@EventListenerアノテーションがついたメソッドをSpring Boot側が自動的に検出し、イベント発生時に実行
public class SignupEventListener {
	private final VerificationTokenService verificationTokenService;
	private final JavaMailSender javaMailSender;
	
	public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
		this.verificationTokenService = verificationTokenService;
		this.javaMailSender = mailSender;
	}
	
	@EventListener	// イベント発生時に実行したいメソッドに付けるアノテーション
	// メソッド：SignupEventクラスを引数にしているので、SignupEventクラスから通知を受けたときにonSignupEvent()が実行される
	private void onSignupEvent(SignupEvent signupEvent) {
		User user = signupEvent.getUser();
		// トークンをUUIDで生成
		String token = UUID.randomUUID().toString();
		// ユーザーIDとトークンをデータベースに保持する
		verificationTokenService.create(user, token);
		
		String recipientAddress = user.getEmail();
		String subject = "メール認証";
		// 生成したトークンをメール認証用のURLに埋め込む
		String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;
		String message = "以下のリンクをクリックして会員登録を完了してください";
		
		// メール内容を作成
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		// 送信先のメールアドレスをセット
		mailMessage.setTo(recipientAddress);
		// 件名をセット
		mailMessage.setSubject(subject);
		// 本文をセット：メールのメッセージ内に生成したトークンが埋め込まれたURLを記載（https://ドメイン名/signup/verify?token=生成したトークン
		mailMessage.setText(message + "\n" + confirmationUrl);
		// メールを送信：javaMailSenderインターフェースのsend()メソッドに前述のSimpleMailMessageオブジェクトを渡す
		javaMailSender.send(mailMessage);
	}
	

}
