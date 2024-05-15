package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.UserRepository;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	
	private final UserRepository userRepository;
	
	public AdminUserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@GetMapping
	// メソッド：対象データ検索、ページネーション、
	// name = "keyword"：HTTPリクエストのパラメータの名前を指定
	// エラーを出さない（パラメタを渡さない状態）ようにするためには、required = false にする。
	public String index(@RequestParam(name = "keyword", required = false) String keyword, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
		// フィールド変数設定
		Page<User> userPage;
		
		if(keyword != null && !keyword.isEmpty()) {
			// UserRepositoryのメソッドを実装：検索
			userPage = userRepository.findByNameLikeOrFuriganaLike("%" + keyword + "%", "%" + keyword + "%", pageable);
		}else {
			userPage = userRepository.findAll(pageable);
		}
		
		model.addAttribute("userPage", userPage);
		model.addAttribute("keyword", keyword);
		
		return "admin/users/index";
	}
	
	@GetMapping("/{id}")
	// name = "id"で指定されたidの変数を取得し、"Integer id"に格納する
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		// 取得したid番号の情報をRepositoryで取得し、変数userに代入する
		User user = userRepository.getReferenceById(id);
		
		// 変数userに格納した情報をビューに渡す
		model.addAttribute("user", user);
		
		return "admin/users/show";
	}

}
