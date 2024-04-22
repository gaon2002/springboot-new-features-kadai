package com.example.samuraitravel.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Reservation;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.form.ReservationRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReservationRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReservationService;
import com.example.samuraitravel.service.StripeService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReservationController {
	private final ReservationRepository reservationRepository;
	private final HouseRepository houseRepository;
	private final ReservationService reservationService;
	private final StripeService stripeService;
	
	public ReservationController(ReservationRepository reservationRepository, HouseRepository houseRepository, ReservationService reservationService, StripeService stripeService) {
		this.reservationRepository = reservationRepository;
		this.houseRepository = houseRepository;
		this.reservationService = reservationService;
		this.stripeService = stripeService;
	}
	
	
	@GetMapping("/reservations")
	// @AuthenticationPrincipalアノテーションを使用し、認証されたユーザーの情報をUserDetailsオブジェクトとして受け取る。
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {

		// userDetailsImpl.getUser()で、Userエンティティ全体を保持し情報にアクセスできるようになる
		User user = userDetailsImpl.getUser();
		Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
		
		model.addAttribute("reservationPage", reservationPage);
		
		return "reservations/index";
	}
	
	// 予約内容の確認データ作成　⇒　confirm.htmlに渡す
	@GetMapping("/houses/{id}/reservations/input")
	public String input(@PathVariable(name = "id") Integer id,
						@ModelAttribute @Validated ReservationInputForm reservationInputForm,
						BindingResult bindingResult,
						RedirectAttributes redirectAttributes,
						Model model)
	{
		House house = houseRepository.getReferenceById(id);
		Integer numberOfPeople = reservationInputForm.getNumberOfPeople();
		Integer capacity = house.getCapacity();
		
		if(numberOfPeople != null) {
			// 定員確認：ReservationService のisWithinCapacity()がfalseであれば
			if(!reservationService.isWithinCapacity(numberOfPeople, capacity)) {
				FieldError fieldError = new FieldError(bindingResult.getObjectName(), "numberOfPeople", "宿泊人数が定員を超えています。");
				bindingResult.addError(fieldError);
			}
		}
		
		// bindingResultがエラーを持っていたら
		if(bindingResult.hasErrors()) {
			model.addAttribute("house", house);
			model.addAttribute("errorMessage", "予約内容に不備があります。");
			return "houses/show";
		}
		
		// input()メソッド：入力フォームに問題がなければ、予約内容の確認ページ(confirm())にデータを渡す
		redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);
		
		return "redirect:/houses/{id}/reservations/confirm";
	
		
	}
	
	// 予約内容の確定　⇒　index.htmlにリダイレクト、reservationテーブルにデータ登録
	@GetMapping("/houses/{id}/reservations/confirm")
	public String confirm(@PathVariable(name = "id") Integer id,
						  @ModelAttribute ReservationInputForm reservationInputForm,
						  @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						  HttpServletRequest httpServletRequest,
						  Model model)
	{
		// 民宿情報全体を取得(house_idでSELECT)
		House house = houseRepository.getReferenceById(id);
		// 会員情報全体を取得(user_idでSELECT)
		User user = userDetailsImpl.getUser();
		
		
		// チェックイン日とチェックアウト日を取得する：calculateAmount()引数設定
		LocalDate checkinDate = reservationInputForm.getCheckinDate();
		LocalDate checkoutDate = reservationInputForm.getCheckoutDate();
		// 宿泊料金を取得：calculateAmount()引数設定
		Integer price = house.getPrice();
		
		// 宿泊料金を計算する
		// ReservationServiceのcalculateAmount()を実行
		Integer amount = reservationService.calculateAmount(checkinDate,checkoutDate,price);
		
		// 民宿情報やユーザー情報、ReservationInputFormオブジェクトを取得し、それらの情報を渡したReservationRegisterFormオブジェクトを生成
		ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(house.getId(), user.getId(), checkinDate.toString(), checkoutDate.toString(), reservationInputForm.getNumberOfPeople(), amount);
		
		// StripeServiceで作成したcreateStripeSession()を実行し、sessionIdに情報を格納
		String sessionId = stripeService.createStripeSession(house.getName(), reservationRegisterForm, httpServletRequest);
		
		model.addAttribute("house", house);
		model.addAttribute("reservationRegisterForm", reservationRegisterForm);
		model.addAttribute("sessionId", sessionId);
		
		return "reservations/confirm";
	}
	
	/*
	@PostMapping("houses/{id}/reservations/create")
	public String create(@ModelAttribute ReservationRegisterForm reservationRegisterForm) {
		reservationService.create(reservationRegisterForm);
		
		予約一覧にリダイレクトするメソッド
		return "redirect:/reservations?reserved";
	}
	*/

}
