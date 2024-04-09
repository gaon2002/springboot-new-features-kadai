package com.example.samuraitravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class HomeController {
	// HTTPリクエストのGETメソッドをそのメソッドにマッピング(対応づけ)できる
	// 引数にはマッピングするルートパス(ドメイン名を省略したパス)を指定する
     @GetMapping("/")
     public String index() {
    	 //呼び出すビューの名前をreturnで返す。　拡張子は省略する。
         return "index";
     }   

}
