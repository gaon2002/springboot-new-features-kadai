package com.example.samuraitravel.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.samuraitravel.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;


@Controller
public class StripeWebhookController {

	private final StripeService stripeService;
	
	@Value("${stripe.api-key}")
	private String stripeApiKey;
	
	@Value("${stripe.webhook-secret}")
	private String webhookSecret;
	
	public StripeWebhookController(StripeService stripeService) {
		this.stripeService = stripeService;
		
	}
	
	// Webhookイベントの通知先を確認
	@PostMapping("/stripe/webhook")
	// Webhookイベントの通知先とコントローラの@PostMappingアノテーションの引数を一致させる必要がある
	// ・ローカル環境：localhost:8080/stripe/webhook
	// ・本番環境：https://ドメイン名/stripe/webhook
	// @RequestBody：HTTPリクエストのボディ部分に含まれるデータを、Javaオブジェクトにマッピングするために使用
	// RequestHeader：
	public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader){
		// フィールド変数設定と初期化
		Stripe.apiKey = stripeApiKey;
		Event event = null;
		
		try {
			// StripeのWebhookシグネチャの検証を実行
			// ・Stripeから受信したWebhookペイロード（イベントのデータ）、署名ヘッダー、およびWebhookシークレットを使用して、イベントを構築
			event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
		}catch(SignatureVerificationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		
		// "checkout.session.completed(決済成功)"イベントの受信
		// ・Stripeのイベントがたくさんあるため、イベントの種類が"checkout.session.completed"と等しいときという分岐を作って、
		// ・StripeServiceクラスに定義したprocessSessionCompleted()メソッドを呼び出して実行
		// ・getType()：イベントのタイプを取得するメソッド。　ここでは、上記で設定したeventのタイプを取得している
		
		if("checkout.session.completed".equals(event.getType())) {
			stripeService.processSessionCompleted(event);
		}
		
		// 予約一覧ページへのリダイレクトはStripe側でおこなってくれるので、HTTPのステータスコードを返している
		// ・HTTPのステータスコード：リクエストに対するレスポンスの状態（成功・失敗など）を3桁の数値で表したもの
		return new ResponseEntity<>("Success", HttpStatus.OK);
	}
	
}
