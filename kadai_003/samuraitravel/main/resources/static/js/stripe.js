	// Stripeの公開可能キー
	const stripe = Stripe('pk_test_51P766BRuhzAsDRtmgYgNH7PCdnH2s60NEpcqhOf0ASkHMXZMGmF3nGFp5hYysebhJuZWknbSzVvjDw4q6yJMl5Gl00gNbyej8x');
	// 確定ボタンの変数設定
	const paymentButton = document.querySelector('#paymentButton');
 
 	paymentButton.addEventListener('click', () => {
//		 StripeのredirectToCheckout()を実行する
   		stripe.redirectToCheckout({
//			Stripeに対してセッションIDを渡す：JavaScriptからサーバーサイドにある支払いセッションの情報を受け取り、Stripeに渡すことができます。
    		sessionId: sessionId
   })
 });