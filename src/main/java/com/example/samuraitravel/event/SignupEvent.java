package com.example.samuraitravel.event;

import org.springframework.context.ApplicationEvent;

import com.example.samuraitravel.entity.User;

import lombok.Getter;

@Getter	// 外部（具体的にはListenerクラス）からイベントに関する情報を取得できるようにゲッターを定義
// ApplicationEventクラスを継承：イベントを作成するための基本的なクラス
// ・イベントのソース(発生源)などを保持する
// ・Listenerクラスにイベント発生を知らせる
public class SignupEvent extends ApplicationEvent{
	
	// イベントに関する情報を保持する
	private User user;			// ユーザー情報の登録
	private String requestUrl;	// リクエストを受けたURLの登録
	
	public SignupEvent(Object source, User user, String requestUrl) {
		super(source);	// 親クラスのコンストラクタを呼び出し、イベントのソース(Pulisherクラスのインスタンス)を渡す
		
		this.user = user;
		this.requestUrl = requestUrl;
	}

}
