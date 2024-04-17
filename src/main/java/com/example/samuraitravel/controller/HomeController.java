package com.example.samuraitravel.controller;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.repository.HouseRepository;

@Controller

public class HomeController {
	private final HouseRepository houseRepository;
	
	public HomeController(HouseRepository houseRepository) {
		this.houseRepository = houseRepository;
	}
	// HTTPリクエストのGETメソッドをそのメソッドにマッピング(対応づけ)できる
	// 引数にはマッピングするルートパス(ドメイン名を省略したパス)を指定する
     @GetMapping("/")
     public String index(Model model) {
    	 
    	 List<House> newHouses = houseRepository.findTop10ByOrderByCreatedAtDesc();
    	 model.addAttribute("newHouses", newHouses);
    			 
    	 //呼び出すビューの名前をreturnで返す。　拡張子は省略する。
         return "index";
     }   

}
