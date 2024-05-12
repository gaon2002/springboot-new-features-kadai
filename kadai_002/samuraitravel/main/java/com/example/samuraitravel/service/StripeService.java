package com.example.samuraitravel.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.form.ReservationRegisterForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {
	// Spring FrameworkやSpring BootなどのJavaベースのフレームワークで使用されるアノテーション
	// ・Springの依存性注入機能を使用して、外部の構成ファイルや環境変数から値を注入するために使用される
	// ・"${stripe.api-key}"：外部の構成ファイルや環境変数から取得したい値を指定するプレースホルダー
	// ・stripe.api-key：プロパティキーで、外部の構成ファイル（例えば、application.propertiesやapplication.yml）や環境変数にこのキーに対応する値が設定されていることを期待している
	// 		Springは自動的にstripe.api-keyに対応する値を取得し、@Valueアノテーションが付けられたフィールドやメソッドの引数に注入する
	@Value("${stripe.api-key}")
	// stripeApiKeyフィールドに外部の構成から取得されたAPIキーを注入する
	private String stripeApiKey;
	
	private final ReservationService reservationService;
	
	public StripeService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}
		
	// createStripeSession()メソッド：Stripeに送信する支払い情報をセッションとして作成
	public String createStripeSession(String houseName, ReservationRegisterForm reservationRegisterForm, HttpServletRequest httpServletRequest) {
		Stripe.apiKey = stripeApiKey;
		// ブラウザから入力されたリクエストを受け取る
		String requestUrl= new String(httpServletRequest.getRequestURL());
		SessionCreateParams params =
				SessionCreateParams.builder()
					// 決済方法（カード）
					.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
					// 
					.addLineItem(
							SessionCreateParams.LineItem.builder()
								.setPriceData(
									SessionCreateParams.LineItem.PriceData.builder()
										.setProductData(
											SessionCreateParams.LineItem.PriceData.ProductData.builder()
												.setName(houseName)
												.build())
										.setUnitAmount((long)reservationRegisterForm.getAmount())
										.setCurrency("jpy")
										.build())
								.setQuantity(1L)
								.build())
					.setMode(SessionCreateParams.Mode.PAYMENT)
					// requestUrl：ブラウザから入力されたリクエストの登録が成功したら指定のURLにリダイレクトする
					// ・支払いが成功したら、指定されたURLから /houses/数字/reservations/confirm という部分を削除し、その後に /reservations?reserved を追加したURLにリダイレクトする
					.setSuccessUrl(requestUrl.replaceAll("/houses/[0-9]+/reservations/confirm", "") + "/reservations?reserved")
					.setCancelUrl(requestUrl.replace("/reservations/confirm", ""))
					// 支払情報を各フィールドにセット
					.setPaymentIntentData(
							SessionCreateParams.PaymentIntentData.builder()
								.putMetadata("houseId", reservationRegisterForm.getHouseId().toString())
								.putMetadata("userId", reservationRegisterForm.getUserId().toString())
								.putMetadata("checkinDate", reservationRegisterForm.getCheckinDate())
								.putMetadata("checkoutDate", reservationRegisterForm.getCheckoutDate())
								.putMetadata("numberOfPeople", reservationRegisterForm.getNumberOfPeople().toString())
								.putMetadata("amount", reservationRegisterForm.getAmount().toString())
								.build())
					.build();
		try {
			Session session = Session.create(params);
			return session.getId();
		}catch(StripeException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	// セッションから予約情報を取得し、ReservationServiceクラスを介してデータベースに登録する
	// ・Eventオブジェクト：Stripeから通知されるイベントの内容を表現したオブジェクト
	public void processSessionCompleted(Event event) {
		// StripeObject：StripeのAPIから返されるデータを表現する基本的なオブジェクト
		// Optionalクラス：nullを持つ可能性のあるオブジェクトを扱うためのクラス
		// ・ラムダ式の引数にはオブジェクトの実際の値が代入される変数名を渡す
		// ・この変数は｛｝内ので利用できる
		Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
		optionalStripeObject.ifPresent(stripeObject -> {
			Session session = (Session)stripeObject;
			// "Payment_intgent"情報を展開する(詳細情報を含める)要指定したSessionRetriveParamsオブジェクトを生成
			SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("payment_intent").build();
			
			try {
				// Sessionオブジェクトから取得したセッションIDとSessionRetriveParamオブジェクトを渡し、支払情報を含む詳細なセッション情報を取得
				session = Session.retrieve(session.getId(),params, null);
				// さらに、取得した詳細なセッション情報からメタデータ（予約情報）を取り出し、
				Map<String, String> paymentIntentObject = session.getPaymentIntentObject().getMetadata();
				// それをpaymentObjectとして、ReservationServiceクラスのcreate()メソッドに渡している(DB更新)
				reservationService.create(paymentIntentObject);
			}catch(StripeException e) {
				e.printStackTrace();
			}
		});
	}

}
