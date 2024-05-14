package com.example.samuraitravel.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Reservation;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReservationRepository;
import com.example.samuraitravel.repository.UserRepository;

@Service
public class ReservationService {
	
	private final ReservationRepository reservationRepository;
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;
	
	public ReservationService(ReservationRepository reservationRepository, HouseRepository houseRepository, UserRepository userRepository) {
		this.reservationRepository = reservationRepository;
		this.houseRepository = houseRepository;
		this.userRepository = userRepository;
	}
	
	@Transactional
	//ReservationRegisterFormオブジェクトの代わりにpaymentIntentObjectというMap型のオブジェクトを受け取る
	// ・paymentIntentObject：支払情報のメタデータが含まれているオブジェクト
	public void create(Map<String, String> paymentIntentObject) {
		// オブジェクト(インスタンス)生成
		Reservation reservation = new Reservation();
		// paymentIntentObjectのフィールド変数設定
		// ・Stripe ServiceクラスのcreateStripeSession()で設定したメタデータ
		// ・get()メソッドにメタデータ名を入れることでその値を取得することができる
		// ・メタデータ名と値をセットにしてセッションを作成しておくことでイベント通知を受け取ったタイミングでオブジェクトから取り出せる
		Integer houseId = Integer.valueOf(paymentIntentObject.get("houseId"));
		Integer userId = Integer.valueOf(paymentIntentObject.get("userId"));
		
		//	reservationRegisterFormではなく、paymentIntentObjectから呼び出す
		House house = houseRepository.getReferenceById(houseId);
		User user = userRepository.getReferenceById(userId);
		LocalDate checkinDate = LocalDate.parse(paymentIntentObject.get("checkinDate"));
		LocalDate checkoutDate = LocalDate.parse(paymentIntentObject.get("checkoutDate"));
		Integer numberOfPeople = Integer.valueOf(paymentIntentObject.get("numberOfPeople"));
		Integer amount = Integer.valueOf(paymentIntentObject.get("amount"));
		
		// Reservationenエンティティに送信し、reservationテーブルに登録するセッターメソッド
		reservation.setHouse(house);
		reservation.setUser(user);
		reservation.setCheckinDate(checkinDate);
		reservation.setCheckoutDate(checkoutDate);
		reservation.setNumberOfPeople(numberOfPeople);
		reservation.setAmount(amount);
		
		// reservationテーブルを保存
		reservationRepository.save(reservation);		
	}
	
	
	
	// 宿泊人数が定員以下かどうかをチェックする
	public boolean isWithinCapacity(Integer numberOfPeople, Integer capacity) {
		return numberOfPeople <= capacity;
		
	}
	
	// 宿泊料金を計算する
	// ChronoUnit.DAYS.between()メソッドを使い、2つの日付（LocalDate型）を使って宿泊日数を計算
	public Integer calculateAmount(LocalDate checkinDate, LocalDate checkoutDate, Integer price) {
		long numberOfNights = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
		int amount = price * (int)numberOfNights;
		return amount;
	
	}
	
}
