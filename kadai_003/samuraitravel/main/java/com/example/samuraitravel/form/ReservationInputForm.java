package com.example.samuraitravel.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
	@NotBlank(message = "チェックイン日とチェックアウト日を選択してください。")
	// 「2023-05-01 から 2023-05-05」と表示したいので、文字列型を指定
	private String fromCheckinDateToCheckoutDate;
	
	@NotNull(message = "宿泊人数を入力してください")
	@Min(value = 1, message = "宿泊人数は1人以上に設定してください。")
	private Integer numberOfPeople;
	
	// チェックイン日の取得
	// FlatpickrというJavaScript製のライブラリを利用し、以下のようにカレンダーによる日付入力機能を作成
	public LocalDate getCheckinDate() {
		// 「から」という文字を区切りにして、チェックイン日とチェックアウト日をsplit()で分割
		String[] checkinDateAndCheckoutDate = getFromCheckinDateToCheckoutDate().split(" から ");
		// parse()メソッドでLocalDate型に変換して返す
		return LocalDate.parse(checkinDateAndCheckoutDate[0]);
	
	}
	
	// チェックアウト日の取得
	public LocalDate getCheckoutDate() {
		String[] checkinDateAndCheckoutDate = getFromCheckinDateToCheckoutDate().split(" から ");
		return LocalDate.parse(checkinDateAndCheckoutDate[1]);
	
	}	
	

}
