package com.example.samuraitravel.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.samuraitravel.entity.User;

@Component	// コントローラなど、イベントを発生させたい処理（例：AuthControllerのsignup()メソッド）の中で呼び出して使う
public class SignupEventPublisher {
	
	// @ComponentアノテーションをつけてDIコンテナに登録し、呼び出すクラス（ここはコントローラ）に対して依存性の注入（DI）を行う
	private final ApplicationEventPublisher applicationEventPublisher;
	
	public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	// メソッド：イベントの発行　⇒イベントを発行させたいタイミングで、publishSignupEvent()メソッドを呼び出せばよい
	// ApplicationEventPublisherインターフェースが提供するpublishEvent()メソッドを使用
	// 　・引数には発行したいEventクラス（今回はSignupEventクラス）のインスタンスを渡す
	public void publishSignupEvent(User user, String requestUrl) {
		applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
	}

}
